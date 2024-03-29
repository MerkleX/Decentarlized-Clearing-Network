pragma solidity 0.5.7;

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
#define WORD_14 448 /* 32*14 = 448 */

#define U64_MASK                                                 0xFFFFFFFFFFFFFFFF
#define U64_MAX                                                  0xFFFFFFFFFFFFFFFF
#define I64_MAX                                                  0x7FFFFFFFFFFFFFFF
#define I64_MIN  0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF8000000000000000
#define U256_MAX 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF

#define MIN_UNLOCK_AT 28800 /* 8 hours in seconds */
#define MAX_UNLOCK_AT 1209600 /* 14 days in seconds */
#define TWO_HOURS 7200 /* 2 hours in seconds */
#define TWO_DAYS 172800 /* 2 days in seconds */

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
  event UserCreated(address indexed creator, uint64 user_id);
  event UserTradeAddressUpdated(uint64 user_id);
  event SessionUpdated(uint64 user_id, uint64 exchange_id);
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
  #define EXCHANGE_COUNT 4294967296             /* 2^32 */
  #define ASSET_COUNT    4294967296             /* 2^32 */
  #define USER_COUNT     18446744073709551616   /* 2^64 */
  #define MARKET_COUNT   18446744073709551616   /* 2^64 (2^32 * 2^32 every asset combination) */

  struct Exchange {
    /* 11 byte name of the exchange */
    uint88 name;
    /* prevents exchange from applying settlement groups */
    uint8 locked;
    /* address used to manage exchange */
    address owner;

    /* address to withdraw funds */
    uint256 withdraw_address;

    /* recovery address to change the owner and withdraw address */
    uint256 recovery_address;

    /* a proposed address to change recovery_address */
    uint256 recovery_address_proposed;

    /* asset balances (in session balance units) */
    uint256[ASSET_COUNT] balances;
  }

  struct Asset {
    /* 8 byte symbol of the asset */
    uint64 symbol;
    /* used to scale between wallet and state balances */
    uint192 unit_scale;

    /* address of the ERC-20 Token */
    uint256 contract_address;
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

    /* address used to interact with session */
    uint256 trade_address;

    /* user balances locked with the exchange */
    SessionBalance[ASSET_COUNT] balances;

    /* market states to protect locked balances */
    MarketState[MARKET_COUNT] market_states;
  }

  struct User {
    /* address used to sign trading limits */
    uint256 trade_address;

    /* address used to withdraw funds */
    uint256 withdraw_address;

    /* address used to update trade_address / withdraw_address */
    uint256 recovery_address;

    /* proposed address to update recovery_address */
    uint256 recovery_address_proposed;

    /* balances under the user's control */
    uint256[ASSET_COUNT] balances;

    /* exchange sessions */
    ExchangeSession[EXCHANGE_COUNT] exchange_sessions;
  }

  User[USER_COUNT] users;
  Asset[ASSET_COUNT] assets;
  Exchange[EXCHANGE_COUNT] exchanges;

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

  #define MSTORE_STR(MSTORE_VAR, STR_OFFSET, STR_LEN, STR_DATA) \
    mstore(MSTORE_VAR, STR_OFFSET) \
    mstore(add(MSTORE_VAR, STR_OFFSET), STR_LEN) \
    mstore(add(MSTORE_VAR, const_add(STR_OFFSET, WORD_1)), STR_DATA)

  #define RETURN_0(VALUE) \
    mstore(return_value_mem, VALUE)

  #define RETURN(WORD, VALUE) \
    mstore(add(return_value_mem, WORD), VALUE)

  #define VALID_USER_ID(USER_ID, REVERT_1) \
    { \
      let user_count := sload(user_count_slot) \
      if iszero(lt(USER_ID, user_count)) { \
        REVERT(REVERT_1) \
      } \
    }

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

  #define CAST_64_NEG(variable) \
      variable := signextend(7, variable)

  #define CAST_96_NEG(variable) \
      variable := signextend(11, variable)

  #define U64_OVERFLOW(NUMBER) \
    gt(NUMBER, U64_MAX)

  /* pointer macros */

  #define ASSET_PTR_(ASSET_ID) \
    pointer(Asset, assets_slot, ASSET_ID)

  #define EXCHANGE_PTR_(EXCHANGE_ID) \
    pointer(Exchange, exchanges_slot, EXCHANGE_ID)

  #define EXCHANGE_BALANCE_PTR_(EXCHANGE_PTR, ASSET_ID) \
      pointer(u256, pointer_attr(Exchange, EXCHANGE_PTR, balances), ASSET_ID)

  #define USER_PTR_(USER_ID) \
    pointer(User, users_slot, USER_ID)

  #define USER_BALANCE_PTR_(USER_PTR, ASSET_ID) \
    pointer(u256, pointer_attr(User, USER_PTR, balances), ASSET_ID)

  #define SESSION_PTR_(USER_PTR, EXCHANGE_ID) \
    pointer(ExchangeSession, pointer_attr(User, USER_PTR, exchange_sessions), EXCHANGE_ID)

  #define SESSION_BALANCE_PTR_(SESSION_PTR, ASSET_ID) \
    pointer(SessionBalance, pointer_attr(ExchangeSession, SESSION_PTR, balances), ASSET_ID)

  #define MARKET_IDX(QUOTE_ASSET_ID, BASE_ASSET_ID) \
    or(mul(QUOTE_ASSET_ID, ASSET_COUNT), BASE_ASSET_ID)

  #define MARKET_STATE_PTR_(SESSION_PTR, QUOTE_ASSET_ID, BASE_ASSET_ID) \
    pointer(MarketState, pointer_attr(ExchangeSession, SESSION_PTR, market_states), MARKET_IDX(QUOTE_ASSET_ID, BASE_ASSET_ID))


  /* feature flags to disable functions */

  #define FEATURE_ADD_ASSET 0x1
  #define FEATURE_ADD_EXCHANGE 0x2
  #define FEATURE_CREATE_USER 0x4
  #define FEATURE_EXCHANGE_DEPOSIT 0x8
  #define FEATURE_USER_DEPOSIT 0x10
  #define FEATURE_TRANSFER_TO_SESSION 0x20
  #define FEATURE_DEPOSIT_ASSET_TO_SESSION 0x40
  #define FEATURE_EXCHANGE_TRANSFER_FROM 0x80
  #define FEATURE_EXCHANGE_SET_LIMITS 0x100
  #define FEATURE_APPLY_SETTLEMENT_GROUPS 0x200
  #define FEATURE_USER_MARKET_RESET 0x400
  #define FEATURE_RECOVER_UNSETTLED_WITHDRAWS 0x800
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
      /* call external contract */ \
      { \
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
        switch returndatasize() \
        /* invalid ERC-20 Token, doesn't return anything and didn't revert: success */ \
        case 0 { } \
        /* valid ERC-20 Token, has return value */ \
        case 32 { \ 
          let result := mload(transfer_out_mem) \
          if iszero(result) { \
            REVERT(REVERT_2) \
          } \
        } \
        /* returned a non standard amount of data: fail */ \
        default { \
          REVERT(REVERT_2) \
        } \
      }

  #define ERC_20_DEPOSIT(TOKEN_ADDRESS, FROM_ADDRESS, TO_ADDRESS, AMOUNT, REVERT_1, REVERT_2) \
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
        switch returndatasize() \
        /* invalid ERC-20 Token, doesn't return anything and didn't revert: success */ \
        case 0 { } \
        /* valid ERC-20 Token, has return value */ \
        case 32 { \ 
          let result := mload(transfer_out_mem) \
          if iszero(result) { \
            REVERT(REVERT_2) \
          } \
        } \
        /* returned a non standard amount of data: fail */ \
        default { \
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
  returns (string memory symbol, uint192 unit_scale, address contract_address) {
    uint256[5] memory return_value_mem;

    assembly {
      let asset_count := sload(asset_count_slot)
      if iszero(lt(asset_id, asset_count)) {
        REVERT(1)
      }

      let asset_ptr := ASSET_PTR_(asset_id)
      let asset_0 := sload(asset_ptr)
      let asset_1 := sload(add(asset_ptr, 1))

      MSTORE_STR(return_value_mem, WORD_3, 8, asset_0)
      RETURN(WORD_1, attr(Asset, 0, asset_0, unit_scale))
      RETURN(WORD_2, attr(Asset, 1, asset_1, contract_address))

      return(return_value_mem, const_add(WORD_3, /* string header */ WORD_1 , /* string data */ 8))
    }
  }

  function get_exchange(uint32 exchange_id) public view
  returns (
    string memory name, bool locked, address owner,
    address withdraw_address, address recovery_address, address recovery_address_proposed
  ) {
    /* [ name_offset, owner, withdraw_address, recovery_address, recovery_address_proposed, name_len, name_data(12) ] */
    uint256[8] memory return_value_mem;

    assembly {
      let exchange_count := sload(exchange_count_slot)
      if iszero(lt(exchange_id, exchange_count)) {
        REVERT(1)
      }

      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_0 := sload(exchange_ptr)
      let exchange_1 := sload(add(exchange_ptr, 1))
      let exchange_2 := sload(add(exchange_ptr, 2))
      let exchange_3 := sload(add(exchange_ptr, 3))

      MSTORE_STR(return_value_mem, WORD_6, 11, exchange_0)
      RETURN(WORD_1, attr(Exchange, 0, exchange_0, locked))
      RETURN(WORD_2, attr(Exchange, 0, exchange_0, owner))
      RETURN(WORD_3, attr(Exchange, 1, exchange_1, withdraw_address))
      RETURN(WORD_4, attr(Exchange, 2, exchange_2, recovery_address))
      RETURN(WORD_5, attr(Exchange, 3, exchange_3, recovery_address_proposed))

      return(return_value_mem, const_add(WORD_6, /* string header */ WORD_1, /* string data */ 12))
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

  function get_user_count() public view returns (uint32 count) {
    uint256[1] memory return_value_mem;

    assembly {
      RETURN_0(sload(user_count_slot))
      return(return_value_mem, WORD_1)
    }
  }

  function get_user(uint64 user_id) public view
  returns (
    address trade_address,
    address withdraw_address, address recovery_address, address recovery_address_proposed
  ) {
    uint256[4] memory return_value_mem;

    assembly {
      let user_count := sload(user_count_slot)
      if iszero(lt(user_id, user_count)) {
        REVERT(1)
      }

      let user_ptr := USER_PTR_(user_id)

      RETURN_0(      sload(pointer_attr(User, user_ptr, trade_address)))
      RETURN(WORD_1, sload(pointer_attr(User, user_ptr, withdraw_address)))
      RETURN(WORD_2, sload(pointer_attr(User, user_ptr, recovery_address)))
      RETURN(WORD_3, sload(pointer_attr(User, user_ptr, recovery_address_proposed)))

      return(return_value_mem, WORD_4)
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

  function get_session(uint64 user_id, uint32 exchange_id) public view
  returns (uint256 unlock_at, address trade_address) {
    uint256[2] memory return_value_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)

      RETURN_0(sload(pointer_attr(ExchangeSession, session_ptr, unlock_at)))
      RETURN(WORD_1, sload(pointer_attr(ExchangeSession, session_ptr, trade_address)))
      return(return_value_mem, WORD_2)
    }
  }

  function get_session_balance(uint64 user_id, uint32 exchange_id, uint32 asset_id) public view
  returns (uint128 total_deposit, uint64 unsettled_withdraw_total, uint64 asset_balance) {
    uint256[3] memory return_value_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, asset_id)
      let session_balance_0 := sload(session_balance_ptr)

      RETURN_0(attr(SessionBalance, 0, session_balance_0, total_deposit))
      RETURN(WORD_1, attr(SessionBalance, 0, session_balance_0, unsettled_withdraw_total))
      RETURN(WORD_2, attr(SessionBalance, 0, session_balance_0, asset_balance))

      return(return_value_mem, WORD_3)
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
      let exchange_state_ptr := MARKET_STATE_PTR_(exchange_session_ptr, quote_shift, base_shift)

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

  #define CREATOR_REQUIRED(REVERT_1) \
    { \
      let creator := sload(creator_slot) \
      if iszero(eq(caller, creator)) { \
        REVERT(REVERT_1) \
      } \
    }

  /************************************
   * Security Feature Lock Management *
   *
   * Taking a defensive approach, these functions allow
   * the creator to turn off DCN functionality. The intent
   * is to allow the creator to disable features if an exploit
   * or logic issue is found. The creator should not be able to
   * restrict a user's ability to withdraw their assets.
   *
   ************************************/

  /**
   * Security Lock
   *
   * Sets which features should be locked/disabled.
   * 
   * @param lock_features: bit flags for each feature to be locked
   */
  function security_lock(uint256 lock_features) public {
    assembly {
      CREATOR_REQUIRED(/* REVERT(1) */ 1)

      let locked_features := sload(security_locked_features_slot)
      sstore(security_locked_features_slot, or(locked_features, lock_features))

      /*
       * Sets the proposal to block all features. This is done to
       * ensure security_set_proposed() does not unlock features.
       */
      sstore(security_locked_features_proposed_slot, FEATURE_ALL)
    }
  }

  /**
   * Security Propose
   *
   * Propose which features should be locked. If a feature is unlocked,
   * should require a timeout before the proposal can be applied.
   *
   * @param proposed_locked_features: bit flags for proposal
   */
  function security_propose(uint256 proposed_locked_features) public {
    assembly {
      CREATOR_REQUIRED(/* REVERT(1) */ 1)

      /*
       * only update security_proposed_unlock_timestamp if
       * proposed_locked_features unlocks a new features
       */

      /*
       * Example: current_proposal = 0b11111111
       *         proposed_features = 0b00010000
       *         differences = XOR = 0b11101111
       */

      let current_proposal := sload(security_locked_features_proposed_slot)
      let proposed_differences := xor(current_proposal, proposed_locked_features)

      /*
       * proposed_differences will have "1" in feature positions that have changed.
       * Want to see if those positions have proposed_locked_features as "0", meaning
       * that those features will be unlocked.
       */
      
      let does_unlocks_features := and(proposed_differences,
                                       not(proposed_locked_features))

      /* update unlock_timestamp */
      if does_unlocks_features {
        sstore(security_proposed_unlock_timestamp_slot, add(timestamp, TWO_DAYS))
      }

      sstore(security_locked_features_proposed_slot, proposed_locked_features)
    }
  }

  /** Security Set Proposed
   *
   * Applies the proposed security locks if the timestamp is met.
   */
  function security_set_proposed() public {
    assembly {
      CREATOR_REQUIRED(/* REVERT(1) */ 1)

      let unlock_timestamp := sload(security_proposed_unlock_timestamp_slot) 
      if gt(unlock_timestamp, timestamp) {
        REVERT(2)
      }

      sstore(security_locked_features_slot,
             sload(security_locked_features_proposed_slot))
    }
  }

  /**********************
   * Creator Management *
   *
   * Allows creator to update keys
   *
   * creator_update
   *    caller = recovery address
   *    updates primary address
   * creator_propose_recovery
   *    caller = recovery address
   *    sets proposed recovery address
   * creator_set_recovery
   *    caller = proposed recovery address
   *    sets recovery from proposed
   *
   * Recovery update is done in these steps to ensure
   * value is set to valid address.
   *
   **********************/

  function creator_update(address new_creator) public {
    assembly {
      let creator_recovery := sload(creator_recovery_slot)
      if iszero(eq(caller, creator_recovery)) {
        REVERT(1)
      }

      sstore(creator_slot, new_creator)
    }
  }

  function creator_propose_recovery(address recovery) public {
    assembly {
      let creator_recovery := sload(creator_recovery_slot)
      if iszero(eq(caller, creator_recovery)) {
        REVERT(1)
      }

      sstore(creator_recovery_proposed_slot, recovery)
    }
  }

  function creator_set_recovery() public {
    assembly {
      let creator_recovery_proposed := sload(creator_recovery_proposed_slot)
      if or(iszero(eq(caller, creator_recovery_proposed)), iszero(caller)) {
        REVERT(1)
      }
      sstore(creator_recovery_slot, caller)
      sstore(creator_recovery_proposed_slot, 0)
    }
  }

  /**
   * Set Exchange Locked
   *
   * Allows the creator to lock bad acting exchanges.
   *
   * @param exchange_id
   * @param locked: the desired locked state
   */
  function set_exchange_locked(uint32 exchange_id, bool locked) public {
    assembly {
      CREATOR_REQUIRED(/* REVERT(1) */ 1)
      VALID_EXCHANGE_ID(exchange_id, /* REVERT(2) */ 2)

      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_0 := sload(exchange_ptr)
      sstore(exchange_ptr, or(
        and(mask_out(Exchange, 0, locked), exchange_0),
        build(Exchange, 0,
              /* name */ 0,
              /* locked */ locked)
      ))
    }
  }

  /**
   * User Create
   *
   * Unrestricted function to create a new user. An event is
   * emitted to determine the user_id.
   */
  function user_create() public returns (uint64 user_id) {
    uint256[2] memory log_data_mem;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_CREATE_USER, /* REVERT(0) */ 0)

      user_id := sload(user_count_slot)
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

      log_event(UserCreated, log_data_mem, caller, user_id)
    }
  }

  /*******************
   * User Management *
   *
   * user_set_trade_address
   *    caller = recovery_address
   *    update user's trade address
   * user_set_withdraw_address
   *    caller = recovery_address
   *    update user's withdraw address
   * user_propose_recovery_address
   *    caller = recovery_address
   *    propose recovery address
   * user_set_recovery_address
   *    caller = recovery_address_proposed
   *    set recovery adress from proposed
   *
   *******************/

  function user_set_trade_address(uint64 user_id, address trade_address) public {
    uint256[1] memory log_data_mem;

    assembly {
      let user_ptr := USER_PTR_(user_id)

      let recovery_address := sload(pointer_attr(User, user_ptr, recovery_address))
      if iszero(eq(caller, recovery_address)) {
        REVERT(1)
      }

      sstore(pointer_attr(User, user_ptr, trade_address),
             trade_address)

      log_event(UserTradeAddressUpdated, log_data_mem, user_id)
    }
  }

  function user_set_withdraw_address(uint64 user_id, address withdraw_address) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)

      let recovery_address := sload(pointer_attr(User, user_ptr, recovery_address))
      if iszero(eq(caller, recovery_address)) {
        REVERT(1)
      }

      sstore(pointer_attr(User, user_ptr, withdraw_address),
             withdraw_address)
    }
  }

  function user_propose_recovery_address(uint64 user_id, address proposed) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)

      let recovery_address := sload(pointer_attr(User, user_ptr, recovery_address))
      if iszero(eq(caller, recovery_address)) {
        REVERT(1)
      }

      sstore(pointer_attr(User, user_ptr, recovery_address_proposed),
             proposed)
    }
  }

  function user_set_recovery_address(uint64 user_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)

      let proposed_ptr := pointer_attr(User, user_ptr, recovery_address_proposed)
      let recovery_address_proposed := sload(proposed_ptr)
      if iszero(eq(caller, recovery_address_proposed)) {
        REVERT(1)
      }

      sstore(proposed_ptr, 0)
      sstore(pointer_attr(User, user_ptr, recovery_address),
             recovery_address_proposed)
    }
  }

  /***********************
   * Exchange Management *
   *
   * exchange_set_owner
   *    caller = recovery_address
   *    set primary address
   * exchange_set_withdraw
   *    caller = recovery_address
   *    set withdraw address
   * exchange_propose_recovery
   *    caller = recovery_address
   *    set propose recovery address
   * exchange_set_recovery
   *    caller = recovery_address_proposed
   *    set recovery_address from proposed
   *
   ***********************/

  function exchange_set_owner(uint32 exchange_id, address new_owner) public {
    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_recovery := sload(pointer_attr(Exchange, exchange_ptr, recovery_address))

      /* ensure caller is recovery */
      if iszero(eq(caller, exchange_recovery)) {
        REVERT(1)
      }

      let exchange_0 := sload(exchange_ptr)
      sstore(exchange_ptr, or(
        and(exchange_0, mask_out(Exchange, 0, owner)),
        build(Exchange, 0,
              /* name */ 0,
              /* locked */ 0,
              /* owner */ new_owner
             )
      ))
    }
  }

  function exchange_set_withdraw(uint32 exchange_id, address new_withdraw) public {
    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_recovery := sload(pointer_attr(Exchange, exchange_ptr, recovery_address))

      /* ensure caller is recovery */
      if iszero(eq(caller, exchange_recovery)) {
        REVERT(1)
      }

      sstore(pointer_attr(Exchange, exchange_ptr, withdraw_address), new_withdraw)
    }
  }

  function exchange_propose_recovery(uint32 exchange_id, address proposed) public {
    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_recovery := sload(pointer_attr(Exchange, exchange_ptr, recovery_address))

      /* ensure caller is proposed */
      if iszero(eq(caller, exchange_recovery)) {
        REVERT(1)
      }

      /* update proposed */
      sstore(pointer_attr(Exchange, exchange_ptr, recovery_address_proposed),
             proposed)
    }
  }

  function exchange_set_recovery(uint32 exchange_id) public {
    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let exchange_recovery_proposed := sload(pointer_attr(Exchange, exchange_ptr, recovery_address_proposed))

      /* ensure caller is proposed recovery */
      if or(iszero(eq(caller, exchange_recovery_proposed)), iszero(caller)) {
        REVERT(1)
      }

      /* update recovery */
      sstore(pointer_attr(Exchange, exchange_ptr, recovery_address), caller)
    }
  }

  /**
   * Add Asset
   *
   * caller = creator
   * Note, it is possible for two assets to have the same contract_address.
   *
   * @param symbol: 4 character symbol for asset
   * @param unit_scale: (ERC20 balance) = unit_scale * (session balance)
   * @param contract_address: address on the ERC20 token
   */
  function add_asset(string memory symbol, uint192 unit_scale, address contract_address) public returns (uint64 asset_id) {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_ADD_ASSET, /* REVERT(0) */ 0)
      CREATOR_REQUIRED(/* REVERT(1) */ 1)

      /* do not want to overflow assets array */
      asset_id := sload(asset_count_slot)
      if iszero(lt(asset_id, ASSET_COUNT)) {
        REVERT(2)
      }

      /* Symbol must be 8 characters */
      let symbol_len := mload(symbol)
      if iszero(eq(symbol_len, 8)) {
        REVERT(3)
      }

      /* Unit scale must be non-zero */
      if iszero(unit_scale) {
        REVERT(4)
      }

      /* Contract address should be non-zero */
      if iszero(contract_address) {
        REVERT(5)
      }

      let asset_symbol := mload(add(symbol, WORD_1 /* offset as first word is size */))

      /* Note, symbol is already shifted not setting it in build */
      let asset_data_0 := or(asset_symbol, build(Asset, 0, /* symbol */ 0, unit_scale))
      let asset_ptr := ASSET_PTR_(asset_id)

      sstore(asset_ptr, asset_data_0)
      sstore(add(asset_ptr, 1), contract_address)
      sstore(asset_count_slot, add(asset_id, 1))
    }
  }

  /**
   * Add Exchange
   *
   * caller = creator
   *
   * @param name: 11 character name of exchange
   * @param addr: address of exchange
   */
  function add_exchange(string memory name, address addr) public returns (uint64 exchange_id) {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_ADD_EXCHANGE, /* REVERT(0) */ 0)
      CREATOR_REQUIRED(/* REVERT(1) */ 1)

      /* Name must be 11 bytes long */
      let name_len := mload(name)
      if iszero(eq(name_len, 11)) {
        REVERT(2)
      }

      /* Do not overflow exchanges */
      exchange_id := sload(exchange_count_slot)
      if iszero(lt(exchange_id, EXCHANGE_COUNT)) {
        REVERT(3)
      }

      let exchange_ptr := EXCHANGE_PTR_(exchange_id)

      /* 
       * name is at start of the word. After loading it is already shifted
       * so add to it rather than shifting twice with build
       */

      let name_data := mload(add(name, WORD_1 /* shift, first word is length */))

      let exchange_0 := or(
        name_data,
        build(Exchange, 0,
              /* space for name */ 0,
              /* locked */ 0,
              /* owner */ addr)
      )
      sstore(exchange_ptr, exchange_0)

      /* Store owner withdraw */
      sstore(pointer_attr(Exchange, exchange_ptr, withdraw_address), addr)

      /* Store owner recovery */
      sstore(pointer_attr(Exchange, exchange_ptr, recovery_address), addr)

      /* Update exchange count */
      sstore(exchange_count_slot, add(exchange_id, 1))
    }
  }

  /**
   * Exchange Withdraw
   *
   * @param quantity: in session balance units
   */
  function exchange_withdraw(uint32 exchange_id, uint32 asset_id,
                             address destination, uint64 quantity) public {
    uint256[3] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)

      /* ensure caller is withdraw_address */
      let withdraw_address := sload(pointer_attr(Exchange, exchange_ptr, withdraw_address))
      if iszero(eq(caller, withdraw_address)) {
        REVERT(1)
      }

      let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, asset_id)
      let exchange_balance := sload(exchange_balance_ptr)

      /* insufficient funds */
      if gt(quantity, exchange_balance) {
        REVERT(2)
      }

      /* decrement balance */
      sstore(exchange_balance_ptr, sub(exchange_balance, quantity))

      let asset_ptr := ASSET_PTR_(asset_id)
      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
      let asset_address := sload(pointer_attr(Asset, asset_ptr, contract_address))

      let withdraw := mul(quantity, unit_scale)

      ERC_20_SEND(
        /* TOKEN_ADDRESS */ asset_address,
        /* TO_ADDRESS */ destination,
        /* AMOUNT */ withdraw,
        /* REVERT(3) */ 3,
        /* REVERT(4) */ 4
      )
    }
  }

  /**
   * Exchange Deposit
   *
   * @param quantity: in session balance units
   */
  function exchange_deposit(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[3] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_DEPOSIT, /* REVERT(0) */ 0)
      VALID_EXCHANGE_ID(exchange_id, /* REVERT(1) */ 1)
      VALID_ASSET_ID(asset_id, /* REVERT(2) */ 2)

      let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(EXCHANGE_PTR_(exchange_id), asset_id)
      let exchange_balance := sload(exchange_balance_ptr)

      let updated_balance := add(exchange_balance, quantity)
      if U64_OVERFLOW(updated_balance) {
        REVERT(3)
      }

      let asset_ptr := ASSET_PTR_(asset_id)
      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
      let asset_address := sload(pointer_attr(Asset, asset_ptr, contract_address))

      let deposit := mul(quantity, unit_scale)

      sstore(exchange_balance_ptr, updated_balance)

      ERC_20_DEPOSIT(
        /* TOKEN_ADDRESS */ asset_address,
        /* FROM_ADDRESS */ caller,
        /* TO_ADDRESS */ address,
        /* AMOUNT */ deposit,
        /* REVERT(4) */ 4,
        /* REVERT(5) */ 5
      )
    }
  }

  /**
   * User Deposit
   *
   * Deposit funds in the user's DCN balance
   *
   * @param amount: in ERC20 balance units
   */
  function user_deposit(uint64 user_id, uint32 asset_id, uint256 amount) public {
    uint256[4] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_USER_DEPOSIT, /* REVERT(0) */ 0)
      VALID_USER_ID(user_id, /* REVERT(1) */ 1)
      VALID_ASSET_ID(asset_id, /* REVERT(2) */ 2)

      if iszero(amount) {
        stop()
      }

      let balance_ptr := USER_BALANCE_PTR_(USER_PTR_(user_id), asset_id)
      let current_balance := sload(balance_ptr)

      let proposed_balance := add(current_balance, amount)

      /* Prevent overflow */
      if lt(proposed_balance, current_balance) {
        REVERT(3)
      }

      let asset_address := sload(pointer_attr(Asset, ASSET_PTR_(asset_id), contract_address))
      sstore(balance_ptr, proposed_balance)

      ERC_20_DEPOSIT(
        /* TOKEN_ADDRESS */ asset_address,
        /* FROM_ADDRESS */ caller,
        /* TO_ADDRESS */ address,
        /* AMOUNT */ amount,
        /* REVERT(4) */ 4,
        /* REVERT(5) */ 5
      )
    }
  }

  /**
   * User Withdraw
   *
   * caller = user's withdraw_address
   *
   * Withdraw from user's DCN balance
   *
   * @param amount: in ERC20 balance units
   */
  function user_withdraw(uint64 user_id, uint32 asset_id, address destination, uint256 amount) public {
    uint256[3] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      if iszero(amount) {
        stop()
      }

      VALID_USER_ID(user_id, /* REVERT(6) */ 6)
      VALID_ASSET_ID(asset_id, /* REVERT(1) */ 1)

      let user_ptr := USER_PTR_(user_id)
      let withdraw_address := sload(pointer_attr(User, user_ptr, withdraw_address))

      if iszero(eq(caller, withdraw_address)) {
        REVERT(2)
      }

      let balance_ptr := USER_BALANCE_PTR_(user_ptr, asset_id)
      let current_balance := sload(balance_ptr)

      /* insufficient funds */
      if lt(current_balance, amount) {
        REVERT(3)
      }

      sstore(balance_ptr, sub(current_balance, amount))

      let asset_address := sload(pointer_attr(Asset, ASSET_PTR_(asset_id), contract_address))

      ERC_20_SEND(
        /* TOKEN_ADDRESS */ asset_address,
        /* TO_ADDRESS */ destination,
        /* AMOUNT */ amount,
        /* REVERT(4) */ 4,
        /* REVERT(5) */ 5
      )
    }
  }

  /**
   * User Session Set Unlock At
   *
   * caller = user's trade_address
   *
   * Update the unlock_at timestamp. Also updates the trade_address
   * if the session is expired. Note, the trade_address should not be
   * updatable when the session is active.
   */
  function user_session_set_unlock_at(uint64 user_id, uint32 exchange_id, uint256 unlock_at) public {
    uint256[3] memory log_data_mem;

    assembly {
      VALID_EXCHANGE_ID(exchange_id, /* REVERT(1) */ 1)

      let user_ptr := USER_PTR_(user_id)
      let trade_address := sload(pointer_attr(User, user_ptr, trade_address))

      if iszero(eq(caller, trade_address)) {
        REVERT(2)
      }

      /* validate time range of unlock_at */
      {
        let fails_min_time := lt(unlock_at, add(timestamp, MIN_UNLOCK_AT))
        let fails_max_time := gt(unlock_at, add(timestamp, MAX_UNLOCK_AT))

        if or(fails_min_time, fails_max_time) {
          REVERT(3)
        }
      }

      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)

      /* only update trade_address when unlocked */
      let unlock_at_ptr := pointer_attr(ExchangeSession, session_ptr, unlock_at)
      if lt(sload(unlock_at_ptr), timestamp) {
        sstore(pointer_attr(ExchangeSession, session_ptr, trade_address), caller)
      }

      sstore(unlock_at_ptr, unlock_at)
      log_event(SessionUpdated, log_data_mem, user_id, exchange_id)
    }
  }

  /**
   * User Market Reset
   *
   * caller = user's trade_address
   *
   * Allows the user to reset their session with an exchange.
   * Only allowed when session in unlocked.
   * Persists limit_version to prevent old limits from being applied.
   */
  function user_market_reset(uint64 user_id, uint32 exchange_id,
                             uint32 quote_asset_id, uint32 base_asset_id) public {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_USER_MARKET_RESET, /* REVERT(0) */ 0)
      VALID_EXCHANGE_ID(exchange_id, /* REVERT(1) */ 1)

      let user_ptr := USER_PTR_(user_id)
      let trade_address := sload(pointer_attr(User, user_ptr, trade_address))
      if iszero(eq(caller, trade_address)) {
        REVERT(2)
      }

      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let unlock_at := sload(pointer_attr(ExchangeSession, session_ptr, unlock_at))
      if gt(unlock_at, timestamp) {
        REVERT(3)
      }

      let market_state_ptr := MARKET_STATE_PTR_(session_ptr, quote_asset_id, base_asset_id)

      sstore(market_state_ptr, 0)
      sstore(add(market_state_ptr, 1), 0)

      /* increment limit_version */
      let market_state_2_ptr := add(market_state_ptr, 2)
      let market_state_2 := sload(market_state_2_ptr)
      let limit_version := add(attr(MarketState, 2, market_state_2, limit_version), 1)

      sstore(market_state_2_ptr, build(MarketState, 2, 
        /* limit_version */ limit_version
      ))
    }
  }

  /**
   * Transfer To Session
   *
   * caller = user's withdraw_address
   *
   * Transfer funds from DCN balance to trading session
   *
   * @param quantity: in session balance units
   */
  function transfer_to_session(uint64 user_id, uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[4] memory log_data_mem;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_TRANSFER_TO_SESSION, /* REVERT(0) */ 0)
      VALID_EXCHANGE_ID(exchange_id, /* REVERT(1) */ 1)
      VALID_ASSET_ID(asset_id, /* REVERT(2) */ 2)

      if iszero(quantity) {
        stop()
      }

      let asset_ptr := ASSET_PTR_(asset_id)
      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
      let scaled_quantity := mul(quantity, unit_scale)

      let user_ptr := USER_PTR_(user_id)

      /* ensure caller is withdraw_address as funds are moving out of DCN account */
      {
        let withdraw_address := sload(pointer_attr(User, user_ptr, withdraw_address))
        if iszero(eq(caller, withdraw_address)) {
          REVERT(3)
        }
      }

      let user_balance_ptr := USER_BALANCE_PTR_(user_ptr, asset_id)
      let user_balance := sload(user_balance_ptr)

      /* insufficient funds */
      if lt(user_balance, scaled_quantity) {
        REVERT(4)
      }

      /* load exchange balance */
      let session_balance_ptr := SESSION_BALANCE_PTR_(SESSION_PTR_(user_ptr, exchange_id), asset_id)
      let session_balance_0 := sload(session_balance_ptr)

      let updated_exchange_balance := add(attr(SessionBalance, 0, session_balance_0, asset_balance), quantity)
      if U64_OVERFLOW(updated_exchange_balance) {
        REVERT(5)
      }

      /* don't care about overflow for total_deposit, is used by exchange to detect update */
      let updated_total_deposit := add(attr(SessionBalance, 0, session_balance_0, total_deposit), quantity)

      /* update user balance */
      sstore(user_balance_ptr, sub(user_balance, scaled_quantity))

      /* update exchange balance */
      sstore(session_balance_ptr, or(
        and(mask_out(SessionBalance, 0, total_deposit, asset_balance), session_balance_0),
        build(SessionBalance, 0,
              /* total_deposit */ updated_total_deposit,
              /* unsettled_withdraw_total */ 0,
              /* asset_balance */ updated_exchange_balance)
      ))

      log_event(ExchangeDeposit, log_data_mem, user_id, exchange_id, asset_id)
    }
  }

  /**
   * Transfer From Session
   *
   * caller = user's trade_address
   *
   * When session is unlocked, allow transfer funds from trading session to DCN balance.
   *
   * @param quantity: in session balance units
   */
  function transfer_from_session(uint64 user_id, uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[4] memory log_data_mem;

    assembly {
      if iszero(quantity) {
        stop()
      }

      VALID_EXCHANGE_ID(exchange_id, /* REVERT(1) */ 1)
      VALID_ASSET_ID(asset_id, /* REVERT(2) */ 2)

      let user_ptr := USER_PTR_(user_id)

      {
        let trade_address := sload(pointer_attr(User, user_ptr, trade_address))
        if iszero(eq(caller, trade_address)) {
          REVERT(3)
        }
      }

      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)

      /* ensure session in unlocked */
      {
        let session_0 := sload(session_ptr)
        let unlock_at := attr(ExchangeSession, 0, session_0, unlock_at)

        if gt(unlock_at, timestamp) {
          REVERT(4)
        }
      }

      /* load exchange balance */
      let session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, asset_id)
      let session_balance_0 := sload(session_balance_ptr)
      let session_balance := attr(SessionBalance, 0, session_balance_0, asset_balance)

      /* insufficient funds */
      if gt(quantity, session_balance) {
        REVERT(5)
      }

      let updated_exchange_balance := sub(session_balance, quantity)
      let unsettled_withdraw_total := attr(SessionBalance, 0, session_balance_0, unsettled_withdraw_total)

      /* do not let user withdraw money owed to the exchange */
      if lt(updated_exchange_balance, unsettled_withdraw_total) {
        REVERT(6)
      }

      sstore(session_balance_ptr, or(
        and(mask_out(SessionBalance, 0, asset_balance), session_balance_0),
        build(SessionBalance, 0,
              /* total_deposit */ 0,
              /* unsettled_withdraw_total */ 0,
              /* asset_balance */ updated_exchange_balance)
      ))

      let asset_ptr := ASSET_PTR_(asset_id)
      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
      let scaled_quantity := mul(quantity, unit_scale)

      let user_balance_ptr := USER_BALANCE_PTR_(user_ptr, asset_id)
      let user_balance := sload(user_balance_ptr)

      let updated_user_balance := add(user_balance, scaled_quantity)
      /* protect against addition overflow */
      if lt(updated_user_balance, user_balance) {
        REVERT(7)
      }

      sstore(user_balance_ptr, updated_user_balance)
    }
  }

  /**
   * User Deposit To Session
   *
   * Deposits funds directly into a trading session with an exchange.
   *
   * @param quantity: in session balance units
   */
  function user_deposit_to_session(uint64 user_id, uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[4] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;
    uint256[3] memory log_data_mem;

    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_DEPOSIT_ASSET_TO_SESSION, /* REVERT(0) */ 0)

      VALID_EXCHANGE_ID(exchange_id, /* REVERT(1) */ 1)
      VALID_ASSET_ID(asset_id, /* REVERT(2) */ 2)

      if iszero(quantity) {
        stop()
      }

      let session_balance_ptr := SESSION_BALANCE_PTR_(SESSION_PTR_(USER_PTR_(user_id), exchange_id), asset_id)
      let session_balance_0 := sload(session_balance_ptr)

      let updated_exchange_balance := add(attr(SessionBalance, 0, session_balance_0, asset_balance), quantity)
      if U64_OVERFLOW(updated_exchange_balance) {
        REVERT(3)
      }

      let asset_ptr := ASSET_PTR_(asset_id)
      let unit_scale := attr(Asset, 0, sload(asset_ptr), unit_scale)
      let asset_address := sload(pointer_attr(Asset, asset_ptr, contract_address))

      let scaled_quantity := mul(quantity, unit_scale)

      let updated_total_deposit := add(attr(SessionBalance, 0, session_balance_0, total_deposit), quantity)

      /* update exchange balance */
      sstore(session_balance_ptr, or(
        and(mask_out(SessionBalance, 0, total_deposit, asset_balance), session_balance_0),
        build(SessionBalance, 0,
              /* total_deposit */ updated_total_deposit,
              /* unsettled_withdraw_total */ 0,
              /* asset_balance */ updated_exchange_balance)
      ))

      ERC_20_DEPOSIT(
        /* TOKEN_ADDRESS */ asset_address,
        /* FROM_ADDRESS */ caller,
        /* TO_ADDRESS */ address,
        /* AMOUNT */ scaled_quantity,
        /* REVERT(4) */ 4,
        /* REVERT(5) */ 5
      )

      log_event(ExchangeDeposit, log_data_mem, user_id, exchange_id, asset_id)
    }
  }

  struct UnsettledWithdrawHeader {
    uint32 exchange_id;
    uint32 asset_id;
    uint32 user_count;
  }

  struct UnsettledWithdrawUser {
    uint64 user_id;
  }

  /**
   * Recover Unsettled Withdraws
   *
   * exchange_transfer_from allows users to pull unsettled
   * funds from the exchange's balance. This is tracked by
   * unsettled_withdraw_total in SessionBalance. This function
   * returns funds to the Exchange.
   *
   * @param data, a binary payload
   *    - UnsettledWithdrawHeader
   *    - 1st UnsettledWithdrawUser
   *    - 2nd UnsettledWithdrawUser
   *    - ...
   *    - (UnsettledWithdrawHeader.user_count)th UnsettledWithdrawUser
   */
  function recover_unsettled_withdraws(bytes memory data) public {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_RECOVER_UNSETTLED_WITHDRAWS, /* REVERT(0) */ 0)

      let data_len := mload(data)
      let cursor := add(data, WORD_1)
      let cursor_end := add(cursor, data_len)

      for {} lt(cursor, cursor_end) {} {
        let unsettled_withdraw_header_0 := mload(cursor)

        let exchange_id := attr(UnsettledWithdrawHeader, 0, unsettled_withdraw_header_0, exchange_id)
        let asset_id := attr(UnsettledWithdrawHeader, 0, unsettled_withdraw_header_0, asset_id)
        let user_count := attr(UnsettledWithdrawHeader, 0, unsettled_withdraw_header_0, user_count)

        let group_end := add(
          cursor, add(
            sizeof(UnsettledWithdrawHeader),
            mul(user_count, sizeof(UnsettledWithdrawUser))
        ))

        if gt(group_end, cursor_end) {
          REVERT(1)
        }

        let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(EXCHANGE_PTR_(exchange_id), asset_id)
        let exchange_balance := sload(exchange_balance_ptr)
        let start_exchange_balance := exchange_balance

        for {} lt(cursor, group_end) { cursor := add(cursor, sizeof(UnsettledWithdrawUser)) } {
          let user_id := attr(UnsettledWithdrawUser, 0, mload(cursor), user_id)

          let session_balance_ptr := SESSION_BALANCE_PTR_(SESSION_PTR_(USER_PTR_(user_id), exchange_id), asset_id)
          let session_balance_0 := sload(session_balance_ptr)

          let asset_balance := attr(SessionBalance, 0, session_balance_0, asset_balance)
          let unsettled_balance := attr(SessionBalance, 0, session_balance_0, unsettled_withdraw_total)
          let to_recover := unsettled_balance

          if gt(to_recover, asset_balance) {
            to_recover := asset_balance
          }

          /* non zero */
          if to_recover {
            exchange_balance := add(exchange_balance, to_recover)
            asset_balance := sub(asset_balance, to_recover)
            unsettled_balance := sub(unsettled_balance, to_recover)

            /* ensure exchange_balance doesn't overflow */
            if gt(start_exchange_balance, exchange_balance) {
              REVERT(2)
            }

            sstore(session_balance_ptr, or(
              and(mask_out(SessionBalance, 0, unsettled_withdraw_total, asset_balance), session_balance_0),
              build(
                SessionBalance, 0,
                /* total_deposit */ 0,
                /* unsettled_withdraw_total */ unsettled_balance,
                /* asset_balance */ asset_balance)
            ))
          }
        }

        sstore(exchange_balance_ptr, exchange_balance)
      }
    }
  }

  struct ExchangeTransfersHeader {
    uint32 exchange_id;
  }

  struct ExchangeTransferGroup {
    uint32 asset_id;
    uint8 allow_overdraft;
    uint8 transfer_count;
  }

  struct ExchangeTransfer {
    uint64 user_id;
    uint64 quantity;
  }

  #define CURSOR_LOAD(TYPE, REVERT_1) \
    mload(cursor) \
    cursor := add(cursor, sizeof(TYPE)) \
    if gt(cursor, cursor_end) { \
      REVERT(REVERT_1) \
    }

  /**
   * Exchange Transfer From
   *
   * caller = exchange owner
   *
   * Gives the exchange the ability to move funds from a user's
   * trading session into the user's DCN balance. This should be
   * the only way funds can be withdrawn from a trading session
   * while it is locked.
   *
   * Exchange has the option to allow_overdraft. If the user's
   * balance in the tradng_session is not enough to cover the
   * target withdraw quantity, the exchange's funds will be used.
   * These funds can be repaid with a call to recover_unsettled_withdraws.
   *
   * @param data, a binary payload
   *    - ExchangeTransfersHeader
   *    - 1st ExchangeTransferGroup
   *      - 1st ExchangeTransfer
   *      - 2nd ExchangeTransfer
   *      - ...
   *      - (ExchangeTransferGroup.transfer_count)th ExchangeTransfer
   *    - 2nd ExchangeTransferGroup
   *      - 1st ExchangeTransfer
   *      - 2nd ExchangeTransfer
   *      - ...
   *      - (ExchangeTransferGroup.transfer_count)th ExchangeTransfer
   *    - ...
   */
  function exchange_transfer_from(bytes memory data) public {
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_TRANSFER_FROM, /* REVERT(0) */ 0)

      let data_len := mload(data)
      let cursor := add(data, WORD_1)
      let cursor_end := add(cursor, data_len)

      /* load exchange_id */
      let header_0 := CURSOR_LOAD(ExchangeTransfersHeader, /* REVERT(1) */ 1)
      let exchange_id := attr(ExchangeTransfersHeader, 0, header_0, exchange_id)
      VALID_EXCHANGE_ID(exchange_id, /* REVERT(2) */ 2)

      {
        /* ensure exchange is caller */
        let exchange_data := sload(EXCHANGE_PTR_(exchange_id))
        if iszero(eq(caller, attr(Exchange, 0, exchange_data, owner))) {
          REVERT(3)
        }

        /* ensure exchange is not locked */
        if attr(Exchange, 0, exchange_data, locked) {
          REVERT(4)
        }
      }

      let asset_count := sload(asset_count_slot)

      for {} lt(cursor, cursor_end) {} {
        let group_0 := CURSOR_LOAD(ExchangeTransferGroup, /* REVERT(5) */ 5)
        let asset_id := attr(ExchangeTransferGroup, 0, group_0, asset_id)

        /* Validate asset id */
        if iszero(lt(asset_id, asset_count)) {
          REVERT(6)
        }

        let disallow_overdraft := iszero(attr(ExchangeTransferGroup, 0, group_0, allow_overdraft))
        let cursor_group_end := add(cursor, mul(
          attr(ExchangeTransferGroup, 0, group_0, transfer_count),
          sizeof(ExchangeTransfer)
        ))

        /* ensure data fits in input */
        if gt(cursor_group_end, cursor_end) {
          REVERT(7)
        }

        let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(EXCHANGE_PTR_(exchange_id), asset_id)
        let exchange_balance_remaining := sload(exchange_balance_ptr)

        let unit_scale := attr(Asset, 0, sload(ASSET_PTR_(asset_id)), unit_scale)
        
        for {} lt(cursor, cursor_group_end) { cursor := add(cursor, sizeof(ExchangeTransfer)) } {
          let transfer_0 := mload(cursor)

          let user_ptr := USER_PTR_(attr(ExchangeTransfer, 0, transfer_0, user_id))
          let quantity := attr(ExchangeTransfer, 0, transfer_0, quantity)

          let exchange_balance_used := 0

          let session_balance_ptr := SESSION_BALANCE_PTR_(SESSION_PTR_(user_ptr, exchange_id), asset_id)
          let session_balance_0 := sload(session_balance_ptr)
          let session_balance := attr(SessionBalance, 0, session_balance_0, asset_balance)

          let session_balance_updated := sub(session_balance, quantity)

          /*
           * check for underflow (quantity > user_exchange_balance),
           * then need to dip into exchange_balance_remaining if user_exchange_balance isn't enough
           */
          if gt(session_balance_updated, session_balance) {
            if disallow_overdraft {
              REVERT(8)
            }

            exchange_balance_used := sub(quantity, session_balance)
            session_balance_updated := 0

            if gt(exchange_balance_used, exchange_balance_remaining) {
              REVERT(9)
            }

            exchange_balance_remaining := sub(exchange_balance_remaining, exchange_balance_used)
          }

          let quantity_scaled := mul(quantity, unit_scale)

          let user_balance_ptr := USER_BALANCE_PTR_(user_ptr, asset_id)
          let user_balance := sload(user_balance_ptr)

          let updated_user_balance := add(user_balance, quantity_scaled)
          /* prevent overflow */
          if gt(user_balance, updated_user_balance) {
            REVERT(10)
          }

          let unsettled_withdraw_total_updated := add(
            attr(SessionBalance, 0, session_balance_0, unsettled_withdraw_total),
            exchange_balance_used
          )

          /* prevent overflow */
          if gt(unsettled_withdraw_total_updated, U64_MAX) {
            REVERT(11)
          }

          sstore(session_balance_ptr, or(
            and(mask_out(SessionBalance, 0, unsettled_withdraw_total, asset_balance), session_balance_0),
            build(SessionBalance, 0,
                  /* total_deposit */ 0,
                  /* unsettled_withdraw_total */ unsettled_withdraw_total_updated,
                  /* asset_balance */ session_balance_updated)
          ))

          sstore(user_balance_ptr, updated_user_balance)
        }

        sstore(exchange_balance_ptr, exchange_balance_remaining)
      }
    }
  }

  struct SetLimitsHeader {
    uint32 exchange_id;
  }

  struct Signature {
    uint256 sig_r;
    uint256 sig_s;
    uint8 sig_v;
  }

  struct UpdateLimit {
    uint32 dcn_id;
    uint64 user_id;
    uint32 exchange_id;
    uint32 quote_asset_id;
    uint32 base_asset_id;
    uint64 fee_limit;

    int64 min_quote_qty;
    int64 min_base_qty;
    uint64 long_max_price;
    uint64 short_min_price;

    uint64 limit_version;
    uint96 quote_shift;
    uint96 base_shift;
  }

  #define UPDATE_LIMIT_BYTES const_add(sizeof(UpdateLimit), sizeof(Signature))
  #define SIG_HASH_HEADER 0x1901000000000000000000000000000000000000000000000000000000000000
  #define DCN_HEADER_HASH 0x6c1a0baa584339032b4ed0d2fdb53c23d290c0b8a7da5a9e05ce919faa986a59
  #define UPDATE_LIMIT_TYPE_HASH 0xbe6b685e53075dd48bdabc4949b848400d5a7e53705df48e04ace664c3946ad2

  struct SetLimitMemory {
    uint256 user_id;
    uint256 exchange_id;
    uint256 quote_asset_id;
    uint256 base_asset_id;
    uint256 limit_version;
    uint256 quote_shift;
    uint256 base_shift;
  }

  /**
   * Exchange Set Limits
   *
   * caller = exchange owner
   *
   * Update trading limits for an exchange's session. Requires
   *  - proper signature
   *  - exchange_id is for exchange
   *  - limit_version is an increase (prevents replays)
   *
   * @param data, a binary payload
   *    - SetLimitsHeader
   *    - 1st Signature + UpdateLimit
   *    - 2nd Signature + UpdateLimit
   *    - ...
   */
  function exchange_set_limits(bytes memory data) public {
    uint256[14] memory to_hash_mem;

    uint256 cursor;
    uint256 cursor_end;
    uint256 exchange_id;

    uint256[sizeof(SetLimitMemory)] memory set_limit_memory_space;

    #define MEM_PTR(KEY) \
      add(set_limit_memory_space, byte_offset(SetLimitMemory, KEY))

    #define SAVE_MEM(KEY, VALUE) \
      mstore(MEM_PTR(KEY), VALUE)

    #define LOAD_MEM(KEY) \
      mload(MEM_PTR(KEY))

    /*
     * Ensure caller is exchange and setup cursors.
     */
    assembly {
      SECURITY_FEATURE_CHECK(FEATURE_EXCHANGE_SET_LIMITS, /* REVERT(0) */ 0)

      let data_size := mload(data)
      cursor := add(data, WORD_1)
      cursor_end := add(cursor, data_size)

      let set_limits_header_0 := CURSOR_LOAD(SetLimitsHeader, /* REVERT(1) */ 1)
      exchange_id := attr(SetLimitsHeader, 0, set_limits_header_0, exchange_id)

      let exchange_0 := sload(EXCHANGE_PTR_(exchange_id))
      let exchange_owner := attr(Exchange, 0, exchange_0, owner)

      /* ensure caller is the exchange owner */
      if iszero(eq(caller, exchange_owner)) {
        REVERT(2)
      }

      /* ensure exchange is not locked */
      if attr(Exchange, 0, exchange_0, locked) {
        REVERT(3)
      }
    }

    /*
     * Iterate through each limit to validate and apply
     */
    while (true) {
      uint256 update_limit_0;
      uint256 update_limit_1;
      uint256 update_limit_2;

      bytes32 limit_hash;

      /* hash limit, and extract variables */
      assembly {
        /* Reached the end */
        if eq(cursor, cursor_end) {
          return(0, 0)
        }

        update_limit_0 := mload(cursor)
        update_limit_1 := mload(add(cursor, WORD_1))
        update_limit_2 := mload(add(cursor, WORD_2))

        cursor := add(cursor, sizeof(UpdateLimit))
        if gt(cursor, cursor_end) {
          REVERT(4)
        }

        /* macro to make life easier */
        #define ATTR_GET(INDEX, ATTR_NAME) \
          attr(UpdateLimit, INDEX, update_limit_##INDEX, ATTR_NAME)

        #define BUF_PUT(WORD, INDEX, ATTR_NAME) \
          temp_var := ATTR_GET(INDEX, ATTR_NAME) \
          mstore(add(to_hash_mem, WORD), temp_var)

        #define BUF_PUT_I64(WORD, INDEX, ATTR_NAME) \
          temp_var := ATTR_GET(INDEX, ATTR_NAME) \
          CAST_64_NEG(temp_var) \
          mstore(add(to_hash_mem, WORD), temp_var)

        #define BUF_PUT_I96(WORD, INDEX, ATTR_NAME) \
          temp_var := ATTR_GET(INDEX, ATTR_NAME) \
          CAST_96_NEG(temp_var) \
          mstore(add(to_hash_mem, WORD), temp_var)
        
        #define SAVE(VAR_NAME) \
          SAVE_MEM(VAR_NAME, temp_var)

        #define LOAD(VAR_NAME) \
          LOAD_MEM(VAR_NAME)
          
        /* store data to hash */
        {
          mstore(to_hash_mem, UPDATE_LIMIT_TYPE_HASH)

          let temp_var := 0

              BUF_PUT(WORD_1,  0, dcn_id)
              BUF_PUT(WORD_2,  0, user_id) SAVE(user_id)
              BUF_PUT(WORD_3,  0, exchange_id)
              BUF_PUT(WORD_4,  0, quote_asset_id) SAVE(quote_asset_id)
              BUF_PUT(WORD_5,  0, base_asset_id) SAVE(base_asset_id)
              BUF_PUT(WORD_6,  0, fee_limit)

          BUF_PUT_I64(WORD_7,  1, min_quote_qty)
          BUF_PUT_I64(WORD_8,  1, min_base_qty)
              BUF_PUT(WORD_9,  1, long_max_price)
              BUF_PUT(WORD_10, 1, short_min_price)

              BUF_PUT(WORD_11, 2, limit_version) SAVE(limit_version)
          BUF_PUT_I96(WORD_12, 2, quote_shift) SAVE(quote_shift)
          BUF_PUT_I96(WORD_13, 2, base_shift) SAVE(base_shift)
        }

        limit_hash := keccak256(to_hash_mem, WORD_14)

        mstore(to_hash_mem, SIG_HASH_HEADER)
        mstore(add(to_hash_mem, 2), DCN_HEADER_HASH)
        mstore(add(to_hash_mem, const_add(WORD_1, 2)), limit_hash)

        limit_hash := keccak256(to_hash_mem, const_add(WORD_2, 2))
      }

      /* verify signature */
      {
        bytes32 sig_r;
        bytes32 sig_s;
        uint8 sig_v;

        /* Load signature data */
        assembly {
          sig_r := attr(Signature, 0, mload(cursor), sig_r)
          sig_s := attr(Signature, 1, mload(add(cursor, WORD_1)), sig_s)
          sig_v := attr(Signature, 2, mload(add(cursor, WORD_2)), sig_v)

          cursor := add(cursor, sizeof(Signature))
          if gt(cursor, cursor_end) {
            REVERT(5)
          }
        }

        uint256 recovered_address = uint256(ecrecover(
          /* hash */ limit_hash,
          /* v */ sig_v,
          /* r */ sig_r,
          /* s */ sig_s
        ));

        assembly {
          let user_ptr := USER_PTR_(LOAD(user_id))
          let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
          let trade_address := sload(pointer_attr(ExchangeSession, session_ptr, trade_address))

          if iszero(eq(recovered_address, trade_address)) {
            REVERT(6)
          }
        }
      }

      /* validate and apply new limit */
      assembly {
        /* limit's exchange id should match */
        {
          if iszero(eq(LOAD(exchange_id), exchange_id)) {
            REVERT(7)
          }
        }

        let user_ptr := USER_PTR_(LOAD(user_id))
        let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
        let market_state_ptr := MARKET_STATE_PTR_(
          session_ptr,
          LOAD(quote_asset_id),
          LOAD(base_asset_id)
        )

        let market_state_0 := sload(market_state_ptr)
        let market_state_1 := sload(add(market_state_ptr, 1))
        let market_state_2 := sload(add(market_state_ptr, 2))

        /* verify limit version is greater */
        {
          let current_limit_version := attr(MarketState, 2, market_state_2, limit_version)

          if iszero(gt(LOAD(limit_version), current_limit_version)) {
            REVERT(8)
          }
        }

        let quote_qty := attr(MarketState, 0, market_state_0, quote_qty)
        CAST_64_NEG(quote_qty)

        let base_qty := attr(MarketState, 0, market_state_0, base_qty)
        CAST_64_NEG(base_qty)

        #define APPLY_SHIFT(SIDE, REVERT_1) \
          { \
            let current_shift := attr(MarketState, 2, market_state_2, SIDE##_shift) \
            CAST_96_NEG(current_shift) \
            \
            let new_shift := LOAD(SIDE##_shift) \
            \
            SIDE##_qty := add(SIDE##_qty, sub(new_shift, current_shift)) \
            if INVALID_I64(SIDE##_qty) { \
              REVERT(REVERT_1) \
            } \
          }

        APPLY_SHIFT(quote, /* REVERT(9) */ 9)
        APPLY_SHIFT(base, /* REVERT(10) */ 10)

        let new_market_state_0 := or(
          build_with_mask(
            MarketState, 0,
            /* quote_qty */ quote_qty,
            /* base_qty */ base_qty,
            /* fee_used */ 0,
            /* fee_limit */ update_limit_0),
            and(mask_out(MarketState, 0, quote_qty, base_qty, fee_limit), market_state_0) /* extract fee_used */
        )

        sstore(market_state_ptr, new_market_state_0)
        sstore(add(market_state_ptr, 1), update_limit_1)
        sstore(add(market_state_ptr, 2), update_limit_2)
      }
    }
  }

  struct ExchangeId {
    uint32 exchange_id;
  }

  struct GroupHeader {
    uint32 quote_asset_id;
    uint32 base_asset_id;
    uint8 user_count;
  }

  struct Settlement {
    uint64 user_id;
    int64 quote_delta;
    int64 base_delta;
    uint64 fees;
  }

  /**
   * Exchange Apply Settlement Groups
   *
   * caller = exchange owner
   *
   * Apply quote & base delta to session balances. Deltas must net to zero
   * to ensure no funds are created or destroyed. Session's balance should
   * only apply if the net result fits within the session's trading limit.
   *
   * @param data, a binary payload
   *    - ExchangeId
   *    - 1st GroupHeader
   *      - 1st Settlement
   *      - 2nd Settlement
   *      - ...
   *      - (GroupHeader.user_count)th Settlement
   *    - 2nd GroupHeader
   *      - 1st Settlement
   *      - 2nd Settlement
   *      - ...
   *      - (GroupHeader.user_count)th Settlement
   *    - ...
   */
  function exchange_apply_settlement_groups(bytes memory data) public {
    uint256[6] memory variables;
    
    assembly {
      /* Check security lock */
      SECURITY_FEATURE_CHECK(FEATURE_APPLY_SETTLEMENT_GROUPS, /* REVERT(0) */ 0)

      let data_len := mload(data)
      let cursor := add(data, WORD_1)
      let cursor_end := add(cursor, data_len)

      let exchange_id_0 := CURSOR_LOAD(ExchangeId, /* REVERT(1) */ 1)
      let exchange_id := attr(ExchangeId, 0, exchange_id_0, exchange_id)
      VALID_EXCHANGE_ID(exchange_id, 2)

      {
        let exchange_ptr := EXCHANGE_PTR_(exchange_id)
        let exchange_0 := sload(exchange_ptr)

        /* caller must be exchange owner */
        if iszero(eq(caller, attr(Exchange, 0, exchange_0, owner))) {
          REVERT(2)
        }

        /* exchange must not be locked */
        if attr(Exchange, 0, exchange_0, locked) {
          REVERT(3)
        }
      }

      /* keep looping while there is space for a GroupHeader */
      for {} lt(cursor, cursor_end) {} {
        let header_0 := CURSOR_LOAD(GroupHeader, /* REVERT(4) */ 4)

        let quote_asset_id := attr(GroupHeader, 0, header_0, quote_asset_id)
        let base_asset_id := attr(GroupHeader, 0, header_0, base_asset_id)

        if eq(quote_asset_id, base_asset_id) {
          REVERT(16)
        }

        let group_end := add(cursor, mul(
          attr(GroupHeader, 0, header_0 /* GroupHeader */, user_count),
          sizeof(Settlement)
        ))

        /* validate quote_asset_id and base_asset_id */
        {
          let asset_count := sload(asset_count_slot)
          if iszero(and(lt(quote_asset_id, asset_count), lt(base_asset_id, asset_count))) {
            REVERT(5)
          }
        }

        /* ensure there is enough space for the settlement group */
        if gt(group_end, cursor_end) {
          REVERT(6)
        }

        let quote_net := 0
        let base_net := 0

        let exchange_ptr := EXCHANGE_PTR_(exchange_id)
        let exchange_balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, quote_asset_id)
        let exchange_balance := sload(exchange_balance_ptr)

        /* loop through each settlement */
        for {} lt(cursor, group_end) { cursor := add(cursor, sizeof(Settlement)) } {
          let settlement_0 := mload(cursor)
          let user_ptr := USER_PTR_(attr(Settlement, 0, settlement_0, user_id))

          /* Stage user's quote/base/fee update and test against limit */
          let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
          let market_state_ptr := MARKET_STATE_PTR_(
            session_ptr,
            quote_asset_id, base_asset_id
          )

          let quote_delta := attr(Settlement, 0, settlement_0, quote_delta)
          CAST_64_NEG(quote_delta)

          let base_delta := attr(Settlement, 0, settlement_0, base_delta)
          CAST_64_NEG(base_delta)

          quote_net := add(quote_net, quote_delta)
          base_net := add(base_net, base_delta)

          let fees := attr(Settlement, 0, settlement_0, fees)
          exchange_balance := add(exchange_balance, fees)

          let market_state_0 := sload(market_state_ptr)

          /* Validate Limit */
          {
            let quote_qty := attr(MarketState, 0, market_state_0, quote_qty)
            CAST_64_NEG(quote_qty)

            let base_qty := attr(MarketState, 0, market_state_0, base_qty)
            CAST_64_NEG(base_qty)

            quote_qty := add(quote_qty, quote_delta)
            base_qty := add(base_qty, base_delta)

            if or(INVALID_I64(quote_qty), INVALID_I64(base_qty)) {
              REVERT(7)
            }

            let fee_used := add(attr(MarketState, 0, market_state_0, fee_used), fees)
            let fee_limit := attr(MarketState, 0, market_state_0, fee_limit)

            if gt(fee_used, fee_limit) {
              REVERT(8)
            }

            market_state_0 := build(
              MarketState, 0,
              /* quote_qty */ quote_qty,
              /* base_qty */ and(base_qty, U64_MASK),
              /* fee_used */ fee_used,
              /* fee_limit */ fee_limit
            )

            let market_state_1 := sload(add(market_state_ptr, 1))

            /* Check against min_qty */
            {
              let min_quote_qty := attr(MarketState, 1, market_state_1, min_quote_qty)
              CAST_64_NEG(min_quote_qty)

              let min_base_qty := attr(MarketState, 1, market_state_1, min_base_qty)
              CAST_64_NEG(min_base_qty)

              if or(slt(quote_qty, min_quote_qty), slt(base_qty, min_base_qty)) {
                REVERT(9)
              }
            }

            /* Check against limit */
            {
              /* Check if price fits limit */
              let negatives := add(slt(quote_qty, 1), mul(slt(base_qty, 1), 2))

              switch negatives
              /* Both negative */
              case 3 {
                /* if one value is non zero, it must be negative */
                if or(quote_qty, base_qty) {
                  REVERT(10)
                }
              }
              /* long: quote_qty negative or zero */
              case 1 {
                let current_price := div(mul(sub(0, quote_qty), PRICE_UNITS), base_qty)
                let long_max_price := attr(MarketState, 1, market_state_1, long_max_price)

                if gt(current_price, long_max_price) {
                  REVERT(11)
                }
              }
              /* short: base_qty negative */
              case 2 {
                if base_qty {
                  let current_price := div(mul(quote_qty, PRICE_UNITS), sub(0, base_qty))
                  let short_min_price := attr(MarketState, 1, market_state_1, short_min_price)

                  if lt(current_price, short_min_price) {
                    REVERT(12)
                  }
                }
              }
            }
          }

          let quote_session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, quote_asset_id)
          let base_session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, base_asset_id)

          let quote_session_balance_0 := sload(quote_session_balance_ptr)
          let base_session_balance_0 := sload(base_session_balance_ptr)

          let quote_balance := attr(SessionBalance, 0, quote_session_balance_0, asset_balance)
          quote_balance := add(quote_balance, quote_delta)
          quote_balance := sub(quote_balance, fees)

          if U64_OVERFLOW(quote_balance) {
            REVERT(13)
          }

          let base_balance := attr(SessionBalance, 0, base_session_balance_0, asset_balance)
          base_balance := add(base_balance, base_delta)
          if U64_OVERFLOW(base_balance) {
            REVERT(14)
          }

          sstore(market_state_ptr, market_state_0)
          sstore(quote_session_balance_ptr, or(
            and(mask_out(SessionBalance, 0, asset_balance), quote_session_balance_0),
            build(
              SessionBalance, 0,
              /* total_deposit */ 0,
              /* unsettled_withdraw_total */ 0,
              /* asset_balance */ quote_balance)
          ))
          sstore(base_session_balance_ptr, or(
            and(mask_out(SessionBalance, 0, asset_balance), base_session_balance_0),
            build(
              SessionBalance, 0,
              /* total_deposit */ 0,
              /* unsettled_withdraw_total */ 0,
              /* asset_balance */ base_balance)
          ))
        }

        if or(quote_net, base_net) {
          REVERT(15)
        }

        sstore(exchange_balance_ptr, exchange_balance)
      }
    }
  }
}
