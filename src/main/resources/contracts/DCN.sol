pragma solidity ^0.5.0;

#define WORD_1 32
#define WORD_2 64
#define WORD_3 96
#define WORD_4 128
#define WORD_5 160
#define WORD_6 192
#define WORD_7 224
#define WORD_8 256
#define WORD_9 288
#define 1WORD_0 320

#define CHAIN_ID 1111
#define VERSION  1

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
    uint64 balance;

    int64 min_quote;
    int64 min_base;
    int64 quote_shift;
    int64 base_shift;

    uint64 padding;
    uint64 limit_version;
    uint64 long_max_price;
    uint64 short_min_price;
  };

  struct QuoteAssetState {
    uint64 fee_limit;
    uint64 fee_used;
    uint64 total_deposit;
    uint64 balance;

    uint64 version;
    uint192 expire_time;

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
  #define EXCHANGE_COUNT pow(2, 32)
  #define ASSET_COUNT pow(2, 32)
  #define USER_COUNT pow(2, 160)

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
    uint256[5] memory return_value;

    assembly {
      let data := sload(pointer(Asset, assets_slot, asset_id))

      mstore(return_value, 96)
      mstore(add(return_value, 96), 4)
      mstore(add(return_value, 128), data)

      mstore(add(return_value, WORD_1), attr(Asset, 0, data, unit_scale))
      mstore(add(return_value, WORD_2), attr(Asset, 0, data, address))

      return(return_value, 132)
    }
  }

  function get_exchange(uint32 exchange_id) public view returns (string memory name, uint64 quote_asset_id, address addr, uint64 fee_balance) {
    /* [ name_offset, quote_asset_id, addr, fee_balance, name_len, name_data(8) ] */
    uint256[6] memory return_value;

    assembly {
      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
      let exchange_data := sload(exchange_ptr)

      /* Store name */
      mstore(return_value, WORD_4)
      mstore(add(return_value, WORD_4), 8)
      mstore(add(return_value, WORD_5), exchange_data)

      /* Store quote_asset_id */
      mstore(add(return_value, WORD_1), attr(Exchange, 0, exchange_data, quote_asset_id))

      /* Store addr */
      mstore(add(return_value, WORD_2), attr(Exchage, 0, exchange_data, owner))

      /* Store fee_balance */
      exchange_data := sload(add(exchange_ptr, 1))
      mstore(add(return_value, WORD_3), attr(Exchange, 1, exchange_data, fee_balance))

      return(return_value, 168)
    }
  }

  function get_exchange_count() public view returns (uint32 count) {
    uint256[1] memory return_value;

    assembly {
      let data := sload(exchange_count_slot)
      mstore(return_value, data)
      return(return_value, WORD_1)
    }
  }

  function get_asset_count() public view returns (uint32 count) {
    uint256[1] memory return_value;

    assembly {
      let asset_count := sload(asset_count_slot)
      mstore(return_value, asset_count)
      return(return_value, WORD_1)
    }
  }

  function get_balance(address user, uint32 asset_id) public view returns (uint256 return_balance) {
    uint256[1] memory return_value;

    assembly {
      let user_ptr := pointer(User, users_slots, user)
      let balance_ptr := pointer(u256, user_ptr, asset_id)

      mstore(return_value, sload(balance_ptr))
      return(return_value, WORD_1)
    }
  }

  function get_session(address user, uint32 exchange_id) public view
  returns (uint64 version, uint64 expire_time, uint64 fee_limit, uint64 fee_used) {
    uint256[4] memory return_values;

    assembly {
      let exchange_ptr := pointer(Exchange, exchanges_slot, exchange_id)
      let quote_asset_id := attr(Exchange, 0, sload(exchange_ptr), quote_asset_id)

      let session_ptr := SESSION_PTR(user, exchange_id)
      let quote_state_ptr := pointer(QuoteAssetState, session_ptr, quote_asset_id)

      let state_data_0 := sload(session_ptr)
      let state_data_1 := sload(add(session_ptr, 1))

      mstore(return_values, attr(QuoteAssetState, 1, state_data_1, version))
      mstore(add(return_values, WORD_1), attr(QuoteAssetState, 1, state_data_1, expire_time))
      mstore(add(return_values, WORD_2), attr(QuoteAssetState, 0, state_data_0, fee_limit))
      mstore(add(return_values, WORD_3), attr(QuoteAssetState, 0, state_data_0, fee_used))

      return(return_values, WORD_4)
    }
  }

  function get_session_balance(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (uint64 total_deposit, uint64 balance) {
    uint256[2] memory return_values;

    assembly {
      let session_ptr := SESSION_PTR(user, exchange_id)
      let state_ptr := pointer(AssetState, session_ptr, asset_id)

      let state_data := sload(state_ptr)

      mstore(return_values, attr(AssetState, 0, state_data, total_deposit))
      mstore(add(return_values, WORD_1), attr(AssetState, 0, state_data, balance))

      return(return_values, WORD_2)
    }
  }

  function get_session_state(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (int64 quote_qty, int64 base_qty, int64 quote_shift, int64 base_shift,
           uint64 version, int64 min_quote, int64 min_base, uint64 long_max_price, uint64 short_min_price) {
    uint256[8] memory return_values;

    assembly {
      let session_ptr := SESSION_PTR(user, exchange_id)
      let state_ptr := pointer(AssetState, session_ptr, asset_id)

      let state_data_0 := sload(state_ptr)
      let state_data_1 := sload(add(state_ptr, 1))
      let state_data_2 := sload(add(state_ptr, 2))

      mstore(return_values, attr(AssetState, 0, state_data_0, quote_qty))
      mstore(add(return_values, WORD_1), attr(AssetState, 0, state_data_0, base_qty))
      mstore(add(return_values, WORD_2), attr(AssetState, 1, state_data_1, quote_shift))
      mstore(add(return_values, WORD_3), attr(AssetState, 1, state_data_1, base_shift))

      mstore(add(return_values, WORD_4), attr(AssetState, 2, state_data_2, version))
      mstore(add(return_values, WORD_5), attr(AssetState, 1, state_data_1, min_quote))
      mstore(add(return_values, WORD_6), attr(AssetState, 1, state_data_1, min_base))

      mstore(add(return_values, WORD_7), attr(AssetState, 2, state_data_2, long_max_price))
      mstore(add(return_values, WORD_8), attr(AssetState, 2, state_data_2, short_min_price))

      return(return_values, WORD_9)
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

      let exchange_ptr := add(exchanges_slot, mul(exchange_count, EXCHANGE_SIZE))

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
    uint256[4] memory transfer_in;
    uint256[1] memory transfer_out;

    assembly {
      /* Validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if or(iszero(asset_id), iszero(lt(asset_id, asset_count))) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }
      }

      mstore(transfer_in, /* transferFrom(address,address,uint256) */ 0x23b872dd00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), caller)
      mstore(add(transfer_in, 36), address)
      mstore(add(transfer_in, 68), amount)

      let asset_data := sload(add(assets_slot, asset_id))
      let asset_address := ASSET(asset_data).address

      /* call external contract */
      {
        let success := call(
          gas,
          asset_address,
          /* don't send any ether */ 0,
          transfer_in,
          /* transfer_in size (bytes) */ 100,
          transfer_out,
          /* transfer_out size (bytes) */ 32
        )

        if iszero(success) {
          mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
        }
        let result := mload(transfer_out)
        if iszero(result) {
          mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
        }
      }

      let user_ptr := add(users_slot, mul(caller, USER_SIZE))
      let asset_ptr := add(user_ptr, asset_id)
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
    uint256[3] memory transfer_in;
    uint256[1] memory transfer_out;
    uint256[1] memory revert_reason;

    assembly {
      if iszero(amount) {
        stop()
      }

      /*
       * Note, don't need to validate asset_id as will have 0 funds if doesn't exist
       */

      let user_ptr := add(users_slot, mul(caller, USER_SIZE))
      let asset_ptr := add(user_ptr, asset_id)
      let current_balance := sload(asset_ptr)
      if lt(current_balance, amount) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      mstore(transfer_in, /* transfer(address,uint256) */ 0xa9059cbb00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), destination)
      mstore(add(transfer_in, 36), amount)

      let asset_data := sload(add(assets_slot, asset_id))
      let asset_address := ASSET(asset_data).address

      let success := call(
        gas,
        asset_address,
        /* don't send any ether */ 0,
        transfer_in,
        /* transfer_in size (bytes) */ 68,
        transfer_out,
        /* transfer_out size (bytes) */ 32
      )

      if iszero(success) {
        mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
      }

      let result := mload(transfer_out)
      if iszero(result) {
        mstore(revert_reason, 4) revert(add(revert_reason, 31), 1)
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
  function update_session(uint32 exchange_id, uint64 expire_time) public {
    uint256[1] memory revert_reason;
    uint256[3] memory log_data_ptr;

    assembly {
      /* ensure: expire_time >= timestamp + 12 hours && expire_time <= timestamp + 30 days */
      if or(gt(add(timestamp, 43200), expire_time), gt(expire_time, add(timestamp, 2592000))) {
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      /* ensure: exchange_id < exchange_count */
      {
        let exchange_count := sload(exchange_count_slot)
        if iszero(lt(exchange_id, exchange_count)) {
          mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
        }
      }

      let session_ptr := add(sessions_slot, mul(
        add(mul(caller, EXCHANGE_COUNT), exchange_id),
        SESSION_SIZE
      ))

      /* Update expire time */
      {
        let time_ptr := add(session_ptr, 1)
        let time_data := sload(time_ptr)
        sstore(time_ptr, BUILD_SESSION_TIME {
          /* padding */ 0,
          add(SESSION_TIME(time_data).version, 1),
          expire_time
        })
      }

      /* Log */
      mstore(log_data_ptr, caller)
      mstore(add(log_data_ptr, 32), exchange_id)
      log1(
        log_data_ptr, 64,
        /* SessionUpdated */ 0x1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7
      )
    }
  }

  /*
   * Test, TODO
   */
  function deposit_asset_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[1] memory revert_reason;
    uint256[4] memory transfer_in;
    uint256[1] memory transfer_out;
    uint256[3] memory log_data_ptr;

    assembly {
      /* validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if or(iszero(asset_id), iszero(lt(asset_id, asset_count))) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }
      }

      let asset_data := sload(add(assets_slot, asset_id))
      let amount := mul(quantity, ASSET(asset_data).unit_scale)
      let asset_address := ASSET(asset_data).address

      mstore(transfer_in, /* transferFrom(address,address,uint256) */ 0x23b872dd00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), caller)
      mstore(add(transfer_in, 36), address)
      mstore(add(transfer_in, 68), amount)

      /* call external contract */
      {
        let success := call(
          gas,
          asset_address,
          /* don't send any ether */ 0,
          transfer_in,
          /* transfer_in size (bytes) */ 100,
          transfer_out,
          /* transfer_out size (bytes) */ 32
        )

        /* verify call was successful */
        if iszero(success) {
          mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
        }
        let result := mload(transfer_out)
        if iszero(result) {
          mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
        }
      }

      /* deposit funds into session */
      let session_ptr := add(sessions_slot, mul(
        add(mul(caller, EXCHANGE_COUNT), exchange_id),
        SESSION_SIZE
      ))

      let position_ptr := add(session_ptr, mul(3, asset_id))

      let position_data := sload(position_ptr)
      let total_deposit := and(add(POSITION(position_data).total_deposit, quantity), 0xFFFFFFFFFFFFFFFF)
      let base_balance := add(POSITION(position_data).base_balance, quantity)

      /* check for base_balance overflow */
      if gt(base_balance, 0xFFFFFFFFFFFFFFFF) {
        mstore(revert_reason, 4) revert(add(revert_reason, 31), 1)
      }

      sstore(
        position_ptr,
        or(
          and(position_data, 0xffffffffffffffffffffffffffffffff00000000000000000000000000000000),
          or(lshift(total_deposit, 64), base_balance)
        )
      )

      mstore(log_data_ptr, caller)
      mstore(add(log_data_ptr, 32), exchange_id)
      mstore(add(log_data_ptr, 64), asset_id)
      log1(
        log_data_ptr, 96,
        /* PositionUpdated */ 0x80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500
      )
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
    uint256[1] memory session_id_ptr;
    uint256[4] memory session_deposit_ptr;

    assembly {
      /*
       * Note, doesn't check exchange_id because safe to deposit into a non existent exchange.
       * You cannot update expire time if the exchnage is not created so you can always withdraw from
       * the session.
       *
       * Note, asset_id is not checked because if it doesn't exist source of funds will be 0
       */
      let session_ptr := add(sessions_slot, mul(
        add(mul(caller, EXCHANGE_COUNT), exchange_id),
        SESSION_SIZE
      ))

      let user_ptr := add(users_slot, mul(caller, USER_SIZE))
      let asset_ptr := add(user_ptr, asset_id)
      let base_balance := sload(asset_ptr)

      /* Update base_balance variable */
      {
        /* Convert quantity to amount using unit_scale */
        let asset_data := sload(add(assets_slot, asset_id))
        let unit_scale := ASSET(asset_data).unit_scale

        /* Note mul cannot overflow as both numbers are 64 bit and result is 256 bits */
        let amount := mul(quantity, unit_scale)

        /* Ensure user has enough base_balance for deposit */
        if gt(amount, base_balance) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }

        base_balance := sub(base_balance, amount)
      }

      /* Update session base_balance */
      let position_ptr := add(session_ptr, mul(3, asset_id))
      let position_data := sload(position_ptr)

      let total_deposit := and(add(POSITION(position_data).total_deposit, quantity), 0xFFFFFFFFFFFFFFFF)
      let position_balance := add(POSITION(position_data).base_balance, quantity)

      /* ensure position_balance doesn't overflow */
      if gt(position_balance, 0xFFFFFFFFFFFFFFFF) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      /* Update balances */
      sstore(asset_ptr, base_balance)
      sstore(position_ptr, or(
        and(position_data, 0xffffffffffffffffffffffffffffffff00000000000000000000000000000000),
        or(lshift(total_deposit, 64), position_balance)
      ))

      /* log */
      mstore(session_deposit_ptr, caller)
      mstore(add(session_deposit_ptr, 32), exchange_id)
      mstore(add(session_deposit_ptr, 64), asset_id)
      log1(
        session_deposit_ptr, 96,
        /* PositionUpdated */ 0x80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500
      )
    }
  }

  /*
     #define SIG_HASH_HEADER 0x1901000000000000000000000000000000000000000000000000000000000000
     #define DCN_HEADER_HASH 0x8bdc799ab1e4f88b464481578308e5bde325b7ed088fe2b99495c7924d58c7f9
     #define UPDATE_LIMIT_TYPE_HASH 0x74be7520fc933d8061b6cf113d28a772f7a40539ab5e0e8276dd066dd71a7d69

     Layout
     [ UPDATE_LIMIT_ADDR, UPDATE_LIMIT_1, UPDATE_LIMIT_2, UPDATE_LIMIT_3, sig_r, sig_s, SIG_V ]

     #define LIMIT_UPDATE_SIZE 149
     #define UPDATE_LIMIT_ADDR_SIZE 20
     #define UPDATE_LIMIT_1_SIZE 32
     #define UPDATE_LIMIT_2_SIZE 32


     UPDATE_LIMIT_ADDR_DEF {
      user_address : 160,
     }

     UPDATE_LIMIT_1_DEF {
      exchange_id : 32,
      asset_id : 32,
      version : 64,
      max_long_price : 64,
      min_short_price : 64,
     }

     UPDATE_LIMIT_2_DEF {
      min_quote_qty : 64,
      min_base_qty : 64,
      quote_shift   : 64,
      base_shift   : 64,
     }

     SIG_V_DEF {
      sig_v : 8,
     }

     #define U128_MASK 0xffffffffffffffffffffffffffffffff
  */

  function set_limit(bytes memory data) public {
    uint256[1] memory revert_reason;
    uint256[10] memory data_hash_buffer;
    uint256[4] memory final_hash_buffer;

    uint256 user_addr;

    assembly {
      let data_size := mload(data)
      let cursor := add(data, 32)

      /* ensure data size is correct */
      if iszero(eq(data_size, LIMIT_UPDATE_SIZE)) {
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      /* load user_address */
      let update_data := mload(cursor)
      cursor := add(cursor, UPDATE_LIMIT_ADDR_SIZE)
      user_addr := UPDATE_LIMIT_ADDR(update_data).user_address

      /* start hash buffer */
      mstore(data_hash_buffer, UPDATE_LIMIT_TYPE_HASH)

      /* load update_limit_1 */
      update_data := mload(cursor)
      cursor := add(cursor, UPDATE_LIMIT_1_SIZE)

      let position_ptr := 0

      {
        let exchange_id := UPDATE_LIMIT_1(update_data).exchange_id
        mstore(add(data_hash_buffer, WORD_1), exchange_id)

        let asset_id := UPDATE_LIMIT_1(update_data).asset_id
        mstore(add(data_hash_buffer, WORD_2), asset_id)

        let exchange_data := sload(add(
          exchanges_slot,
          mul(exchange_id, EXCHANGE_SIZE)
        ))

        /* exchange address must be caller */
        let exchange_address := EXCHANGE(exchange_data).address
        if iszero(eq(caller, exchange_address)) {
          mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
        }

        let quote_asset_id := EXCHANGE(exchange_data).quote_asset_id

        position_ptr := add(
          add(
            add(sessions_slot, mul(
              add(mul(user_addr, EXCHANGE_COUNT), exchange_id),
              SESSION_SIZE
            )),
            mul(asset_id, SESSION_ASSET_SIZE)
          ),
          mul(quote_asset_id, SESSION_ASSET_SIZE)
        )
      }

      {
        let version := UPDATE_LIMIT_1(update_data).version

        mstore(add(data_hash_buffer, WORD_3), version)

        /* version must increase */
        {
          let version_data := sload(add(position_ptr, 2))
          let current_version := PRICE_LIMIT(version_data).limit_version

          if iszero(lt(current_version, version)) {
            mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
          }
        }

        let max_long_price := UPDATE_LIMIT_1(update_data).max_long_price
        mstore(add(data_hash_buffer, WORD_4), max_long_price)

        let min_short_price := UPDATE_LIMIT_1(update_data).min_short_price
        mstore(add(data_hash_buffer, WORD_5), min_short_price)

        sstore(add(position_ptr, 2), BUILD_PRICE_LIMIT{
          0,
          version,
          max_long_price,
          min_short_price
        })
      }

      update_data := mload(cursor)
      cursor := add(cursor, UPDATE_LIMIT_2_SIZE)

      {
        let pos_limit := 0

        /* Note, set pos_limit before handling neg so we don't need to mask */
        {
          let min_quote_qty := UPDATE_LIMIT_2(update_data).min_quote_qty
          pos_limit := BUILD_POS_LIMIT{ min_quote_qty, 0, 0, 0 }

          if and(min_quote_qty, NEG_64_FLAG) {
            min_quote_qty := or(min_quote_qty, I64_TO_NEG)
          }
          mstore(add(data_hash_buffer, WORD_6), min_quote_qty)
        }

        {
          let min_base_qty := UPDATE_LIMIT_2(update_data).min_base_qty
          pos_limit := or(pos_limit, BUILD_POS_LIMIT{ 0, min_base_qty, 0, 0 })

          if and(min_base_qty, NEG_64_FLAG) {
            min_base_qty := or(min_base_qty, I64_TO_NEG)
          }
          mstore(add(data_hash_buffer, WORD_7), min_base_qty)
        }

        let quote_shift := UPDATE_LIMIT_2(update_data).quote_shift
        pos_limit := or(pos_limit, BUILD_POS_LIMIT{ 0, 0, quote_shift, 0 })

        if and(quote_shift, NEG_64_FLAG) {
          quote_shift := or(quote_shift, I64_TO_NEG)
        }
        mstore(add(data_hash_buffer, WORD_8), quote_shift)

        let base_shift := UPDATE_LIMIT_2(update_data).base_shift
        pos_limit := or(pos_limit, BUILD_POS_LIMIT{ 0, 0, 0, base_shift })

        if and(base_shift, NEG_64_FLAG) {
          base_shift := or(base_shift, I64_TO_NEG)
        }
        mstore(add(data_hash_buffer, WORD_9), base_shift)

        /* Normalize shift against existing */
        {
          let current_pos_limit_data := sload(add(position_ptr, 1))

          {
            let current_quote_shift := POS_LIMIT(current_pos_limit_data).quote_shift
            if and(current_quote_shift, NEG_64_FLAG) {
              current_quote_shift := or(current_quote_shift, I64_TO_NEG)
            }
            quote_shift := sub(quote_shift, current_quote_shift)
          }

          {
            let current_base_shift := POS_LIMIT(current_pos_limit_data).base_shift
            if and(current_base_shift, NEG_64_FLAG) {
              current_base_shift := or(current_base_shift, I64_TO_NEG)
            }
            base_shift := sub(base_shift, current_base_shift)
          }
        }

        let position_data := sload(position_ptr)
        let quote_qty := add(quote_shift, POSITION(position_data).quote_qty)
        let base_qty := add(base_shift, POSITION(position_data).base_qty)

        sstore(position_ptr, or(
          and(position_data, U128_MASK),
          BUILD_POSITION{quote_qty, base_qty, 0, 0}
        ))
        sstore(add(position_ptr, 1), pos_limit)
      }

      let hash := keccak256(data_hash_buffer, 1WORD_0)

      {
        let final_ptr := data_hash_buffer
        mstore(final_ptr, SIG_HASH_HEADER)
        final_ptr := add(final_ptr, 2)
        mstore(final_ptr, DCN_HEADER_HASH)
        final_ptr := add(final_ptr, WORD_1)
        mstore(final_ptr, hash)
      }

      hash := keccak256(data_hash_buffer, 66)
      mstore(data_hash_buffer, hash)

      update_data := mload(cursor)
      cursor := add(cursor, WORD_1)
      mstore(add(data_hash_buffer, WORD_1), update_data)

      update_data := mload(cursor)
      cursor := add(cursor, WORD_1)
      mstore(add(data_hash_buffer, WORD_2), update_data)

      update_data := mload(cursor)
      mstore(add(data_hash_buffer, WORD_3), SIG_V(update_data).sig_v)
    }

    uint256 recover_address = uint256(ecrecover(
      bytes32(data_hash_buffer[0]),
      uint8(data_hash_buffer[3]),
      bytes32(data_hash_buffer[1]),
      bytes32(data_hash_buffer[2])
    ));

    assembly {
      if iszero(eq(recover_address, user_addr)) {
        mstore(revert_reason, 4) revert(add(revert_reason, 31), 1)
      }
    }
  }

  /*

    #define GROUPS_HEADER_SIZE 4
    #define GROUP_HEADER_SIZE 40
    #define SETTLEMENT_SIZE 352

    GROUPS_HEADER_DEF {
      exchange_id: 32,
    }

    GROUP_HEADER_DEF {
      asset_id : 32,
      user_count : 8,
    }

    SETTLEMENT_ADDR_DEF {
      user_address : 160,
    }

    SETTLEMENT_DATA_DEF {
      quote_delta : 64,
      base_delta : 64,
      fees        : 64,
    }

    #define NEG_64_FLAG 0x8000000000000000
    #define I64_MIN 0xffffffffffffffffffffffffffffffffffffffffffffffff8000000000000000
    #define I64_TO_NEG 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000
    #define U64_INV_MASK 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000
    #define U64_MASK 0xffffffffffffffff
    #define I64_MAX 0x7fffffffffffffff
    #define PRICE_UNITS 100000000
    #define POSITION_BALANCES_MASK 0xffffffffffffffffffffffffffffffff
   */

  function apply_settlement_groups(bytes memory data) public {
    uint256[1] memory revert_reason;

    assembly {
      let cursor := add(data, 1)
      let data_end := add(cursor, mload(data))

      let header_data := mload(cursor)
      cursor := add(cursor, GROUPS_HEADER_SIZE)

      let exchange_id := GROUPS_HEADER(header_data).exchange_id
      let quote_asset_id := 0
      {
        let exchange_count := sload(exchange_count_slot)

        /* exchange id must be valid */
        if iszero(lt(exchange_id, exchange_count)) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }

        let exchange_data := sload(add(exchanges_slot, mul(exchange_id, EXCHANGE_SIZE)))
        quote_asset_id := EXCHANGE(exchange_data).quote_asset_id
      }

      /* keep looping while there is space for a header */
      for {} iszero(lt(sub(data_end, cursor), GROUP_HEADER_SIZE)) {} {
        header_data := mload(cursor)
        let user_count := GROUP_HEADER(header_data).user_count

        // TODO: validate asset_id?

        let asset_id := GROUP_HEADER(header_data).asset_id
        let cursor_end := add(cursor, add(mul(user_count, SETTLEMENT_SIZE), GROUP_HEADER_SIZE))

        /* make sure there is enough size for the group */
        if gt(cursor_end, data_end) {
          revert(0, 0)
        }

        let quote_net := 0
        let base_net := 0

        for {} lt(cursor, cursor_end) {} {
          header_data := mload(cursor)

          let session_ptr := add(
            sessions_slot,
            mul(
              add(
                mul(
                  SETTLEMENT_ADDR(header_data).user_address,
                  EXCHANGE_COUNT
                ),
                exchange_id
              ),
              SESSION_SIZE
            )
          )

          cursor := add(cursor, 20)
          let settlement_data := mload(cursor)

          let quote_delta := SETTLEMENT_DATA(settlement_data).quote_delta
          let base_delta := SETTLEMENT_DATA(settlement_data).base_delta
          let fees := SETTLEMENT_DATA(settlement_data).fees

          /* convert i64 to i256 */
          if and(quote_delta, NEG_64_FLAG) {
            quote_delta := or(quote_delta, I64_TO_NEG)
          }
          if and(base_delta, NEG_64_FLAG) {
            base_delta := or(base_delta, I64_TO_NEG)
          }

          /* update net totals */
          quote_net := add(quote_net, quote_delta)
          base_net := add(base_net, base_delta)

          /* update quote balance */
          {
            let quote_ptr := add(session_ptr, mul(quote_asset_id, SESSION_ASSET_SIZE))

            /* ensure we're withing expire time */
            {
              let expire_time_data := sload(add(quote_ptr, 1))
              let expire_time := SESSION_TIME(expire_time_data).expire_time
              if gt(expire_time, timestamp) {
                mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
              }
            }

            let quote_position_data := sload(quote_ptr)

            let quote_balance := QUOTE_POSITION(quote_position_data).quote_balance
            quote_balance := add(quote_balance, quote_delta)
            quote_balance := sub(quote_balance, SETTLEMENT_DATA(settlement_data).fees)

            /* make sure quote balance is positive and doesn't overflow */
            if gt(quote_balance, U64_MASK) {
              revert(0, 0)
            }
            sstore(quote_ptr, or(and(quote_position_data, U64_INV_MASK), quote_balance))
          }

          let position_ptr := add(session_ptr, mul(asset_id, SESSION_ASSET_SIZE))
          let position_data := sload(position_ptr)
          let base_balance := POSITION(position_data).base_balance

          base_balance := add(base_balance, base_delta)
          if gt(base_balance, U64_MASK) {
            revert(0, 0)
          }

          /* load position and convert i64 to i256 */
          let quote_qty := POSITION(position_data).quote_qty
          if and(quote_qty, NEG_64_FLAG) {
            quote_qty := or(quote_qty, I64_TO_NEG)
          }
          let base_qty := POSITION(position_data).base_qty
          if and(base_qty, NEG_64_FLAG) {
            base_qty := or(base_qty, I64_TO_NEG)
          }

          /* Note, shift is applied in limit update and is factored into _qty */

          quote_qty := add(quote_qty, quote_delta)
          base_qty := add(base_qty, base_delta)

          position_data := BUILD_POSITION{
            quote_qty,
            base_qty,
            POSITION(position_data).total_deposit,
            base_balance
          }
          sstore(position_ptr, position_data)

          if or(sgt(quote_qty, I64_MAX), sgt(base_qty, I64_MAX)) {
            revert(0, 0)
          }

          /* Ensure position fits min limits */
          {
            let limit_data := sload(add(position_ptr, 1))

            let min_quote := POS_LIMIT(limit_data).min_quote
            if and(min_quote, NEG_64_FLAG) {
              min_quote := or(min_quote, I64_TO_NEG)
            }

            let min_base := POS_LIMIT(limit_data).min_base
            if and(min_base, NEG_64_FLAG) {
              min_base := or(min_base, I64_TO_NEG)
            }

            if or(slt(quote_qty, min_quote), slt(base_qty, min_base)) {
              revert(0, 0)
            }
          }

          /* Ensure there is no overflow */
          if or(slt(quote_qty, I64_MIN), slt(base_qty, I64_MIN)) {
            revert(0, 0)
          }

          let price_limit_data := sload(add(position_ptr, 2))

          /* Check if price fits limit */
          let negatives := add(slt(quote_qty, 0), mul(slt(base_qty, 0), 2))
          switch negatives
          /* Both negative */
          case 3 {
            revert(0, 0)
          }
          /* long: quote_qty negative */
          case 1 {
            if iszero(base_qty) {
              revert(0, 0)
            }

            let current_price := div(mul(sub(0, quote_qty), PRICE_UNITS), base_qty)
            if gt(current_price, PRICE_LIMIT(price_limit_data).long_max_price) {
              revert(0, 0)
            }
          }
          /* short: base_qty negative */
          case 2 {
            if iszero(quote_qty) {
              revert(0, 0)
            }

            let current_price := div(mul(quote_qty, PRICE_UNITS), sub(0, base_qty))
            if lt(current_price, PRICE_LIMIT(price_limit_data).short_min_price) {
              revert(0, 0)
            }
          }
        }

        /* ensure net balance is 0 for settlement group */
        if or(quote_net, base_net) {
          revert(0, 0)
        }
      }
    }
  }
}
