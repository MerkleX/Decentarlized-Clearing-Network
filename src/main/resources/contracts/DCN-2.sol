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
    uint256[5] memory return_value;

    assembly {
      let data := sload(pointer(Asset, assets_slot, asset_id))

      mstore(return_value, 96)
      mstore(add(return_value, 96), 4)
      mstore(add(return_value, 128), data)

      mstore(add(return_value, WORD_1), attr(Asset, 0, data, unit_scale))
      mstore(add(return_value, WORD_2), attr(Asset, 0, data, contract_address))

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
      mstore(add(return_value, WORD_2), attr(Exchange, 0, exchange_data, owner))

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
      let user_ptr := pointer(User, users_slot, user)
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
  returns (uint64 total_deposit, uint64 asset_balance) {
    uint256[2] memory return_values;

    assembly {
      let session_ptr := SESSION_PTR(user, exchange_id)
      let state_ptr := pointer(AssetState, session_ptr, asset_id)

      let state_data := sload(state_ptr)

      mstore(return_values, attr(AssetState, 0, state_data, total_deposit))
      mstore(add(return_values, WORD_1), attr(AssetState, 0, state_data, asset_balance))

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

      mstore(add(return_values, WORD_4), attr(AssetState, 2, state_data_2, limit_version))
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
}
