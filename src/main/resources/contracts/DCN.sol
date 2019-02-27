pragma solidity ^0.5.0;

/* required for transpiler */
#define TRANSPILE

/* data constants */
#define WORD_0 0   /* 32*0 = 0 */
#define WORD_1 32   /* 32*1 = 32 */
#define WORD_2 64   /* 32*2 = 64 */
#define WORD_3 96   /* 32*3 = 96 */
#define WORD_4 128  /* 32*4 = 128 */
#define WORD_5 160  /* 32*5 = 160 */
#define WORD_6 192  /* 32*6 = 192 */
#define WORD_7 224  /* 32*7 = 224 */
#define WORD_8 256  /* 32*8 = 256 */
#define WORD_9 288  /* 32*9 = 288 */
#define WORD_10 320 /* 32*10 = 320 */
#define WORD_11 352 /* 32*11 = 352 */
#define WORD_12 384 /* 32*12 = 384 */
#define WORD_13 416 /* 32*13 = 416 */

#define U64_MASK                                                 0xFFFFFFFFFFFFFFFF
#define U64_MAX                                                  0xFFFFFFFFFFFFFFFF
#define I64_MAX                                                  0x7FFFFFFFFFFFFFFF
#define I64_MIN  0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF8000000000000000
#define U256_MAX 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF

#define PRICE_UNITS 100000000

/*
 * Resolves to 1 if number cannot fit in i64.
 *
 * Valid range for i64 [-2^63, 2^64-1]
 * = [ -9,223,372,036,854,775,808, 9,223,372,036,854,775,807 ]
 * = (64 bit) [ 0x8000000000000000, 0x7fffffffffffffff ]
 * = (256 bit) = [ 0xffffffffffffffffffffffffffffffffffffffffffffffff8000000000000000, 0x7fffffffffffffff ]
 */

