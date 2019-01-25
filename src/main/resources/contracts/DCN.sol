pragma solidity ^0.5.0;

#define TRANSPILE

#define WORD_1 32
#define WORD_2 64
#define WORD_3 96
#define WORD_4 128
#define WORD_5 160
#define WORD_6 192
#define WORD_7 224
#define WORD_8 256
#define WORD_9 288
#define WORD_10 320

#define U64_MASK 0xFFFFFFFFFFFFFFFF

#define CHAIN_ID 1111
#define VERSION  1

#define PRICE_UNITS 100000000

#define INVALID_I64(variable) \
  or(slt(variable, 0xffffffffffffffffffffffffffffffffffffffffffffffff8000000000000000), sgt(variable, 0x7fffffffffffffff))

contract DCN {
  event SessionUpdated(address user, uint64 exchange_id);
  event PositionUpdated(address user, uint64 exchange_id, uint32 asset_id); 

  /* Contract Constants */

  uint256 creator;
  uint256 exchange_count;
  uint256 asset_count;

  /* Memory Layout */

  #define EXCHANGE_COUNT (2**32)
  #define ASSET_COUNT (2**32)
  #define USER_COUNT (2**160)

  struct Exchange {
    uint64 name;
    uint32 quote_asset_id;
    address owner;
    uint256 fee_balance;
  }

  struct Asset {
    uint32 symbol;
    uint64 unit_scale;
    address contract_address;
  }

  struct User {
    uint256[ASSET_COUNT] balances;
  }

  struct AssetState {
    int64 quote_qty;
    int64 base_qty;
    uint64 total_deposit;
    uint64 asset_balance;

    int64 min_quote;
    int64 min_base;
    int64 quote_shift;
    int64 base_shift;

    uint64 padding;
    uint64 limit_version;
    uint64 long_max_price;
    uint64 short_min_price;
  }

  struct QuoteAssetState {
    uint64 fee_limit;
    uint64 fee_used;
    uint64 total_deposit;
    uint64 asset_balance;

    uint64 version;
    uint192 unlock_at;

    uint256 padding;
  }

  struct UserExchangeSession {
    AssetState[ASSET_COUNT] states;
  }

  struct UserSessions {
    UserExchangeSession[EXCHANGE_COUNT] exchange_sessions;
  }

  Exchange[EXCHANGE_COUNT] exchanges;
  Asset[ASSET_COUNT] assets;
  User[USER_COUNT] users;
  UserSessions[USER_COUNT] sessions;

  /* Change constants for assembly */
  #define EXCHANGE_COUNT exp(2, 32)
  #define ASSET_COUNT exp(2, 32)
  #define USER_COUNT exp(2, 160)

  #define SESSION_PTR(user_addr, exchange_id) pointer(UserExchangeSession, pointer(UserSessions, sessions_slot, user_addr), exchange_id)
  #define REVERT(code) mstore(revert_reason, code) revert(add(revert_reason, 31), 1)

  constructor() public {
    assembly {
      sstore(creator_slot, caller)
    }
  }

  /* View functions */

  function get_creator() public view returns (address dcn_creator) {
    return address(creator);
  }

  function get_asset(uint32 asset_id) public view
  returns (string memory symbol, uint64 unit_scale, address contract_address) {
    uint256[5] memory return_value_mem;

    assembly {
      let data := sload(pointer(Asset, assets_slot, asset_id))

      mstore(return_value_mem, 96)
      mstore(add(return_value_mem, 96), 4)
      mstore(add(return_value_mem, 128), data)

      mstore(add(return_value_mem, WORD_1), attr(Asset, 0, data, unit_scale))
      mstore(add(return_value_mem, WORD_2), attr(Asset, 0, data, contract_address))

      return(return_value_mem, 132)
    }
  }

  function get_exchange(uint32 exchange_id) public view returns (string memory name, uint64 quote_asset_id, address addr, uint64 fee_balance) {
    /* [ name_offset, quote_asset_id, addr, fee_balance, name_len, name_data(8) ] */
    uint256[6] memory return_value_mem;

    assembly {
      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
      let exchange_data := sload(exchange_ptr)

      /* Store name */
      mstore(return_value_mem, WORD_4)
      mstore(add(return_value_mem, WORD_4), 8)
      mstore(add(return_value_mem, WORD_5), exchange_data)

      /* Store quote_asset_id */
      mstore(add(return_value_mem, WORD_1), attr(Exchange, 0, exchange_data, quote_asset_id))

      /* Store addr */
      mstore(add(return_value_mem, WORD_2), attr(Exchange, 0, exchange_data, owner))

      /* Store fee_balance */
      exchange_data := sload(add(exchange_ptr, 1))
      mstore(add(return_value_mem, WORD_3), attr(Exchange, 1, exchange_data, fee_balance))

      return(return_value_mem, 168)
    }
  }

  function get_exchange_count() public view returns (uint32 count) {
    uint256[1] memory return_value_mem;

    assembly {
      let data := sload(exchange_count_slot)
      mstore(return_value_mem, data)
      return(return_value_mem, WORD_1)
    }
  }

  function get_asset_count() public view returns (uint32 count) {
    uint256[1] memory return_value_mem;

    assembly {
      let asset_count := sload(asset_count_slot)
      mstore(return_value_mem, asset_count)
      return(return_value_mem, WORD_1)
    }
  }

  function get_balance(address user, uint32 asset_id) public view returns (uint256 return_balance) {
    uint256[1] memory return_value_mem;

    assembly {
      let user_ptr := pointer(User, users_slot, user)
      let balance_ptr := pointer(u256, user_ptr, asset_id)

      mstore(return_value_mem, sload(balance_ptr))
      return(return_value_mem, WORD_1)
    }
  }

  function get_session(address user, uint32 exchange_id) public view
  returns (uint64 version, uint64 unlock_at, uint64 fee_limit, uint64 fee_used) {
    uint256[4] memory return_value_mem;

    assembly {
      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
      let quote_asset_id := attr(Exchange, 0, sload(exchange_ptr), quote_asset_id)

      let session_ptr := SESSION_PTR(user, exchange_id)
      let quote_state_ptr := pointer(AssetState, session_ptr, quote_asset_id)

      let state_data_0 := sload(quote_state_ptr)
      let state_data_1 := sload(add(quote_state_ptr, 1))

      mstore(return_value_mem, attr(QuoteAssetState, 1, state_data_1, version))
      mstore(add(return_value_mem, WORD_1), attr(QuoteAssetState, 1, state_data_1, unlock_at))
      mstore(add(return_value_mem, WORD_2), attr(QuoteAssetState, 0, state_data_0, fee_limit))
      mstore(add(return_value_mem, WORD_3), attr(QuoteAssetState, 0, state_data_0, fee_used))

      return(return_value_mem, WORD_4)
    }
  }

  function get_session_balance(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (uint64 total_deposit, uint64 asset_balance) {
    uint256[2] memory return_value_mem;

    assembly {
      let session_ptr := SESSION_PTR(user, exchange_id)
      let state_ptr := pointer(AssetState, session_ptr, asset_id)

      let state_data := sload(state_ptr)

      mstore(return_value_mem, attr(AssetState, 0, state_data, total_deposit))
      mstore(add(return_value_mem, WORD_1), attr(AssetState, 0, state_data, asset_balance))

      return(return_value_mem, WORD_2)
    }
  }

  function get_session_state(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (int64 quote_qty, int64 base_qty, int64 quote_shift, int64 base_shift,
           uint64 version, int64 min_quote, int64 min_base, uint64 long_max_price, uint64 short_min_price) {
    uint256[8] memory return_value_mem;

    assembly {
      let session_ptr := SESSION_PTR(user, exchange_id)
      let state_ptr := pointer(AssetState, session_ptr, asset_id)

      let state_data_0 := sload(state_ptr)
      let state_data_1 := sload(add(state_ptr, 1))
      let state_data_2 := sload(add(state_ptr, 2))

      mstore(return_value_mem, attr(AssetState, 0, state_data_0, quote_qty))
      mstore(add(return_value_mem, WORD_1), attr(AssetState, 0, state_data_0, base_qty))
      mstore(add(return_value_mem, WORD_2), attr(AssetState, 1, state_data_1, quote_shift))
      mstore(add(return_value_mem, WORD_3), attr(AssetState, 1, state_data_1, base_shift))

      mstore(add(return_value_mem, WORD_4), attr(AssetState, 2, state_data_2, limit_version))
      mstore(add(return_value_mem, WORD_5), attr(AssetState, 1, state_data_1, min_quote))
      mstore(add(return_value_mem, WORD_6), attr(AssetState, 1, state_data_1, min_base))

      mstore(add(return_value_mem, WORD_7), attr(AssetState, 2, state_data_2, long_max_price))
      mstore(add(return_value_mem, WORD_8), attr(AssetState, 2, state_data_2, short_min_price))

      return(return_value_mem, WORD_9)
    }
  }

  /*
   * Tests:
   *
   * CreatorTests
   * - non creator should not be able to change creator to self
   * - non creator should not be able to change creator to other
   * - creator should be able to change to other
   * - new creator should be able to change to other
   */

  function set_creator(address new_creator) public {
    assembly {
      let current_creator := sload(creator_slot)
      if iszero(eq(current_creator, caller)) {
        revert(0, 0)
      }

      sstore(creator_slot, new_creator)
    }
  }

  /*
   * Tests:
   *
   * AssetTests
   * - add assets
   * -- non creator should not be able to add asset
   * -- creator should be able to add asset
   * -- should not be able to create asset with 0 unit scale
   */
  function add_asset(string memory symbol, uint64 unit_scale, address contract_address) public {
    uint256[1] memory revert_reason;

    assembly {
      let creator_address := sload(creator_slot)

      /* only creator can add asset */
      if iszero(eq(creator_address, caller)) {
        REVERT(1)
      }

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

      let asset_symbol := mload(add(symbol, 32 /* offset as first word is size */))

      /* Note, symbol is already shifted not setting it in build */
      let asset_data := or(asset_symbol, build(Asset, /* word */ 0, /* symbol */ 0, unit_scale, contract_address))

      let asset_ptr := pointer(Asset, assets_slot, asset_id)

      sstore(asset_ptr, asset_data)
      sstore(asset_count_slot, add(asset_id, 1))
    }
  }


  /*
   * Tests:
   *
   * ExchangeTests
   * - add exchange
   * -- non creator should fail to add exchange
   * -- creator should be able to create exchange
   * -- should not be able to create exchange with 10 char name
   * -- should not be able to create exchange with 15 char name
   * -- should not be able to add exchange with invalid quote_asset
   * -- should be able to add exchange with non eth asset
   */
  function add_exchange(string memory name, uint32 quote_asset_id, address addr) public {
    uint256[1] memory revert_reason;

    assembly {
      let creator_address := sload(creator_slot)

      /* Only the creator can add an exchange */
      if iszero(eq(creator_address, caller)) {
        REVERT(1)
      }

      /* Name must be 8 bytes long */
      let name_len := mload(name)
      if iszero(eq(name_len, 8)) {
        REVERT(2)
      }

      /* Quote asset must exist */
      let asset_count := sload(asset_count_slot)
      if iszero(lt(quote_asset_id, asset_count)) {
        REVERT(3)
      }

      /* Do not overflow exchanges */
      let exchange_count := sload(exchange_count_slot)
      if iszero(lt(exchange_count, EXCHANGE_COUNT)) {
        REVERT(4)
      }

      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_count)

      /*
       * name_data will start with 12 bytes of name data
       * and addr is 20 bytes. Total 32 bytes (one word)
       */
      let name_data := mload(add(name, 32))
      let exchange_data := or(name_data, build(Exchange, /* word */ 0, /* symbol */ 0, quote_asset_id, addr))
      sstore(exchange_ptr, exchange_data)
      sstore(exchange_count_slot, add(exchange_count, 1))
    }
  }

  /*
   * Tests
   *
   * BalanceTests
   * - manage assets
   * -- should fail to deposit asset without allowance
   * -- should be able to deposit asset
   * -- should fail to deposit more than allowance
   * -- should be able to deposit more
   *
   * TODO:
   * - should fail if asset_id is invalid
   */
  function deposit_asset(uint32 asset_id, uint256 amount) public {
    uint256[1] memory revert_reason;
    uint256[4] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      if iszero(amount) {
        stop()
      }

      /* Validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if iszero(lt(asset_id, asset_count)) {
          REVERT(1)
        }
      }

      mstore(transfer_in_mem, /* transferFrom(address,address,uint256) */ fn_hash("transferFrom(address,address,uint256)"))
      mstore(add(transfer_in_mem, 4), caller)
      mstore(add(transfer_in_mem, 36), address)
      mstore(add(transfer_in_mem, 68), amount)

      let asset_data := sload(pointer(Asset, assets_slot, asset_id))
      let asset_address := attr(Asset, 0, asset_data, contract_address)

      /* call external contract */
      {
        let success := call(
          gas,
          asset_address,
          /* don't send any ether */ 0,
          transfer_in_mem,
          /* transfer_in_mem size (bytes) */ 100,
          transfer_out_mem,
          /* transfer_out_mem size (bytes) */ 32
        )

        if iszero(success) {
          REVERT(2)
        }

        let result := mload(transfer_out_mem)
        if iszero(result) {
          REVERT(3)
        }
      }

      let user_ptr := pointer(User, users_slot, caller)
      let asset_ptr := pointer(u256, user_ptr, asset_id)
      let current_balance := sload(asset_ptr)

      sstore(asset_ptr, add(current_balance, amount))
    }
  }

  /*
   * Tests:
   *
   * BalanceTests
   * - manage assets
   * -- should be able to partial withdraw
   * -- should not be able to overdraft
   * -- should be able to withdraw to zero
   *
   * TODO:
   * - should exit with zero affect if amount is zero
   */
  function withdraw_asset(uint32 asset_id, address destination, uint256 amount) public {
    uint256[1] memory revert_reason;
    uint256[3] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      if iszero(amount) {
        stop()
      }

      /*
       * Note, don't need to validate asset_id as will have 0 funds if doesn't exist
       */

      let user_ptr := pointer(User, users_slot, caller)
      let asset_ptr := pointer(u256, user_ptr, asset_id)

      let current_balance := sload(asset_ptr)
      if lt(current_balance, amount) {
        REVERT(1)
      }

      mstore(transfer_in_mem, fn_hash("transfer(address,uint256)"))
      mstore(add(transfer_in_mem, 4), destination)
      mstore(add(transfer_in_mem, 36), amount)

      let asset_data := sload(pointer(Asset, assets_slot, asset_id))
      let asset_address := attr(Asset, 0, asset_data, contract_address)

      let success := call(
        gas,
        asset_address,
        /* don't send any ether */ 0,
        transfer_in_mem,
        /* transfer_in_mem size (bytes) */ 68,
        transfer_out_mem,
        /* transfer_out_mem size (bytes) */ 32
      )

      if iszero(success) {
        REVERT(2)
      }

      let result := mload(transfer_out_mem)
      if iszero(result) {
        REVERT(3)
      }

      sstore(asset_ptr, sub(current_balance, amount))
    }
  }

  /*
   * Tests:
   *
   * UpdateSessionTests
   * - should fail to update with expire time = now
   * - should fail to update with expire time = now + 31 days
   * - should fail to update with non existent exchange_id
   * - should be able to update, send log, update version/state
   * - should be able to deposit with update_session, check remainder
   */

  #define MIN_EXPIRE_TIME 43200
  #define MAX_EXPIRE_TIME 2592000

  function update_session(uint32 exchange_id, uint64 unlock_at, uint64 fee_limit) public {
    uint256[1] memory revert_reason;
    uint256[3] memory log_data_mem;

    assembly {
      /* ensure: unlock_at >= timestamp + 12 hours && unlock_at <= timestamp + 30 days */
      if or(lt(unlock_at, add(timestamp, MIN_EXPIRE_TIME)), gt(unlock_at, add(timestamp, MAX_EXPIRE_TIME))) {
        REVERT(1)
      }

      /* ensure: exchange_id < exchange_count */
      {
        let exchange_count := sload(exchange_count_slot)
        if iszero(lt(exchange_id, exchange_count)) {
          REVERT(2)
        }
      }

      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
      let quote_asset_id := attr(Exchange, 0, sload(exchange_ptr), quote_asset_id)

      let session_ptr := SESSION_PTR(caller, exchange_id)
      let quote_state_ptr := pointer(AssetState, session_ptr, quote_asset_id)

      let quote_state := sload(quote_state_ptr)
      let current_fee_limit := attr(QuoteAssetState, 0, quote_state, fee_limit)

      /* fee limit cannot decrease */
      if lt(fee_limit, current_fee_limit) {
        REVERT(3)
      }

      /* only update if needed */
      if gt(fee_limit, current_fee_limit) {
        sstore(quote_state_ptr, or(
          and(quote_state, mask_out(QuoteAssetState, 0, fee_limit)),
          build(QuoteAssetState, 0, fee_limit)
        ))
      }

      let version := attr(QuoteAssetState, 1, sload(add(quote_state_ptr, 1)), version)

      sstore(add(quote_state_ptr, 1), build(
        QuoteAssetState, 1,
        /* version overflow will wrap which is desired */
        add(version, 1),
        /* timestamp should never overflow u192 */
        unlock_at
      ))

      log_event(SessionUpdated, log_data_mem, caller, exchange_id)
    }
  }

  /*
   * Tests
   * 
   * DepositAssetToSessionTests
   * - should error with invalid assset id
   */
  function deposit_asset_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[1] memory revert_reason;
    uint256[4] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;
    uint256[3] memory log_data_mem;

    assembly {
      /* validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if iszero(lt(asset_id, asset_count)) {
          REVERT(1)
        }
      }

      if iszero(quantity) {
        REVERT(2)
      }

      let asset_data := sload(pointer(Asset, assets_slot, asset_id))
      let amount := mul(quantity, attr(Asset, 0, asset_data, unit_scale))
      let asset_address := attr(Asset, 0, asset_data, contract_address)

      mstore(transfer_in_mem, fn_hash("transferFrom(address,address,uint256)"))
      mstore(add(transfer_in_mem, 4), caller)
      mstore(add(transfer_in_mem, 36), address)
      mstore(add(transfer_in_mem, 68), amount)

      /* call external contract */
      {
        let success := call(
          gas,
          asset_address,
          /* don't send any ether */ 0,
          transfer_in_mem,
          /* transfer_in_mem size (bytes) */ 100,
          transfer_out_mem,
          /* transfer_out_mem size (bytes) */ 32
        )

        /* verify call was successful */
        if iszero(success) {
          REVERT(3)
        }

        let result := mload(transfer_out_mem)
        if iszero(result) {
          REVERT(4)
        }
      }

      /* deposit funds into session */
      let session_ptr := SESSION_PTR(caller, exchange_id)

      let asset_state_ptr := pointer(AssetState, session_ptr, asset_id)

      let asset_state_data := sload(asset_state_ptr)
      let total_deposit := and(add(attr(AssetState, 0, asset_state_data, total_deposit), quantity), U64_MASK)
      let asset_balance := add(attr(AssetState, 0, asset_state_data, asset_balance), quantity)

      /* note, allow total_deposit to overflow */
      /* check for asset_balance overflow */
      if gt(asset_balance, U64_MASK) {
        REVERT(5)
      }

      sstore(
        asset_state_ptr,
        or(
          and(asset_state_data, mask_out(AssetState, 0, total_deposit, asset_balance)),
          build(AssetState, 0, 0, 0, total_deposit, asset_balance)
        )
      )

      log_event(PositionUpdated, log_data_mem, caller, exchange_id, asset_id)
    }
  }

  /*
   * Tests, TODO
   *
   * TransferToSessionTests
   * - should not be able to transfer from 0 funds account
   */
  function transfer_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[1] memory revert_reason;
    uint256[4] memory log_data_mem;

    assembly {
      /* Update asset_balance variable */
      {
        let user_ptr := pointer(User, users_slot, caller)
        let asset_ptr := pointer(u256, user_ptr, asset_id)
        let asset_balance := sload(asset_ptr)

        /* Convert quantity to amount using unit_scale */
        let asset_data := sload(pointer(Asset, assets_slot, asset_id))
        let unit_scale := attr(Asset, 0, asset_data, unit_scale)

        /* Note, mul cannot overflow as both numbers are 64 bit and result is 256 bits */
        let amount := mul(quantity, unit_scale)

        /* Ensure user has enough asset_balance for deposit */
        if gt(amount, asset_balance) {
          REVERT(1)
        }

        asset_balance := sub(asset_balance, amount)
        sstore(asset_ptr, asset_balance)
      }

      /* Update session asset_balance */
      let session_ptr := SESSION_PTR(caller, exchange_id)
      let asset_state_ptr := pointer(AssetState, session_ptr, asset_id)
      let asset_state_data := sload(asset_state_ptr)

      let total_deposit := add(attr(AssetState, 0, asset_state_data, total_deposit), quantity)
      let asset_balance := add(attr(AssetState, 0, asset_state_data, asset_balance), quantity)

      /* allow overflow in total_deposit */
      total_deposit := and(total_deposit, U64_MASK)

      /* ensure asset_balance doesn't overflow */
      if gt(asset_balance, U64_MASK) {
        REVERT(2)
      }

      /* Update balances */
      sstore(asset_state_ptr, or(
        and(asset_state_data, mask_out(AssetState, 0, total_deposit, asset_balance)),
        build(AssetState, 0, 0, 0, total_deposit, asset_balance)
      ))

      log_event(PositionUpdated, log_data_mem, caller, exchange_id, asset_id)
    }
  }

  function transfer_from_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[1] memory revert_reason;
    uint256[4] memory log_data_mem;

    assembly {
      let exchange_data := sload(pointer(Exchange, exchanges_slot, exchange_id))
      let quote_asset_id := attr(Exchange, 0, exchange_data, quote_asset_id)

      let session_ptr := SESSION_PTR(caller, exchange_id)

      {
        let quote_state_ptr := pointer(AssetState, session_ptr, quote_asset_id)
        let unlock_at := attr(QuoteAssetState, 1, sload(add(quote_state_ptr, 1)), unlock_at)

        /* revert if locked */
        if lt(timestamp, unlock_at) {
          REVERT(1)
        }
      }

      /* subtract from session */
      {
        let asset_state_ptr := pointer(AssetState, session_ptr, asset_id)
        let asset_state_data := sload(asset_state_ptr)

        /* trying to withdraw too much */
        let asset_balance := attr(AssetState, 0, asset_state_data, asset_balance)
        if gt(quantity, asset_balance) {
          REVERT(2)
        }

        asset_balance := sub(asset_balance, quantity)

        sstore(asset_state_ptr, or(
          and(asset_state_data, mask_out(AssetState, 0, asset_balance)),
          build(AssetState, 0, 0, 0, 0, asset_balance)
        ))
      }

      /* add to user balance */
      {
        let user_ptr := pointer(User, users_slot, caller)
        let asset_ptr := pointer(u256, user_ptr, asset_id)
        let asset_balance := sload(asset_ptr)

        /* Convert quantity to amount using unit_scale */
        let asset_data := sload(pointer(Asset, assets_slot, asset_id))
        let unit_scale := attr(Asset, 0, asset_data, unit_scale)

        /* Note mul cannot overflow as both numbers are 64 bit and result is 256 bits */
        let amount := mul(quantity, unit_scale)

        asset_balance := add(asset_balance, amount)

        /* protect again overflow */
        if lt(asset_balance, amount) {
          REVERT(3)
        }

        sstore(asset_ptr, asset_balance)
      }

      log_event(PositionUpdated, log_data_mem, caller, exchange_id, asset_id)
    }
  }

  struct Signature {
    uint256 sig_r;
    uint256 sig_s;
    uint8 sig_v;
  }

  struct Address {
    address user_address;
    uint96 padding;
  }

  struct UpdateLimit {
    uint32 exchange_id;
    uint32 asset_id;
    uint64 version;
    uint64 long_max_price;
    uint64 short_min_price;

    uint64 min_quote_qty;
    uint64 min_base_qty;
    uint64 quote_shift;
    uint64 base_shift;
  }

  #define UPDATE_LIMIT_BYTES const_add(sizeof(address), sizeof(UpdateLimit), sizeof(Signature))
  #define SIG_HASH_HEADER 0x1901000000000000000000000000000000000000000000000000000000000000
  #define DCN_HEADER_HASH 0xe3d3073cc59e3a3126c17585a7e516a048e61a9a1c82144af982d1c194b18710
  #define UPDATE_LIMIT_TYPE_HASH 0xe0bfc2789e007df269c9fec46d3ddd4acf88fdf0f76af154da933aab7fb2f2b9

  #define NEG_64_FLAG 0x8000000000000000
  #define I64_TO_NEG 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000

  #define CAST_64_NEG(variable) \
      if and(variable, NEG_64_FLAG) { \
        variable := or(variable, I64_TO_NEG) \
      }

  function set_limit(bytes memory data) public {
    uint256[1] memory revert_reason;
    uint256[10] memory hash_buffer_mem;

    uint256 user_addr;

    assembly {
      let data_size := mload(data)
      let cursor := add(data, WORD_1)

      /* ensure data size is correct */
      if iszero(eq(data_size, UPDATE_LIMIT_BYTES)) {
        REVERT(1)
      }

      /* load user_address */
      let update_data := mload(cursor)
      cursor := add(cursor, sizeof(address))
      user_addr := attr(Address, 0, update_data, user_address)

      /* start hash buffer */
      mstore(hash_buffer_mem, UPDATE_LIMIT_TYPE_HASH)

      /* load update_limit_1 */
      update_data := mload(cursor)
      cursor := add(cursor, WORD_1)

      /* note, scopes used to help compiler trim the stack */

      let asset_state_ptr := 0
      {
        let version := 0
        {
          let exchange_id := attr(UpdateLimit, 0, update_data, exchange_id)
          mstore(add(hash_buffer_mem, WORD_1), exchange_id)

          let asset_id := attr(UpdateLimit, 0, update_data, asset_id)
          mstore(add(hash_buffer_mem, WORD_2), asset_id)

          version := attr(UpdateLimit, 0, update_data, version)
          mstore(add(hash_buffer_mem, WORD_3), version)

          let session_ptr := SESSION_PTR(user_addr, exchange_id)
          asset_state_ptr := pointer(AssetState, session_ptr, asset_id)

          /* exchange address must be caller and asset_id cannot be quote */
          {
            let exchange_data := sload(pointer(Exchange, exchanges_slot, exchange_id))

            let exchange_address := attr(Exchange, 0, exchange_data, owner)
            if iszero(eq(caller, exchange_address)) {
              REVERT(2)
            }

            /* Prevents exchange from corrupting user's limits */
            let quote_asset_id := attr(Exchange, 0, exchange_data, quote_asset_id)
            if eq(quote_asset_id, asset_id) {
              REVERT(6)
            }
          }

          let current_version := attr(AssetState, 2, sload(add(asset_state_ptr, 2)), limit_version)

          /* version must be greater than */
          if iszero(gt(version, current_version)) {
            REVERT(3)
          }
        }

        {
          let long_max_price := attr(UpdateLimit, 0, update_data, long_max_price)
          mstore(add(hash_buffer_mem, WORD_4), long_max_price)

          let short_min_price := attr(UpdateLimit, 0, update_data, short_min_price)
          mstore(add(hash_buffer_mem, WORD_5), short_min_price)

          sstore(add(asset_state_ptr, 2), build(AssetState, 2,
            /* padding */ 0,
            version,
            long_max_price,
            short_min_price
          ))
        }
      }

      update_data := mload(cursor)
      cursor := add(cursor, WORD_1)

      /* Note, set asset_state_data before CAST_64_NEG so we don't need to mask */
      let state_data_1 := 0

      {
        let min_quote_qty := attr(UpdateLimit, 1, update_data, min_quote_qty)
        state_data_1 := build(AssetState, 1, min_quote_qty)
        CAST_64_NEG(min_quote_qty)
        mstore(add(hash_buffer_mem, WORD_6), min_quote_qty)
      }

      {
        let min_base_qty := attr(UpdateLimit, 1, update_data, min_base_qty)
        state_data_1 := or(state_data_1, build(AssetState, 1, 0, min_base_qty))
        CAST_64_NEG(min_base_qty)
        mstore(add(hash_buffer_mem, WORD_7), min_base_qty)
      }

      let quote_shift := attr(UpdateLimit, 1, update_data, quote_shift)
      state_data_1 := or(state_data_1, build(AssetState, 1, 0, 0, quote_shift))
      CAST_64_NEG(quote_shift)
      mstore(add(hash_buffer_mem, WORD_8), quote_shift)

      let base_shift := attr(UpdateLimit, 1, update_data, base_shift)
      state_data_1 := or(state_data_1, build(AssetState, 1, 0, 0, 0, base_shift))
      CAST_64_NEG(base_shift)
      mstore(add(hash_buffer_mem, WORD_9), base_shift)

      sstore(add(asset_state_ptr, 1), state_data_1)

      /* Normalize shift against existing */
      {
        let current_state_data := sload(add(asset_state_ptr, 1))

        {
          let current_quote_shift := attr(AssetState, 1, current_state_data, quote_shift)
          CAST_64_NEG(current_quote_shift)
          quote_shift := sub(quote_shift, current_quote_shift)
        }

        {
          let current_base_shift := attr(AssetState, 1, current_state_data, base_shift)
          CAST_64_NEG(current_base_shift)
          base_shift := sub(base_shift, current_base_shift)
        }
      }

      let state_data_0 := sload(asset_state_ptr)
      let quote_qty := add(quote_shift, attr(AssetState, 0, state_data_0, quote_qty))
      let base_qty := add(base_shift, attr(AssetState, 0, state_data_0, base_qty))

      if or(INVALID_I64(quote_qty), INVALID_I64(base_qty)) {
        REVERT(4)
      }

      sstore(asset_state_ptr, or(
        and(state_data_0, mask_out(AssetState, 0, quote_qty, base_qty)),
        build(AssetState, 1, and(quote_qty, U64_MASK), and(base_qty, U64_MASK))
      ))

      let hash := keccak256(hash_buffer_mem, WORD_10)

      {
        let final_ptr := hash_buffer_mem

        mstore(final_ptr, SIG_HASH_HEADER)
        final_ptr := add(final_ptr, 2)

        mstore(final_ptr, DCN_HEADER_HASH)
        final_ptr := add(final_ptr, WORD_1)

        mstore(final_ptr, hash)
      }

      hash := keccak256(hash_buffer_mem, 66)
      mstore(hash_buffer_mem, hash)

      update_data := mload(cursor)
      cursor := add(cursor, WORD_1)
      mstore(add(hash_buffer_mem, WORD_1), attr(Signature, 0, update_data, sig_r))

      update_data := mload(cursor)
      cursor := add(cursor, WORD_1)
      mstore(add(hash_buffer_mem, WORD_2), attr(Signature, 1, update_data, sig_s))

      update_data := mload(cursor)
      mstore(add(hash_buffer_mem, WORD_3), attr(Signature, 2, update_data, sig_v))
    }

    uint256 recover_address = uint256(ecrecover(
      bytes32(hash_buffer_mem[0]),
      uint8(hash_buffer_mem[3]),
      bytes32(hash_buffer_mem[1]),
      bytes32(hash_buffer_mem[2])
    ));

    assembly {
      if iszero(eq(recover_address, user_addr)) {
        REVERT(5)
      }
    }
  }

  struct GroupsHeader {
    uint32 exchange_id;
  }

  struct GroupHeader {
    uint32 base_asset_id;
    uint8 user_count;
  }

  struct UserAddress {
    address user_address;
  }

  struct Settlement {
    int64 quote_delta;
    int64 base_delta;
    uint64 fees;
  }

  function apply_settlement_groups(bytes memory data) public {
    uint256[5] memory variables;
    
    #define VARIABLES_END         msize
    #define VARIABLES_START       sub(VARIABLES_END, WORD_6)

    #define QUOTE_ASSET_ID_MEM    sub(VARIABLES_END, WORD_6)
    #define DATA_END_MEM          sub(VARIABLES_END, WORD_5)
    #define EXCHANGE_FEES_MEM     sub(VARIABLES_END, WORD_4)
    #define EXCHANGE_ID_MEM       sub(VARIABLES_END, WORD_3)
    #define GROUP_END_MEM         sub(VARIABLES_END, WORD_2)
    #define REVERT_REASON_MEM     sub(VARIABLES_END, WORD_1)

    #define SMART_REVERT(code)    mstore(REVERT_REASON_MEM, code) \
                                  revert(add(REVERT_REASON_MEM, 31), 1)
    
    #define DEBUG(code)           mstore(REVERT_REASON_MEM, code) \
                                  revert(REVERT_REASON_MEM, 32)

    assembly {
      let cursor := add(data, 32)
      let data_len := mload(data)

      mstore(DATA_END_MEM, add(cursor, data_len))

      /* ensure there is space for a header */
      if lt(data_len, sizeof(GroupsHeader)) {
        SMART_REVERT(0)
      }

      let tmp_data /* GroupsHeader */ := mload(cursor)
      cursor := add(cursor, sizeof(GroupsHeader))

      /* Validate exchange_id and load exchange data (quote_asset_id, fee_balance, exchange_id) */
      {
        let exchange_id := attr(GroupsHeader, 0, tmp_data /* GroupsHeader */, exchange_id)
        let exchange_count := sload(exchange_count_slot)

        /* exchange id must be valid */
        if iszero(lt(exchange_id, exchange_count)) {
          SMART_REVERT(1)
        }

        let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
        let exchange_data_0 := sload(exchange_ptr)

        /* caller must be exchange owner */
        if iszero(eq(caller, attr(Exchange, 0, exchange_data_0, owner))) {
          SMART_REVERT(2)
        }

        mstore(QUOTE_ASSET_ID_MEM, attr(Exchange, 0, exchange_data_0, quote_asset_id))
        mstore(EXCHANGE_FEES_MEM, attr(Exchange, 1, sload(add(exchange_ptr, 1)), fee_balance))
        mstore(EXCHANGE_ID_MEM, exchange_id)
      }

      /* keep looping while there is space for a GroupHeader */
      for {} iszero(lt(sub(mload(DATA_END_MEM), cursor), sizeof(GroupHeader))) {} {
        tmp_data /* GroupHeader */ := mload(cursor)
        cursor := add(cursor, sizeof(GroupHeader))

        {
          let user_count := attr(GroupHeader, 0, tmp_data /* GroupHeader */, user_count)
          let settlements_size := mul(user_count, const_add(sizeof(UserAddress), sizeof(Settlement)))
          let group_end := add(cursor, settlements_size)

          /* make sure there is enough size for the group */
          if gt(group_end, mload(DATA_END_MEM)) {
            SMART_REVERT(3)
          }

          mstore(GROUP_END_MEM, group_end)
        }

        let base_asset_id := attr(GroupHeader, 0, tmp_data /* GroupHeader */, base_asset_id)

        let quote_net := 0
        let base_net := 0

        /* loop through each settlement */
        for {} lt(cursor, mload(GROUP_END_MEM)) {} {
          tmp_data /* UserAddress */ := mload(cursor)
          cursor := add(cursor, sizeof(UserAddress))

          let session_ptr := SESSION_PTR(attr(UserAddress, 0, tmp_data, user_address), mload(EXCHANGE_ID_MEM))

          tmp_data /* SettlementData */ := mload(cursor)
          cursor := add(cursor, sizeof(Settlement))

          let quote_delta := attr(Settlement, 0, tmp_data, quote_delta)
          let base_delta := attr(Settlement, 0, tmp_data, base_delta)
          let fees := attr(Settlement, 0, tmp_data, fees)

          CAST_64_NEG(quote_delta)
          CAST_64_NEG(base_delta)

          quote_net := add(quote_net, quote_delta)
          base_net := add(base_net, base_delta)

          let quote_state_ptr := pointer(AssetState, session_ptr, mload(QUOTE_ASSET_ID_MEM))
          let base_state_ptr := pointer(AssetState, session_ptr, base_asset_id)

          /* ensure we're within expire time */
          {
            let state_data_1 := sload(add(quote_state_ptr, 1))
            let unlock_at := attr(QuoteAssetState, 1, state_data_1, unlock_at)

            if gt(timestamp, unlock_at) {
              SMART_REVERT(4)
            }
          }

          /* update quote balance */
          {
            let state_data_0 := sload(quote_state_ptr)

            let asset_balance := attr(QuoteAssetState, 0, state_data_0, asset_balance)
            asset_balance := add(asset_balance, quote_delta)
            asset_balance := sub(asset_balance, fees)

            /* make sure quote balance is positive and doesn't overflow */
            if gt(asset_balance, U64_MASK) {
              SMART_REVERT(5)
            }

            let fee_used := attr(QuoteAssetState, 0, state_data_0, fee_used)
            fee_used := add(fee_used, fees)

            let exchange_fees_mem := EXCHANGE_FEES_MEM
            mstore(exchange_fees_mem, add(mload(exchange_fees_mem), fees))

            let fee_limit := attr(QuoteAssetState, 0, state_data_0, fee_limit)

            /* ensure don't over spend fee */
            /* note, also provides overflow check */
            if gt(fee_used, fee_limit) {
              SMART_REVERT(6)
            }

            sstore(quote_state_ptr, or(
              and(state_data_0, mask_out(QuoteAssetState, 0, fee_used, asset_balance)),
              build(QuoteAssetState, 0, 0, fee_used, 0, asset_balance)
            ))
          }

          let quote_qty := 0
          let base_qty := 0
          {
            let state_ptr := pointer(AssetState, session_ptr, base_asset_id)
            let state_data_0 := sload(state_ptr)
            let asset_balance := attr(AssetState, 0, state_data_0, asset_balance)

            asset_balance := add(asset_balance, base_delta)
            if gt(asset_balance, U64_MASK) {
              SMART_REVERT(7)
            }

            quote_qty := attr(AssetState, 0, state_data_0, quote_qty)
            base_qty := attr(AssetState, 0, state_data_0, base_qty)

            CAST_64_NEG(quote_qty)
            CAST_64_NEG(base_qty)

            quote_qty := add(quote_qty, quote_delta)
            base_qty := add(base_qty, base_delta)

            if or(INVALID_I64(quote_qty), INVALID_I64(base_qty)) {
              SMART_REVERT(8)
            }

            sstore(state_ptr, or(
              and(state_data_0, mask_out(AssetState, 0, quote_qty, base_qty, asset_balance)),
              build(AssetState, 0, quote_qty, base_qty, 0, asset_balance)
            ))
          }

          /* Ensure position fits min limits */
          {
            let state_data_1 := sload(add(base_state_ptr, 1))

            let min_quote := attr(AssetState, 1, state_data_1, min_quote)
            let min_base := attr(AssetState, 1, state_data_1, min_base)

            CAST_64_NEG(min_quote)
            CAST_64_NEG(min_base)

            if or(slt(quote_qty, min_quote), slt(base_qty, min_base)) {
              SMART_REVERT(9)
            }
          }

          /* Check against limit */
          {
            let state_data_2 := sload(add(base_state_ptr, 2))

            /* Check if price fits limit */
            let negatives := add(slt(quote_qty, 1), mul(slt(base_qty, 1), 2))

            switch negatives
            /* Both negative */
            case 3 {
              /* if both values are 0, we're fine */
              if not(and(eq(quote_qty, 0), eq(base_qty, 0) {
                SMART_REVERT(10)
              }
            }
            /* long: quote_qty negative */
            case 1 {
              if iszero(base_qty) {
                SMART_REVERT(11)
              }

              let current_price := div(mul(sub(0, quote_qty), PRICE_UNITS), base_qty)
              if gt(current_price, attr(AssetState, 2, state_data_2, long_max_price)) {
                SMART_REVERT(12)
              }
            }
            /* short: base_qty negative */
            case 2 {
              if iszero(quote_qty) {
                SMART_REVERT(13)
              }

              let current_price := div(mul(quote_qty, PRICE_UNITS), sub(0, base_qty))
              if lt(current_price, attr(AssetState, 2, state_data_2, short_min_price)) {
                SMART_REVERT(14)
              }
            }
          }
        }

        /* ensure net balance is 0 for settlement group */
        if or(quote_net, base_net) {
          SMART_REVERT(15)
        }
      }

      let exchange_fees := mload(EXCHANGE_FEES_MEM)

      let exchange_id := mload(EXCHANGE_ID_MEM)
      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
      sstore(add(exchange_ptr, 1), exchange_fees)
    }
  }
}