contract DCN {
  event UserCreated(uint64 user_id);
  event UnlockAtUpdated(uint64 user_id, uint64 exchange_id);
  event ExchangeDeposit(uint64 user_id, uint64 exchange_id, uint32 asset_id);

  /* address allowed to update self and add assets and exchanges */
  uint256 creator;
  uint256 creator_recovery;
  uint256 creator_recovery_proposed;

  /* number of users */
  uint256 user_count;

  /* number of exchanges registered */
  uint256 exchange_count;

  /* number of assets registered */
  uint256 asset_count;

  /* used to disable features in case of bug */
  uint256 security_locked_features;
  uint256 security_locked_features_proposed;
  uint256 security_proposed_unlock_timestamp;

  /* maximum values */
  #define EXCHANGE_COUNT (2**32) /* 2^32 */
  #define ASSET_COUNT (2**32)    /* 2^32 */
  #define USER_COUNT (2**64)    /* 2^64 */
  #define MARKET_COUNT (2**64)   /* 2^64 (2^32 * 2^32 every asset combination) */

  struct Exchange {
    /* 12 byte name of the exchange */
    uint96 name;
    /* address used to manage exchange */
    address owner;

    /* backup address to change the owner address */
    uint256 recovery_address;

    /* a proposed address to change recovery_address */
    uint256 recovery_address_proposed;

    /* asset balances (scaled) */
    uint256[ASSET_COUNT] balances;
  }

  struct Asset {
    /* 4 byte symbol of the asset */
    uint32 symbol;
    /* used to scale between wallet and state balances */
    uint64 unit_scale;
    /* address of the ERC-20 Token */
    address contract_address;

    /* tracks total deposits, used to recover accidental transfers */
    uint256 net_deposits;
  }

  struct MarketState {
    /* net quote balance change due to settlements */
    int64 quote_qty;
    /* net base balance change due to settlements */
    int64 base_qty;
    /* total fees used */
    uint64 fee_used;
    /* max value for fee_used */
    uint64 fee_limit;

    /* min allowed value for min_quote_qty after settlement */
    int64 min_quote_qty;
    /* min allowed value for min_base_qty after settlement */
    int64 min_base_qty;
    /* max scaled quote/base ratio when long after settlement */
    uint64 long_max_price;
    /* min scaled quote/base ratio when short after settlement */
    uint64 short_min_price;

    /* version to prevent old limits from being set */
    uint64 limit_version;
    /* how much quote_qty has been shifted by */
    int96 quote_shift;
    /* how much base_qty has been shifted by */
    int96 base_shift;
  }

  struct SessionBalance {
    /* used for exchange to sync balances with DCN (scaled) */
    uint128 total_deposit;
    /* amount given to user that will be repaid in settlement (scaled) */
    uint64 unsettled_withdraw_total;
    /* current balance of asset (scaled) */
    uint64 asset_balance;
  }

  struct ExchangeSession {
    /* timestamp used to prevent user withdraws and allow settlements */
    uint256 unlock_at;

    /* user balances locked with the exchange */
    SessionBalance[ASSET_COUNT] balances;

    /* market states to protect locked balances */
    MarketState[MARKET_COUNT] market_states;
  }

  struct User {
    /* address used to sign trading limits */
    uint256 trade_address;

    /* proposed address to update trade_address */
    uint256 trade_address_proposed;

    /* a timeout to update trade address with proposed to ensure settlements can't be blocked */
    uint256 trade_address_proposed_unlock_at;

    /* address used to withdraw funds */
    uint256 withdraw_address;

    /* address used to update trade_address / withdraw_address */
    uint256 recovery_address;

    /* proposed address to update recover_address */
    uint256 recovery_address_proposed;

    /* balances under the user's control */
    uint256[ASSET_COUNT] balances;

    /* exchange sessions */
    ExchangeSession[EXCHANGE_COUNT] exchange_sessions;
  }

  User[USER_COUNT] users;
  Asset[ASSET_COUNT] assets;
  Exchange[EXCHANGE_COUNT] exchanges;

  /* Change constants for assembly */
  #define EXCHANGE_COUNT exp(2, 32)
  #define ASSET_COUNT exp(2, 32)
  #define USER_COUNT exp(2, 64)
  #define EXCHANGE_COUNT exp(2, 64)

  constructor() public {
    assembly {
      sstore(creator_slot, caller)
      sstore(creator_recovery_slot, caller)
    }
  }

  /* utility macros */


  #define REVERT(code) \
    mstore(WORD_1, code) revert(const_add(WORD_1, 31), 1)

  #define DEBUG_REVERT(data) \
    mstore(WORD_1, data) revert(WORD_1, WORD_1)

  #define INVALID_I64(variable) \
    or(slt(variable, I64_MIN), sgt(variable, I64_MAX))

  #define MSTORE_STR(MSTORE_VAR, CONTENT_DATA, STR_LEN, STR_DATA) \
    mstore(MSTORE_VAR, CONTENT_DATA) \
    mstore(add(MSTORE_VAR, CONTENT_DATA), STR_LEN) \
    mstore(add(MSTORE_VAR, const_add(CONTENT_DATA, WORD_1)), STR_DATA)

  #define RETURN_0(VALUE) \
    mstore(return_value_mem, VALUE)

  #define RETURN(WORD, VALUE) \
    mstore(add(return_value_mem, WORD), VALUE)

  #define VALID_ASSET_ID(asset_id, REVERT_1) \
    { \
      let asset_count := sload(asset_count_slot) \
      if iszero(lt(asset_id, asset_count)) { \
        REVERT(REVERT_1) \
      } \
    } \

  #define VALID_EXCHANGE_ID(EXCHANGE_ID, REVERT_1) \
    { \
      let exchange_count := sload(exchange_count_slot) \
      if iszero(lt(EXCHANGE_ID, exchange_count)) { \
        REVERT(REVERT_1) \
      } \
    }

  /* math macros */

  #define I64_NEG_BIT 0x8000000000000000
  #define I64_TO_NEG 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000

  #define CAST_64_NEG(variable) \
      if and(variable, I64_NEG_BIT) { \
        variable := or(variable, I64_TO_NEG) \
      }

  #define I96_NEG_BIT 0x800000000000000000000000
  #define I96_TO_NEG 0xffffffffffffffffffffffffffffffffffffffff000000000000000000000000

  #define CAST_96_NEG(variable) \
      if and(variable, I96_NEG_BIT) { \
        variable := or(variable, I96_TO_NEG) \
      }

  #define U64_OVERFLOW(NUMBER) \
    gt(NUMBER, U64_MAX)

  /* pointer macros */

  #define ASSET_PTR_(ASSET_ID) \
    pointer(Asset, assets_slot, ASSET_ID)

  #define EXCHANGE_PTR_(EXCHANGE_ID) \
    pointer(Exchange, exchanges_slot, EXCHANGE_ID)

  #define EXCHANGE_BALANCE_PTR_(EXCHANGE_PTR, ASSET_ID) \
      pointer(u256, pointer_attr(Exchange, EXCHANGE_PTR, balances), asset_id)

  #define USER_PTR_(USER_ID) \
    pointer(User, users_slot, USER_ID)

  #define USER_BALANCE_PTR_(USER_PTR, ASSET_ID) \
    pointer(u256, pointer_attr(User, USER_PTR, balances), ASSET_ID)

  #define SESSION_PTR_(USER_PTR, EXCHANGE_ID) \
    pointer(Exchange, pointer_attr(User, USER_PTR, exchange_sessions), EXCHANGE_ID)

  #define SESSION_BALANCE_PTR_(SESSION_PTR, ASSET_ID) \
    pointer(SessionBalance, pointer_attr(ExchangeSession, SESSION_PTR, balances), ASSET_ID)

  #define MARKET_IDX(QUOTE_ASSET_ID, BASE_ASSET_ID) \
    add(mul(QUOTE_ASSET_ID, ASSET_COUNT), BASE_ASSET_ID)

  #define MARKET_STATE_PTR_(SESSION_PTR, QUOTE_ASSET_ID, BASE_ASSET_ID) \
    pointer(MarketState, pointer_attr(ExchangeSession, SESSION_PTR, market_states), MARKET_IDX(QUOTE_ASSET_ID, BASE_ASSET_ID))


  /* feature flags to disable functions */

  #define FEATURE_ADD_ASSET 0x1
  #define FEATURE_ADD_EXCHANGE 0x2
  #define FEATURE_EXCHANGE_DEPOSIT 0x4
  #define FEATURE_DEPOSIT 0x8
  #define FEATURE_TRANSFER_TO_SESSION 0x10
  #define FEATURE_DEPOSIT_ASSET_TO_SESSION 0x20
  #define FEATURE_EXCHANGE_TRANSFER_FROM_LOCKED 0x40
  #define FEATURE_EXCHANGE_SET_LIMITS 0x80
  #define FEATURE_APPLY_SETTLEMENT_GROUPS 0x100
  #define FEATURE_EXCHANGE_UPDATE_OWNER 0x200
  #define FEATURE_EXCHANGE_PROPOSE_RECOVERY 0x400
  #define FEATURE_EXCHANGE_SET_RECOVERY 0x800
  #define FEATURE_CREATE_USER 0x1000
  #define FEATURE_ALL U256_MAX

  #define SECURITY_FEATURE_CHECK(FEATURE, REVERT_1) \
    { \
      let locked_features := sload(security_locked_features_slot) \
      if and(locked_features, FEATURE) { REVERT(REVERT_1) } \
    }

  /* ERC_20 */

  #define ERC_20_SEND(TOKEN_ADDRESS, TO_ADDRESS, AMOUNT, REVERT_1, REVERT_2) \
      mstore(transfer_in_mem, fn_hash("transfer(address,uint256)")) \
      mstore(add(transfer_in_mem, 4), TO_ADDRESS) \
      mstore(add(transfer_in_mem, 36), AMOUNT) \
      \
      let success := call( \
        gas, \
        TOKEN_ADDRESS, \
        /* don't send any ether */ 0, \
        transfer_in_mem, \
        /* transfer_in_mem size (bytes) */ 68, \
        transfer_out_mem, \
        /* transfer_out_mem size (bytes) */ 32 \
      ) \
      \
      if iszero(success) { \
        REVERT(REVERT_1) \
      } \
      \
      let result := mload(transfer_out_mem) \
      if iszero(result) { \
        REVERT(REVERT_2) \
      } \

  #define ERC_20_DEPOSIT(TOKEN_ADDRESS, FROM_ADDRESS, TO_ADDRESS AMOUNT, REVERT_1, REVERT_2) \
      mstore(transfer_in_mem, /* transferFrom(address,address,uint256) */ fn_hash("transferFrom(address,address,uint256)")) \
      mstore(add(transfer_in_mem, 4), FROM_ADDRESS) \
      mstore(add(transfer_in_mem, 36), TO_ADDRESS) \
      mstore(add(transfer_in_mem, 68), AMOUNT) \
      /* call external contract */ \
      { \
        let success := call( \
          gas, \
          TOKEN_ADDRESS, \
          /* don't send any ether */ 0, \
          transfer_in_mem, \
          /* transfer_in_mem size (bytes) */ 100, \
          transfer_out_mem, \
          /* transfer_out_mem size (bytes) */ 32 \
        ) \
        if iszero(success) { \
          REVERT(REVERT_1) \
        } \
        let result := mload(transfer_out_mem) \
        if iszero(result) { \
          REVERT(REVERT_2) \
        } \
      }


  function get_security_state() public view
  returns (uint256 locked_features, uint256 locked_features_proposed, uint256 proposed_unlock_timestamp) {
    uint256[3] memory return_value_mem;

    assembly {
      RETURN_0(sload(security_locked_features_slot))
      RETURN(WORD_1, sload(security_locked_features_proposed_slot))
      RETURN(WORD_2, sload(security_proposed_unlock_timestamp_slot))
      return(return_value_mem, WORD_3)
    }
  }

  function get_creator() public view
  returns (address dcn_creator, address dcn_creator_recovery, address dcn_creator_recovery_proposed) {
    uint256[3] memory return_value_mem;

    assembly {
      RETURN_0(sload(creator_slot))
      RETURN(WORD_1, sload(creator_recovery_slot))
      RETURN(WORD_2, sload(creator_recovery_proposed_slot))
      return(return_value_mem, WORD_3)
    }
  }

  function get_asset(uint32 asset_id) public view
  returns (string memory symbol, uint64 unit_scale, address contract_address, uint256 net_deposits) {
    uint256[6] memory return_value_mem;

    assembly {
      let asset_ptr := ASSET_PTR_(asset_id)
      let asset_0 := sload(asset_ptr)
      let asset_1 := sload(asset_ptr)

      MSTORE_STR(return_value_mem, WORD_3, 4, asset_0)
      RETURN(WORD_1, attr(Asset, 0, asset_0, unit_scale))
      RETURN(WORD_2, attr(Asset, 0, asset_0, contract_address))
      RETURN(WORD_3, attr(Asset, 1, asset_1, net_deposits))

      return(return_value_mem, const_add(WORD_4, /* string header */ WORD_1 , /* string data */ 4))
    }
  }

  function get_exchange(uint32 exchange_id) public view returns (string memory name, address owner,
                                                                 address recovery_address, address recovery_address_proposed) {
    /* [ name_offset, owner, recovery_address, recovery_address_proposed, name_len, name_data(12) ] */
    uint256[6] memory return_value_mem;

    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_0 := sload(exchange_ptr)
      let exchange_1 := sload(add(exchange_ptr, 1))
      let exchange_2 := sload(add(exchange_ptr, 2))

      MSTORE_STR(return_value_mem, WORD_6, 12, exchange_0)
      RETURN(WORD_1, attr(Exchange, 0, exchange_0, owner))
      RETURN(WORD_2, attr(Exchange, 1, exchange_1, recovery_address))
      RETURN(WORD_3, attr(Exchange, 2, exchange_2, recovery_address_proposed))

      return(return_value_mem, const_add(WORD_4, /* string header */ WORD_1, /* string data */ 12))
    }
  }

  function get_exchange_balance(uint32 exchange_id, uint32 asset_id) public view returns (uint256 exchange_balance) {
    uint256[1] memory return_value_mem;

    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, asset_id)

      RETURN_0(sload(exchange_balance_ptr))
      return(return_value_mem, WORD_1)
    }
  }

  function get_exchange_count() public view returns (uint32 count) {
    uint256[1] memory return_value_mem;

    assembly {
      RETURN_0(sload(exchange_count_slot))
      return(return_value_mem, WORD_1)
    }
  }

  function get_asset_count() public view returns (uint32 count) {
    uint256[1] memory return_value_mem;

    assembly {
      RETURN_0(sload(asset_count_slot))
      return(return_value_mem, WORD_1)
    }
  }

  function get_user(uint64 user_id) public view
  returns (
    address trade_address, address trade_address_proposed, uint256 trade_address_proposed_unlock_at,
    address withdraw_address, address recovery_address, address recovery_address_proposed
  ) {
    uint256[6] memory return_value_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)

      RETURN_0(      sload(pointer_attr(User, user_ptr, trade_address)))
      RETURN(WORD_1, sload(pointer_attr(User, user_ptr, trade_address_proposed)))
      RETURN(WORD_2, sload(pointer_attr(User, user_ptr, trade_address_proposed_unlock_at)))
      RETURN(WORD_3, sload(pointer_attr(User, user_ptr, withdraw_address)))
      RETURN(WORD_4, sload(pointer_attr(User, user_ptr, recovery_address)))
      RETURN(WORD_5, sload(pointer_attr(User, user_ptr, recovery_address_proposed)))

      return(return_value_mem, WORD_6)
    }
  }

  function get_balance(uint64 user_id, uint32 asset_id) public view returns (uint256 return_balance) {
    uint256[1] memory return_value_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)
      let user_balance_ptr := USER_BALANCE_PTR_(user_ptr, asset_id)

      RETURN_0(sload(user_balance_ptr))
      return(return_value_mem, WORD_1)
    }
  }

  function get_unlock_at(uint64 user_id, uint32 exchange_id) public view
  returns (uint256 unlock_at) {
    uint256[1] memory return_value_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let session_0 := sload(session_ptr)

      RETURN_0(attr(ExchangeSession, 0, session_0, unlock_at))
      return(return_value_mem, WORD_1)
    }
  }

  function get_session_balance(uint64 user_id, uint32 exchange_id, uint32 asset_id) public view
  returns (uint192 total_deposit, uint64 asset_balance) {
    uint256[2] memory return_value_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, asset_id)
      let session_balance_0 := sload(session_balance_ptr)

      RETURN_0(attr(SessionBalance, 0, session_balance_0, total_deposit))
      RETURN(WORD_1, attr(SessionBalance, 0, session_balance_0, asset_balance))

      return(return_value_mem, WORD_2)
    }
  }

  function get_market_state(
    uint64 user_id, uint32 exchange_id,
    uint32 quote_asset_id, uint32 base_asset_id
  ) public view returns (
    int64 quote_qty, int64 base_qty, uint64 fee_used, uint64 fee_limit,
    int64 min_quote_qty, int64 min_base_qty, uint64 long_max_price, uint64 short_min_price,
    uint64 limit_version, int96 quote_shift, int96 base_shift
  ) {
    uint256[11] memory return_value_mem;

    assembly {
      /* hack to get around stack depth issues in Solidity */
      base_shift := base_asset_id
      quote_shift := quote_asset_id

      let user_ptr := USER_PTR_(user_id)
      let exchange_session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let exchange_state_ptr := MARKET_STATE_PTR_(exchange_session_ptr, base_shift, quote_shift)

      let state_data_0 := sload(exchange_state_ptr)
      let state_data_1 := sload(add(exchange_state_ptr, 1))
      let state_data_2 := sload(add(exchange_state_ptr, 2))

      #define RETURN_64NEG(WORD, VALUE) \
        { \
          let tmp := VALUE \
          CAST_64_NEG(tmp) \
          RETURN(WORD, tmp) \
        }

      #define RETURN_96NEG(WORD, VALUE) \
        { \
          let tmp := VALUE \
          CAST_96_NEG(tmp) \
          RETURN(WORD, tmp) \
        }

      RETURN_64NEG(WORD_0,  attr(MarketState, 0, state_data_0, quote_qty))
      RETURN_64NEG(WORD_1,  attr(MarketState, 0, state_data_0, base_qty))
            RETURN(WORD_2,  attr(MarketState, 0, state_data_0, fee_used))
            RETURN(WORD_3,  attr(MarketState, 0, state_data_0, fee_limit))

      RETURN_64NEG(WORD_4,  attr(MarketState, 1, state_data_1, min_quote_qty))
      RETURN_64NEG(WORD_5,  attr(MarketState, 1, state_data_1, min_base_qty))
            RETURN(WORD_6,  attr(MarketState, 1, state_data_1, long_max_price))
            RETURN(WORD_7,  attr(MarketState, 1, state_data_1, short_min_price))

            RETURN(WORD_8,  attr(MarketState, 2, state_data_2, limit_version))
      RETURN_96NEG(WORD_9,  attr(MarketState, 2, state_data_2, quote_shift))
      RETURN_96NEG(WORD_10, attr(MarketState, 2, state_data_2, base_shift))

      return(return_value_mem, WORD_11)
    }
  }

  /* Security Feature Lock Management */

  #define CREATOR_REQUIRED(REVERT_1) \
    { \
      let creator := sload(creator_slot) \
      if iszero(eq(creator, caller)) { \
        REVERT(REVERT_1) \
      } \
    }

  function security_lock(uint256 lock_features) public {
    assembly {
      CREATOR_REQUIRED(1)

      let locked_features := sload(security_locked_features_slot)
      sstore(security_locked_features_slot, or(locked_features, lock_features))
      sstore(security_locked_features_proposed_slot, FEATURE_ALL)
    }
  }

  #define DAYS_2 172800 /* 2 days in seconds */

  function security_propose(uint256 proposed_locked_features) public {
    assembly {
      CREATOR_REQUIRED(1)

      /*
       * only update security_proposed_unlock_timestamp if
       * proposed_locked_features unlocks a new features
       */

      let current_proposal := sload(security_locked_features_proposed_slot)
      let proposed_differences := xor(current_proposal, proposed_locked_features)

      /*
       * proposed_differences will have "1" in feature positions that have changed.
       * Want to see if those positions have proposed_locked_features as "0", meaning
       * that those features will be unlocked.
       */
      
      let does_unlocks_features := and(proposed_differences, not(proposed_locked_features))

      /* update unlock_timestamp */
      if does_unlocks_features {
        sstore(security_proposed_unlock_timestamp_slot, add(timestamp, DAYS_2))
      }

      sstore(security_locked_features_proposed_slot, proposed_locked_features)
    }
  }

  function security_set_proposed() public {
    assembly {
      CREATOR_REQUIRED(1)

      let unlock_timestamp := sload(security_proposed_unlock_timestamp_slot) 
      if lt(unlock_timestamp, timestamp) {
        REVERT(2)
      }

      sstore(security_locked_features_slot, sload(security_locked_features_proposed_slot))
    }
  }

  /* Creator Management */

  function creator_update(address new_creator) public {
    assembly {
      let creator_recovery := sload(creator_recovery_slot)
      if iszero(eq(creator_recovery, caller)) {
        REVERT(1)
      }

      sstore(creator_slot, new_creator)
    }
  }

  function creator_propose_recovery(address recovery) public {
    assembly {
      let creator_recovery := sload(creator_recovery_slot)
      if iszero(eq(creator_recovery, caller)) {
        REVERT(1)
      }

      sstore(creator_recovery_proposed_slot, recovery)
    }
  }

  function creator_update_recovery() public {
    assembly {
      let creator_recovery_proposed := sload(creator_recovery_proposed_slot)
      if or(iszero(eq(creator_recovery_proposed, caller)), iszero(caller)) {
        REVERT(1)
      }
      sstore(creator_recovery_slot, caller)
    }
  }

  /* User Management */

  function user_create() public {
    uint256[2] memory log_data_mem;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_CREATE_USER, 0)

      let user_id := sload(user_count_slot)
      if iszero(lt(user_id, USER_COUNT)) {
        REVERT(1)
      }

      /* increase user count */
      sstore(user_count_slot, add(user_id, 1))

      /* set management addresses */
      let user_ptr := USER_PTR_(user_id)
      sstore(pointer_attr(User, user_ptr, trade_address), caller)
      sstore(pointer_attr(User, user_ptr, withdraw_address), caller)
      sstore(pointer_attr(User, user_ptr, recovery_address), caller)

      log_event(UserCreated, log_data_mem, user_id)
    }
  }

  function user_trade_address_propose(uint64 user_id, address trade_address) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
    }
  }

  /* Exchange Management */

  function exchange_update_owner(uint32 exchange_id, address new_owner) public {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_UPDATE_OWNER, 0)

      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_backup := attr(Exchange, 1, sload(add(exchange_ptr, 1)), recovery_address)

      /* ensure caller is backup */
      if iszero(eq(exchange_backup, caller)) {
        REVERT(1)
      }

      let exchange_0 := sload(exchange_ptr)
      sstore(exchange_ptr, or(
        and(exchange_0, mask_out(Exchange, 0, owner)),
        build(Exchange, 0, /* name */ 0, /* quote_asset_id */ 0, /* owner */ new_owner)
      ))
    }
  }

  function exchange_propose_backup(uint32 exchange_id, address backup) public {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_PROPOSE_RECOVERY, 0)

      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_backup := attr(Exchange, 1, sload(add(exchange_ptr, 1)), recovery_address)

      /* ensure caller is backup */
      if iszero(eq(exchange_backup, caller)) {
        REVERT(1)
      }

      /* update proposed */
      sstore(add(exchange_ptr, 2), backup)
    }
  }

  function exchange_set_backup(uint32 exchange_id) public {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_SET_RECOVERY, 0)

      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_backup_proposed := attr(Exchange, 2, sload(add(exchange_ptr, 2)), recovery_address_proposed)

      /* ensure caller is proposed backup */
      if or(iszero(eq(exchange_backup_proposed, caller)), iszero(caller)) {
        REVERT(1)
      }

      /* update backup */
      sstore(add(exchange_ptr, 1), caller)
    }
  }

  /* Manage Registered Entities */

  function add_asset(string memory symbol, uint64 unit_scale, address contract_address) public {
    uint256[1] memory revert_reason;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_ADD_ASSET, 0)
      CREATOR_REQUIRED(1)

      /* do not want to overflow assets array */
      let asset_id := sload(asset_count_slot)
      if iszero(lt(asset_id, ASSET_COUNT)) {
        REVERT(2)
      }

      /* Symbol must be 4 characters */
      let symbol_len := mload(symbol)
      if iszero(eq(symbol_len, 4)) {
        REVERT(3)
      }

      /* Unit scale must be non zero */
      if iszero(unit_scale) {
        REVERT(4)
      }

      if iszero(contract_address) {
        REVERT(5)
      }

      let asset_symbol := mload(add(symbol, WORD_1 /* offset as first word is size */))

      /* Note, symbol is already shifted not setting it in build */
      let asset_data := or(asset_symbol, build(Asset, 0, /* symbol */ 0, unit_scale, contract_address))
      let asset_ptr := ASSET_PTR_(asset_id)

      sstore(asset_ptr, asset_data)
      sstore(asset_count_slot, add(asset_id, 1))
    }
  }
//
//  function add_exchange(string memory name, address addr) public {
//    uint256[1] memory revert_reason;
//
//    assembly {
//      SECURITY_FEATURE_CHECK(FEATURE_ADD_EXCHANGE, 0)
//      CREATOR_REQUIRED(1)
//
//      /* Name must be 12 bytes long */
//      let name_len := mload(name)
//      if iszero(eq(name_len, 12)) {
//        REVERT(2)
//      }
//
//      /* Do not overflow exchanges */
//      let exchange_id := sload(exchange_count_slot)
//      if iszero(lt(exchange_id, EXCHANGE_COUNT)) {
//        REVERT(4)
//      }
//
//      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
//
//      /* 
//       * name is at start of the word. After loading it is already shifted
//       * so use or to add it rather than shifting twice with build
//       */
//
//      let name_data := mload(add(name, 32))
//      let exchange_data := or(name_data, build(Exchange, 0, /* space for name */ 0, addr))
//      sstore(exchange_ptr, exchange_data)
//
//      /* Store owner backup */
//      sstore(add(exchange_ptr, 2), addr)
//
//      /* Update exchange count */
//      sstore(exchange_count_slot, add(exchange_id, 1))
//    }
//  }
//
//  /* Manage Exchange Balance */
//
//  function exchange_withdraw(uint32 exchange_id, uint32 asset_id,
//                             address destination, uint64 quantity) public {
//    uint256[1] memory revert_reason;
//    uint256[3] memory transfer_in_mem;
//    uint256[1] memory transfer_out_mem;
//
//    assembly {
//      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
//      let exchange_0 := sload(exchange_ptr)
//
//      /* ensure caller is owner */
//      let exchange_owner := attr(Exchange, 0, exchange_0, owner)
//      if iszero(eq(exchange_owner, caller)) {
//        REVERT(1)
//      }
//
//      let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, asset_id)
//      let exchange_balance := sload(exchange_balance_ptr)
//
//      /* insufficient funds */
//      if gt(quantity, exchange_balance) {
//        REVERT(2)
//      }
//
//      /* decrement balance */
//      sstore(exchange_balance_ptr, sub(exchange_balance, quantity))
//
//      let asset_0 := sload(ASSET_PTR_(quote_asset_id))
//      let unit_scale := attr(Asset, 0, asset_0, unit_scale)
//      let asset_address := attr(Asset, 0, asset_0, contract_address)
//
//      let withdraw := mul(quantity, unit_scale)
//
//      ERC_20_SEND(
//        /* TOKEN_ADDRESS */ asset_address,
//        /* TO_ADDRESS */ destination,
//        /* AMOUNT */ withdraw,
//        /* REVERT_1 */ 3,
//        /* REVERT_2 */ 4
//      )
//    }
//  }
//
//  function exchange_deposit(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
//    uint256[1] memory revert_reason;
//    uint256[3] memory transfer_in_mem;
//    uint256[1] memory transfer_out_mem;
//
//    assembly {
//      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_DEPOSIT, 0)
//
//      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
//      let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, asset_id)
//      let exchange_balance := sload(exchange_balance_ptr)
//
//      let updated_balance := add(exchange_balance, quantity)
//      if U64_OVERFLOW(updated_balance) {
//        REVERT(1)
//      }
//
//      let asset_0 := sload(ASSET_PTR_(quote_asset_id))
//      let unit_scale := attr(Asset, 0, asset_0, unit_scale)
//      let asset_address := attr(Asset, 0, asset_0, contract_address)
//
//      let deposit := mul(quantity, unit_scale)
//
//      ERC_20_DEPOSIT(
//        /* TOKEN_ADDRESS */ asset_address,
//        /* FROM_ADDRESS */ caller,
//        /* TO_ADDRESS */ address,
//        /* AMOUNT */ deposit,
//        /* REVERT_1 */ 2,
//        /* REVERT_2 */ 3
//      )
//
//      sstore(exchange_balance_ptr, updated_balance)
//    }
//  }
//
//  function deposit(uint32 asset_id, uint256 amount) public {
//    uint256[1] memory revert_reason;
//    uint256[4] memory transfer_in_mem;
//    uint256[1] memory transfer_out_mem;
//
//    assembly {
//      SECURITY_FEATURE_CHECK(FEATURE_DEPOSIT, 0)
//      VALID_ASSET_ID(asset_id, 1)
//
//      if iszero(amount) {
//        stop()
//      }
//
//      let balance_ptr := USER_BALANCE_PTR(USER_PTR(caller), asset_id)
//      let current_balance := sload(balance_ptr)
//
//      let proposed_balance := add(current_balance, amount)
//
//      /* Prevent overflow */
//      if lt(proposed_balance, current_balance) {
//        REVERT(2)
//      }
//
//      let asset_0 := sload(ASSET_PTR_(asset_id))
//      let asset_address := attr(Asset, 0, asset_0, contract_address)
//
//      ERC_20_DEPOSIT(
//        /* TOKEN_ADDRESS */ asset_address,
//        /* FROM_ADDRESS */ caller,
//        /* TO_ADDRESS */ address,
//        /* AMOUNT */ amount,
//        /* REVERT_1 */ 3,
//        /* REVERT_2 */ 4
//      )
//
//      sstore(balance_ptr, proposed_balance)
//    }
//  }
//
//  function withdraw(uint32 asset_id, address destination, uint256 amount) public {
//    uint256[1] memory revert_reason;
//    uint256[3] memory transfer_in_mem;
//    uint256[1] memory transfer_out_mem;
//
//    assembly {
//      if iszero(amount) {
//        stop()
//      }
//
//      VALID_ASSET_ID(asset_id, 1)
//
//      let balance_ptr := USER_BALANCE_PTR(USER_PTR(caller), asset_id)
//      let current_balance := sload(balance_ptr)
//
//      /* insufficient funds */
//      if lt(current_balance, amount) {
//        REVERT(1)
//      }
//
//      sstore(asset_ptr, sub(current_balance, amount))
//
//      let asset_data := sload(ASSET_PTR_(asset_id))
//      let asset_address := attr(Asset, 0, asset_data, contract_address)
//
//      ERC_20_SEND(
//        /* TOKEN_ADDRESS */ asset_address,
//        /* TO_ADDRESS */ destination,
//        /* AMOUNT */ amount,
//        /* REVERT_1 */ 2,
//        /* REVERT_2 */ 3
//      )
//    }
//  }
//
//  #define MIN_EXPIRE_TIME 28800 /* 8 hours in seconds */
//  #define MAX_EXPIRE_TIME 1209600 /* 14 days in seconds */
//
//  function set_unlock(uint32 exchange_id, uint256 unlock_at) public {
//    uint256[1] memory revert_reason;
//    uint256[3] memory log_data_mem;
//
//    assembly {
//      /* validate time range of unlock_at */
//      { 
//        let fails_min_time := lt(unlock_at, add(timestamp, MIN_EXPIRE_TIME))
//        let fails_max_time := gt(unlock_at, add(timestamp, MAX_EXPIRE_TIME))
//
//        if or(fails_min_time, fails_max_time) {
//          REVERT(1)
//        }
//      }
//
//      VALID_EXCHANGE_ID(exchange_id, 2)
//
//      let user_ptr := USER_PTR_(caller)
//      sstore(user_ptr, unlock_at)
//
//      log_event(UnlockAtUpdated, log_data_mem, caller, exchange_id)
//    }
//  }
//
//  function transfer_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
//    uint256[1] memory revert_reason;
//    uint256[4] memory log_data_mem;
//
//    assembly {
//      if iszero(quantity) {
//        stop()
//      }
//
//      SECURITY_FEATURE_CHECK(FEATURE_TRANSFER_TO_SESSION, 0)
//      VALID_EXCHANGE_ID(exchange_id, 1)
//      VALID_ASSET_ID(asset_id, 2)
//
//      let asset_ptr := ASSET_PTR_(asset_id)
//      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
//      let scaled_quantity := mul(quantity, unit_scale)
//
//      /* load user balance */
//      let user_ptr := USER_PTR(caller)
//      let user_balance_ptr := USER_BALANCE_PTR(user_ptr, asset_id)
//      let user_balance := sload(user_balance_ptr)
//
//      /* insufficient funds */
//      if lt(user_balance, scaled_quantity) {
//        REVERT(3)
//      }
//
//      /* load exchange balance */
//      let session_ptr := EXCHANGE_SESSION_PTR(user_ptr, exchange_id)
//      let exchange_balance_ptr := USER_EXCHANGE_BALANCE_PTR(session_ptr, asset_id)
//      let exchange_balance_data_0 := sload(exchange_balance_ptr)
//
//      let updated_exchange_balance := add(attr(SessionBalance, 0, asset_balance), quantity)
//      if U64_OVERFLOW(updated_exchange_balance) {
//        REVERT(4)
//      }
//
//      /* don't care about overflow for total_deposit, is used by exchange to detect updated */
//      let updated_total_deposit := add(attr(SessionBalance, 0, exchange_balance_data_0, total_deposit), quantity)
//
//      /* update user balance */
//      sstore(user_balance_ptr, sub(user_balance, scaled_quantity))
//
//      /* update exchange balance */
//      sstore(exchange_balance_ptr, or(
//        and(mask_out(SessionBalance, 0, total_deposit, asset_balance), exchange_balance_data_0),
//        build(SessionBalance, 0,
//              /* total_deposit */ updated_total_deposit,
//              /* unsettled_withdraw_total */ 0,
//              /* asset_balance */ updated_exchange_balance)
//      ))
//
//      log_event(ExchangeDeposit, log_data_mem, caller, exchange_id, asset_id)
//    }
//  }
//
//  function transfer_from_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
//    uint256[1] memory revert_reason;
//    uint256[4] memory log_data_mem;
//
//    assembly {
//      if iszero(quantity) {
//        stop()
//      }
//
//      VALID_EXCHANGE_ID(exchange_id, 1)
//      VALID_ASSET_ID(asset_id, 2)
//
//      let user_ptr := USER_PTR(caller)
//      let session_ptr := EXCHANGE_SESSION_PTR(user_ptr, exchange_id)
//
//      /* ensure session in unlocked */
//      {
//        let exchange_session_data_0 := sload(session_ptr)
//        let unlock_at := attr(ExchangeSession, 0, exchange_balance_data_0, unlock_at)
//
//        if gt(unlock_at, timestamp) {
//          REVERT(3)
//        }
//      }
//
//      /* load exchange balance */
//      let exchange_balance_ptr := EUSER_XCHANGE_BALANCE_PTR(session_ptr, asset_id)
//      let exchange_balance_data_0 := sload(exchange_balance_ptr)
//      let exchange_balance := attr(SessionBalance, 0, exchange_balance_data_0, asset_balance)
//
//      /* insufficient funds */
//      if gt(quantity, exchange_balance) {
//        REVERT(4)
//      }
//
//      let updated_exchange_balance := sub(exchange_balance, quantity)
//      let unsettled_withdraw_total := attr(SessionBalance, 0, exchange_balance_data_0, unsettled_withdraw_total)
//
//      /* do not let user withdraw money owed to the exchange */
//      if lt(updated_exchange_balance, unsettled_withdraw_total) {
//        REVERT(5)
//      }
//
//      sstore(exchange_balance_ptr, or(
//        and(mask_out(SessionBalance, 0, asset_balance), exchange_balance_data_0),
//        build(SessionBalance, 0,
//              /* total_deposit */ 0,
//              /* unsettled_withdraw_total */ 0,
//              /* asset_balance */ updated_exchange_balance)
//      ))
//
//      let asset_ptr := ASSET_PTR_(asset_id)
//      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
//      let scaled_quantity := mul(quantity, unit_scale)
//
//      let user_balance_ptr := USER_BALANCE_PTR(user_ptr, asset_id)
//      let user_balance := sload(user_balance_ptr)
//
//      let updated_user_balance := add(user_balance, scaled_quantity)
//      /* protect against addition overflow */
//      if lt(updated_user_balance, user_balance) {
//        REVERT(6)
//      }
//
//      sstore(user_balance_ptr, updated_user_balance)
//    }
//  }
//
//  function deposit_asset_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
//    uint256[1] memory revert_reason;
//    uint256[4] memory transfer_in_mem;
//    uint256[1] memory transfer_out_mem;
//    uint256[3] memory log_data_mem;
//
//    assembly {
//      SECURITY_FEATURE_CHECK(FEATURE_DEPOSIT_ASSET_TO_SESSION, 0)
//
//      VALID_EXCHANGE_ID(exchange_id, 1)
//      VALID_ASSET_ID(asset_id, 2)
//
//      if iszero(quantity) {
//        stop()
//      }
//
//      let session_ptr := EXCHANGE_SESSION_PTR(USER_PTR(caller), exchange_id)
//      let exchange_balance_ptr := USER_EXCHANGE_BALANCE_PTR(session_ptr, asset_id)
//      let exchange_balance_data_0 := sload(exchange_balance_ptr)
//
//      let updated_exchange_balance := add(attr(SessionBalance, 0, asset_balance), quantity)
//      if U64_OVERFLOW(updated_exchange_balance) {
//        REVERT(3)
//      }
//
//      let asset_0 := sload(ASSET_PTR_(asset_id))
//      let asset_address := attr(Asset, 0, asset_0, contract_address)
//      let unit_scale := attr(Asset, 0, asset_0, unit_scale)
//
//      let scaled_quantity := mul(quantity, unit_scale)
//
//      ERC_20_DEPOSIT(
//        /* TOKEN_ADDRESS */ asset_address,
//        /* FROM_ADDRESS */ caller,
//        /* TO_ADDRESS */ address,
//        /* AMOUNT */ scaled_quantity,
//        /* REVERT_1 */ 4,
//        /* REVERT_2 */ 5
//      )
//
//      let updated_total_deposit := add(attr(SessionBalance, 0, exchange_balance_data_0, total_deposit), quantity)
//      /* update exchange balance */
//      sstore(exchange_balance_ptr, or(
//        and(mask_out(SessionBalance, 0, total_deposit, asset_balance), exchange_balance_data_0),
//        build(SessionBalance, 0,
//              /* total_deposit */ updated_total_deposit,
//              /* unsettled_withdraw_total */ 0,
//              /* asset_balance */ updated_exchange_balance)
//      ))
//
//      log_event(ExchangeDeposit, log_data_mem, caller, exchange_id, asset_id)
//    }
//  }
//
//  struct ExchangeTransfersHeader {
//    uint32 exchange_id;
//  }
//
//  struct ExchangeTransferGroup {
//    uint32 asset_id;
//    uint8 allow_overdraft;
//    uint8 transfer_count;
//  }
//
//  struct ExchangeTransfer {
//    address user_address;
//    uint64 quantity;
//  }
//
//  function exchange_transfer_from_locked(bytes memory data) public {
//    uint256[1] memory revert_reason;
//
//    assembly {
//      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_TRANSFER_FROM_LOCKED, 0)
//
//      let data_len := mload(data)
//      let cursor := add(data, WORD_1)
//      let cursor_end := add(cursor, data_len)
//
//      /* ensure there's enough space for the header */
//      if lt(data_len, sizeof(ExchangeTransfersHeader)) {
//        REVERT(1)
//      }
//
//      let exchange_transfer_header_0 := mload(cursor)
//      cursor := add(cursor, sizeof(ExchangeTransfersHeader))
//
//      let exchange_id := attr(ExchangeTransfersHeader, 0, exchange_transfer_header_0, exchange_id)
//      VALID_EXCHANGE_ID(exchange_id)
//
//      /* ensure exchange is caller */
//      {
//        let exchange_data := sload(EXCHANGE_PTR_(exchange_id))
//        if iszero(eq(caller, attr(Exchange, 0, exchange_data, owner))) {
//          REVERT(3)
//        }
//      }
//
//      let asset_count := sload(asset_count_slot)
//
//      for {} lt(cursor, cursor_end) {} {
//        load := mload(cursor)
//        cursor := add(cursor, sizeof(ExchangeTransferGroup))
//
//        /* ensure there is enough space for ExchangeTransferGroup */
//        if gt(cursor, cursor_end) {
//          REVERT(4)
//        }
//
//        let asset_id := attr(ExchangeTransferGroup, 0, load, asset_id)
//
//        /* Validate asset id */
//        if iszero(lt(asset_id, asset_count)) {
//          REVERT(5)
//        }
//
//        let disallow_overdraft := iszero(attr(ExchangeTransferGroup, 0, load, allow_overdraft))
//        let cursor_group_end := add(cursor, mul(
//          attr(ExchangeTransferGroup, 0, load, transfer_count),
//          sizeof(ExchangeTransfer)
//        ))
//
//        /* ensure data fits in input */
//        if gt(cursor_group_end, cursor_end) {
//          REVERT(6)
//        }
//
//        let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(EXCHANGE_PTR_(exchange_id), asset_id)
//        let exchange_balance_remaining := sload(exchange_balance_ptr)
//
//        let unit_scale := attr(Asset, 0, sload(ASSET_PTR_(asset_id)), unit_scale)
//        
//        for {} lt(cursor, cursor_group_end) { cursor := add(cursor, sizeof(ExchangeTransfer)) } {
//          load := mload(cursor)
//
//          let user_ptr := USER_PTR(attr(ExchangeTransfer, 0, load, user_address))
//          let quantity := attr(ExchangeTransfer, 0, load, quantity)
//
//          let exchange_balance_used := 0
//
//          let user_exchange_balance_ptr := USER_EXCHANGE_BALANCE_PTR(EXCHANGE_SESSION_PTR(user_ptr, exchange_id), asset_id)
//          let user_exchange_balance_data_0 := sload(user_exchange_balance_ptr)
//          let user_exchange_balance := attr(SessionBalance, 0, user_exchange_balance_data_0, asset_balance)
//
//          let user_exchange_balance_updated := sub(user_exchange_balance, quantity)
//
//          /*
//           * check for underflow (quantity > user_exchange_balance),
//           * then need to dip into exchange_balance_remaining if user_exchange_balance isn't enough
//           */
//          if gt(user_exchange_balance_updated, user_exchange_balance) {
//            if disallow_overdraft {
//              REVERT(7)
//            }
//
//            exchange_balance_used := sub(quantity, user_exchange_balance)
//            user_exchange_balance_updated := 0
//
//            if gt(exchange_balance_used, exchange_balance_remaining) {
//              REVERT(8)
//            }
//
//            exchange_balance_remaining := sub(exchange_balance_remaining, exchange_balance_used)
//          }
//
//          let quantity_scaled := mul(quantity, unit_scale)
//
//          let user_balance_ptr := USER_BALANCE_PTR(user_ptr, asset_id)
//          let user_balance := sload(user_balance_ptr)
//
//          let updated_user_balance := add(user_balance, quantity_scaled)
//          /* prevent overflow */
//          if gt(user_balance, updated_user_balance) {
//            REVERT(9)
//          }
//
//          let updated_unsettled_withdraw_total := add(
//            attr(SessionBalance, 0, user_exchange_balance_data_0, unsettled_withdraw_total),
//            exchange_balance_used
//          ) 
//
//          sstore(user_exchange_balances_ptr, or(
//            and(mask_out(SessionBalance, 0, unsettled_withdraw_total, asset_balance), user_exchange_balance_data_0),
//            build(SessionBalance, 0,
//                  /* total_deposit */ 0,
//                  /* unsettled_withdraw_total */ updated_unsettled_withdraw_total,
//                  /* asset_balance */ user_exchange_balance_updated)
//          ))
//
//          sstore(user_balance_ptr, updated_user_balance)
//        }
//
//        sstore(exchange_balance_ptr, exchange_balance_remaining)
//      }
//    }
//  }
//
//  struct SetLimitsHeader {
//    uint32 exchange_id;
//  }
//
//  struct Signature {
//    uint256 sig_r;
//    uint256 sig_s;
//    uint8 sig_v;
//    address user_address;
//  }
//
//  struct UpdateLimit {
//    uint96 dcn_id;
//    uint32 exchange_id;
//    uint32 quote_asset_id;
//    uint32 base_asset_id;
//    uint64 fee_limit;
//
//    int64 min_quote_qty;
//    int64 min_base_qty;
//    uint64 long_max_price;
//    uint64 short_min_price;
//
//    uint64 limit_version;
//    uint96 quote_shift;
//    uint96 base_shift;
//  }
//
//  #define UPDATE_LIMIT_BYTES const_add(sizeof(UpdateLimit), sizeof(Signature))
//  #define SIG_HASH_HEADER 0x1901000000000000000000000000000000000000000000000000000000000000
//  #define DCN_HEADER_HASH 0xe3d3073cc59e3a3126c17585a7e516a048e61a9a1c82144af982d1c194b18710
//  #define UPDATE_LIMIT_TYPE_HASH 0xe0bfc2789e007df269c9fec46d3ddd4acf88fdf0f76af154da933aab7fb2f2b9
//
//  function exchange_set_limits(bytes memory data) public {
//    uint256[1] memory revert_reason;
//    uint256[10] memory to_hash_mem;
//
//    uint256 cursor;
//    uint256 cursor_end;
//    uint256 exchange_id;
//
//    /*
//     * Ensure caller is exchange and setup cursors.
//     */
//    assembly {
//      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_SET_LIMITS, 0)
//
//      let data_size := mload(data)
//      cursor := add(data, WORD_1)
//      cursor_end := add(cursor, data_size)
//
//      let set_limits_header_0 := mload(cursor)
//      cursor := add(cursor, sizeof(SetLimitsHeader))
//
//      /* ensure there was space for SetLimitsHeader */
//      if gt(cursor, cursor_end) {
//        REVERT(1)
//      }
//
//      exchange_id := attr(SetLimitsHeader, 0, set_limits_header_0, exchange_id)
//      let exchange_0 := sload(EXCHANGE_PTR_(exchange_id))
//      let exchange_owner := attr(Exchange, 0, exchange_0, owner)
//
//      /* ensure caller is the exchange owner */
//      if iszero(eq(caller, exchange_owner)) {
//        REVERT(2)
//      }
//    }
//
//    /*
//     * Iterate through each limit to validate and apply
//     */
//    while (true) {
//      uint256 update_limit_0;
//      uint256 update_limit_1;
//      uint256 update_limit_2;
//
//      uint256 limit_hash;
//
//      /* hash limit */
//      assembly {
//        /* Reached the end */
//        if eq(cursor, cursor_end) {
//          return(0, 0)
//        }
//
//        update_limit_0 := mload(cursor)
//        update_limit_1 := mload(add(cursor, WORD_1))
//        update_limit_2 := mload(add(cursor, WORD_2))
//        cursor := add(cursor, WORD_3)
//
//        /* ensure there is space */
//        if gt(cursor, cursor_end) {
//          REVERT(3)
//        }
//
//        /* macro to make life easier */
//        #define ATTR_GET(INDEX, ATTR_NAME) \
//          attr(UpdateLimit, INDEX, update_limit_##INDEX, ATTR_NAME)
//
//        #define BUF_PUT(WORD, INDEX, ATTR_NAME) \
//          mstore(add(to_hash_mem, WORD), ATTR_GET(ATTR_NAME))
//
//        #define BUF_PUT_I64(WORD, INDEX, ATTR_NAME) \
//          { \
//            let temp_var := ATTR_GET(INDEX, ATTR_NAME) \
//            CAST_64_NEG(temp_var) \
//            mstore(add(to_hash_mem, WORD), temp_var) \
//          }
//
//        #define BUF_PUT_I96(WORD, INDEX, ATTR_NAME) \
//          { \
//            let temp_var := ATTR_GET(INDEX, ATTR_NAME) \
//            CAST_96_NEG(temp_var) \
//            mstore(add(to_hash_mem, WORD), temp_var) \
//          }
//
//        /* store data to hash */
//        {
//          mstore(to_hash_mem, UPDATE_LIMIT_TYPE_HASH)
//
//          BUF_PUT(WORD_1, 0, dcn_id)
//          BUF_PUT(WORD_2, 0, exchange_id)
//          BUF_PUT(WORD_3, 0, quote_asset_id)
//          BUF_PUT(WORD_4, 0, base_asset_id)
//          BUF_PUT(WORD_5, 0, fee_limit)
//
//          BUF_PUT_I64(WORD_6, 1, min_quote_qty)
//          BUF_PUT_I64(WORD_7, 1, min_base_qty)
//          BUF_PUT(WORD_8, 1, long_max_price)
//          BUF_PUT(WORD_9, 1, short_min_price)
//
//          BUF_PUT(WORD_10, 2, limit_version)
//          BUF_PUT_I96(WORD_11, 2, quote_shift)
//          BUF_PUT_I96(WORD_12, 2, base_shift)
//        }
//
//        limit_hash := keccak256(to_hash_mem, 66)
//      }
//
//      address user_address;
//
//      /* verify signature */
//      {
//        bytes32 sig_r;
//        bytes32 sig_s;
//        uint8 sig_v;
//
//        assembly {
//          sig_r := attr(Signature, 0, sload(cursor), sig_r)
//          sig_s := attr(Signature, 1, sload(add(cursor, 1)), sig_s)
//
//          let signature_2 := sload(add(cursor, 2))
//          sig_v := attr(Signature, 2, signature_2, sig_v)
//          user_address := attr(Signature, 2, signature_2, user_address)
//
//          cursor := add(cursor, sizeof(Settlement))
//          if gt(cursor, cursor_end) {
//            REVERT(4)
//          }
//        }
//
//        uint256 recover_address = uint256(ecrecover(
//          /* hash */ bytes32(to_hash_mem[0]),
//          /* v */ sig_v,
//          /* r */ sig_r,
//          /* s */ sig_s
//        ));
//
//        assembly {
//          if iszero(eq(recover_address, user_address)) {
//            REVERT(5)
//          }
//        }
//      }
//
//      /* validate and apply new limit */
//      assembly {
//        /* limit's exchange id should match */
//        {
//          let limit_exchange_id := attr(UpdateLimit, 0, update_limit_0, exchange_id)
//          if iszero(eq(limit_exchange_id, exchange_id)) {
//            REVERT(6)
//          }
//        }
//
//        let user_ptr := USER_PTR(user_address)
//        let session_ptr := EXCHANGE_SESSION_PTR(user_ptr, exchange_id)
//        let market_state_ptr := MARKET_STATE_PTR(
//          session_ptr,
//          attr(UpdateLimit, 0, update_limit_0, quote_asset_id),
//          attr(UpdateLimit, 0, update_limit_0, base_asset_id)
//        )
//
//        let market_state_0 := sload(market_state_ptr)
//        let market_state_1 := sload(add(market_state_ptr, 1))
//        let market_state_2 := sload(add(market_state_ptr, 2))
//
//        /* verify limit version is greater */
//        {
//          let current_limit_version := attr(UpdateLimit, 2, update_limit_2, limit_version)
//          let proposed_limit_version := attr(MarketState, 2, market_state_2, limit_version)
//
//          if iszero(gt(proposed_limit_version, current_limit_version)) {
//            REVERT(7)
//          }
//        }
//
//        let quote_qty := attr(MarketState, 0, market_state_0, quote_qty)
//        CAST_64_NEG(quote_qty)
//
//        let base_qty := attr(MarketState, 0, market_state_0, base_qty)
//        CAST_64_NEG(base_qty)
//
//        #define APPLY_SHIFT(SIDE, REVERT_1) \
//          { \
//            let current_shift := attr(MarketState, 2, market_state_2, SIDE##_shift) \
//            CAST_96_NEG(current_shift) \
//            \
//            let new_shift := attr(UpdateLimit, 2, update_limit_2, SIDE##_shift) \
//            CAST_96_NEG(new_shift) \
//            \
//            SIDE##_qty := add(quote_qty, sub(new_shift, current_shift)) \
//            if INVALID_I64(SIDE##_qty) { \
//              REVERT(REVERT_1) \
//            } \
//          }
//
//        APPLY_SHIFT(quote, 8)
//        APPLY_SHIFT(base, 8)
//
//        let new_market_state_0 := or(
//          build(MarketState, 0, quote_qty, base_qty, /* fee_used */ 0, update_limit_0),
//          and(mask_out(MarketState, 0, quote_qty, base_qty, fee_limit), market_state_0) /* extract fee_used */
//        )
//
//        sstore(market_state_ptr, new_market_state_0)
//        sstore(add(market_state_ptr, 1), update_limit_1)
//        sstore(add(market_state_ptr, 2), update_limit_2)
//      }
//    }
//  }
//
//  struct GroupsHeader {
//    uint32 exchange_id;
//  }
//
//  struct GroupHeader {
//    uint32 quote_asset_id;
//    uint32 base_asset_id;
//    uint8 user_count;
//  }
//
//  struct UserAddress {
//    address user_address;
//  }
//
//  struct Settlement {
//    int64 quote_delta;
//    int64 base_delta;
//    uint64 fees;
//  }
//
//  function exchange_apply_settlement_groups(bytes memory data) public {
//    uint256[6] memory variables;
//    
//    /*
//    #define VARIABLES_END         msize
//    #define VARIABLES_START       sub(VARIABLES_END, WORD_6)
//
//    #define QUOTE_ASSET_ID_MEM    sub(VARIABLES_END, WORD_6)
//    #define DATA_END_MEM          sub(VARIABLES_END, WORD_5)
//    #define EXCHANGE_FEES_MEM     sub(VARIABLES_END, WORD_4)
//    #define EXCHANGE_ID_MEM       sub(VARIABLES_END, WORD_3)
//    #define GROUP_END_MEM         sub(VARIABLES_END, WORD_2)
//    #define REVERT_REASON_MEM     sub(VARIABLES_END, WORD_1)
//
//    #define REVERT(code)    mstore(REVERT_REASON_MEM, code) \
//                                  revert(add(REVERT_REASON_MEM, 31), 1)
//    
//    #define DEBUG(code)           mstore(REVERT_REASON_MEM, code) \
//                                  revert(REVERT_REASON_MEM, 32)
//                                  */
//
//    assembly {
//      /* Check security lock */
//      SECURITY_FEATURE_CHECK(FEATURE_APPLY_SETTLEMENT_GROUPS, 0)
//
//      let data_len := mload(data)
//      let cursor := add(data, WORD_1)
//      let cursor_end := add(cursor, data_len)
//
//      let m_load /* GroupsHeader */ := mload(cursor)
//      cursor := add(cursor, sizeof(GroupsHeader))
//      if gt(cursor, cursor_end) {
//        REVERT(0)
//      }
//
//      let exchange_id := attr(GroupsHeader, 0, m_load /* GroupsHeader */, exchange_id)
//      VALID_EXCHANGE_ID(1)
//
//      /* caller must be exchange owner */
//      {
//        let exchange_ptr := EXCHANGE_PTR_(exchange_id)
//        let exchange_0 := sload(exchange_ptr)
//
//        if iszero(eq(caller, attr(Exchange, 0, exchange_0, owner))) {
//          REVERT(2)
//        }
//      }
//
//      /* keep looping while there is space for a GroupHeader */
//      for {} lt(cursor, cursor_end), sizeof(GroupHeader))) {} {
//        m_load /* GroupHeader */ := mload(cursor)
//        cursor := add(cursor, sizeof(GroupHeader))
//        if gt(cursor, cursor_end) {
//          REVERT(3)
//        }
//
//        let quote_asset_id := attr(GroupHeader, 0, m_load /* GroupHeader */, quote_asset_id)
//        let base_asset_id := attr(GroupHeader, 0, m_load /* GroupHeader */, base_asset_id)
//        let group_end := add(cursor, mul(
//          attr(GroupHeader, 0, m_load /* GroupHeader */, user_count),
//          const_add(sizeof(UserAddress), sizeof(Settlement))
//        ))
//
//        /* validate quote_asset_id and base_asset_id */
//        {
//          let asset_count := sload(asset_count_slot)
//          if iszero(and(lt(quote_asset_id, asset_count), lt(base_asset_id, asset_count))) {
//            REVERT(4)
//          }
//        }
//
//        /* ensure there is enough space for the settlement group */
//        if gt(group_end, cursor_end) {
//          REVERT(5)
//        }
//
//        let quote_net := 0
//        let base_net := 0
//
//        let exchange_ptr := EXCHANGE_PTR_(exchange_id)
//        let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, asset_id)
//        let exchange_balance := sload(exchange_balance_ptr)
//
//        /* loop through each settlement */
//        for {} lt(cursor, cursor_end) {} {
//          load /* UserAddress */ := mload(cursor)
//          cursor := add(cursor, sizeof(UserAddress))
//
//          let user_ptr := USER_PTR(attr(UserAddress, 0, tmp_data, user_address))
//
//          /* Stage user's quote/base/fee update and test against limit */
//          let market_state_ptr := MARKET_STATE_PTR(
//            EXCHANGE_SESSION_PTR(user_ptr, exchange_id),
//            quote_asset_id, base_asset_id
//          )
//
//          let market_state_0 := sload(market_state_ptr)
//
//          let settlement_0 := mload(cursor)
//          cursor := add(cursor, sizeof(Settlement))
//
//          let quote_delta := attr(Settlement, 0, settlement_0, quote_delta)
//          CAST_64_NEG(quote_delta)
//
//          let base_delta := attr(Settlement, 0, settlement_0, base_delta)
//          CAST_64_NEG(base_delta)
//
//          quote_net := add(quote_net, quote_delta)
//          base_net := add(base_net, base_delta)
//
//          let fee := attr(Settlement, 0, settlement_0, fee)
//
//          /* Validate Limit */
//          {
//            let market_state_1 := sload(add(market_state_ptr, 1))
//
//            let quote_qty := attr(MarketState, 0, market_state_0, quote_qty)
//            CAST_64_NEG(quote_qty)
//
//            let base_qty := attr(MarketState, 0, market_state_0, base_qty)
//            CAST_64_NEG(base_qty)
//
//
//            quote_qty := add(quote_qty, quote_delta)
//            base_qty := add(base_qty, base_delta)
//
//            if or(INVALID_I64(quote_qty), INVALID_I64(base_qty)) {
//              REVERT(6)
//            }
//
//            /* Should not invalidate min_qty */
//            {
//              let min_quote_qty := attr(MarketState, 1, market_state_1, min_quote_qty)
//              CAST_64_NEG(min_quote_qty)
//
//              let min_base_qty := attr(MarketState, 1, market_state_1, min_base_qty)
//              CAST_64_NEG(min_base_qty)
//
//              if or(slt(quote_qty, min_quote_qty), slt(base_qty, min_base_qty)) {
//                REVERT(6)
//              }
//            }
//
//            /* Check against limit */
//            {
//              /* Check if price fits limit */
//              let negatives := add(slt(quote_qty, 1), mul(slt(base_qty, 1), 2))
//
//              switch negatives
//              /* Both negative */
//              case 3 {
//                /* if one value is non zero, it must be negative */
//                if or(quote_qty, base_qty) {
//                  REVERT(7)
//                }
//              }
//              /* long: quote_qty negative */
//              case 1 {
//                let current_price := div(mul(sub(0, quote_qty), PRICE_UNITS), base_qty)
//                let long_max_price := attr(MarketState, 2, state_data_2, long_max_price)
//
//                if gt(current_price, long_max_price) {
//                  REVERT(8)
//                }
//              }
//              /* short: base_qty negative */
//              case 2 {
//                let current_price := div(mul(quote_qty, PRICE_UNITS), sub(0, base_qty))
//                let short_min_price := attr(MarketState, 2, state_data_2, short_min_price)
//
//                if lt(current_price, short_min_price) {
//                  REVERT(9)
//                }
//              }
//            }
//          }
//
//
//
//
//
//
//
//          let session_ptr := SESSION_PTR(attr(UserAddress, 0, tmp_data, user_address), mload(EXCHANGE_ID_MEM))
//
//          tmp_data /* SettlementData */ := mload(cursor)
//          cursor := add(cursor, sizeof(Settlement))
//
//          let quote_delta := attr(Settlement, 0, tmp_data, quote_delta)
//          let base_delta := attr(Settlement, 0, tmp_data, base_delta)
//          let fees := attr(Settlement, 0, tmp_data, fees)
//
//          CAST_64_NEG(quote_delta)
//          CAST_64_NEG(base_delta)
//
//          quote_net := add(quote_net, quote_delta)
//          base_net := add(base_net, base_delta)
//
//          let quote_state_ptr := pointer(MarketState, session_ptr, mload(QUOTE_ASSET_ID_MEM))
//          let base_state_ptr := pointer(MarketState, session_ptr, base_asset_id)
//
//          /* ensure we're within expire time */
//          {
//            let state_data_1 := sload(add(quote_state_ptr, 1))
//            let unlock_at := attr(QuoteAssetState, 1, state_data_1, unlock_at)
//
//            if gt(timestamp, unlock_at) {
//              REVERT(4)
//            }
//          }
//
//          /* update quote balance */
//          {
//            let state_data_0 := sload(quote_state_ptr)
//
//            let asset_balance := attr(QuoteAssetState, 0, state_data_0, asset_balance)
//            asset_balance := add(asset_balance, quote_delta)
//            asset_balance := sub(asset_balance, fees)
//
//            /* make sure quote balance is positive and doesn't overflow */
//            if gt(asset_balance, U64_MASK) {
//              REVERT(5)
//            }
//
//            let fee_used := attr(QuoteAssetState, 0, state_data_0, fee_used)
//            fee_used := add(fee_used, fees)
//
//            let exchange_fees_mem := EXCHANGE_FEES_MEM
//            mstore(exchange_fees_mem, add(mload(exchange_fees_mem), fees))
//
//            let fee_limit := attr(QuoteAssetState, 0, state_data_0, fee_limit)
//
//            /* ensure don't over spend fee */
//            /* note, also provides overflow check */
//            if gt(fee_used, fee_limit) {
//              REVERT(6)
//            }
//
//            sstore(quote_state_ptr, or(
//              and(state_data_0, mask_out(QuoteAssetState, 0, fee_used, asset_balance)),
//              build(QuoteAssetState, 0, 0, fee_used, 0, asset_balance)
//            ))
//          }
//
//          let quote_qty := 0
//          let base_qty := 0
//          {
//            let state_ptr := pointer(MarketState, session_ptr, base_asset_id)
//            let state_data_0 := sload(state_ptr)
//            let asset_balance := attr(MarketState, 0, state_data_0, asset_balance)
//
//            asset_balance := add(asset_balance, base_delta)
//            if gt(asset_balance, U64_MASK) {
//              REVERT(7)
//            }
//
//            quote_qty := attr(MarketState, 0, state_data_0, quote_qty)
//            base_qty := attr(MarketState, 0, state_data_0, base_qty)
//
//            CAST_64_NEG(quote_qty)
//            CAST_64_NEG(base_qty)
//
//            quote_qty := add(quote_qty, quote_delta)
//            base_qty := add(base_qty, base_delta)
//
//            if or(INVALID_I64(quote_qty), INVALID_I64(base_qty)) {
//              REVERT(8)
//            }
//
//            sstore(state_ptr, or(
//              and(state_data_0, mask_out(MarketState, 0, quote_qty, base_qty, asset_balance)),
//              build(MarketState, 0, quote_qty, base_qty, 0, asset_balance)
//            ))
//          }
//
//        }
//
//        /* ensure net balance is 0 for settlement group */
//        if or(quote_net, base_net) {
//          REVERT(15)
//        }
//      }
//
//      let exchange_fees := mload(EXCHANGE_FEES_MEM)
//
//      let exchange_id := mload(EXCHANGE_ID_MEM)
//      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
//      sstore(add(exchange_ptr, 1), exchange_fees)
//    }
//  }
}
