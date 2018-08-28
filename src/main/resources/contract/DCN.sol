contract DCN {
  uint256 creator;

  uint256 user_count;
  uint256 exchange_count;
  uint256 asset_count;

  uint256[2 * (2 ** 32)] exchanges;
  uint256[2 ** 16] assets;
  uint256[(2 + (2 ** 16)) * (2 ** 32)] users;
  uint256[(2 ** 32) * 34] sessions;

  /*

   //
   // EXCHANGES
   // 
   // EXCHANGE {
   //  0: name + address
   //  1: fee balance
   // }

   EXCHANGE_DEF {
    name              :  96,
    address           : 160,
   }

   //
   // ASSETS
   // 
   // ASSET {
   //  0: symbol + unit_scale + address
   // }

   ASSET_DEF {
    symbol            :  32,
    unit_scale        :  64,
    address           : 160,
   }

   //
   // USERS
   //
   // USER {
   //      0: manage address
   //      1: trade address
   //      2: ether_balance
   //      3: asset_1_balance
   //    ...: asset_n_balance
   //  65538: asset_65535_balance
   // }

   ADDRESS_DEF {
    _                 :  96,
    value             : 160,
   }

   //
   // SESSIONS
   //
   // SESSION {
   //   0: SESSION_DEF
   //   1: ETHER_DEF
   //
   //   2: POSITION_DEF
   //   3: LIMIT_DEF
   //
   //   4: POSITION_DEF
   //   5: LIMIT_DEF
   //
   //   ...
   //
   //   30: POSITION_DEF
   //   31: LIMIT_DEF
   //
   //   32: POSITION_DEF
   //   33: LIMIT_DEF
   // }

   SESSION_DEF {
    _                 :  60,
    position_count    :   4,
    user_id           :  32,
    exchange_id       :  32,
    max_ether_fees    :  64,
    expire_time       :  64,
   }

   ETHER_DEF {
    _                 : 192,
    balance           :  64,
   }

   POSITION_DEF {
    asset_id          :  16,
    _padding          :  48,
    ether_qty         :  64,
    asset_qty         :  64,
    asset_balance     :  64,
   }

   LIMIT_DEF {
    version           :  32,

    long_asset_qty     :  20,
    long_asset_qty_pow :   4,

    short_asset_qty     :  20,
    short_asset_qty_pow :   4,

    long_price        :  20,
    long_price_pow    :   4,

    short_price       :  20,
    short_price_pow   :   4,

    ether_shift   :  64,
    asset_shift   :  64,
   }
  */

  constructor() public {
      assembly {
          sstore(creator_slot, caller)
          sstore(assets_slot, BUILD_ASSET{ /* "ETH " */ 0x45544820, 100000000, 0 })
      }
  }

  function set_creator(address new_creator) public {
    assembly {
      let current_creator := sload(creator_slot)

      if eq(current_creator, caller) {
        sstore(creator_slot, new_creator)
      }
    }
  }

  function add_exchange(string name, address addr) public {
    uint256[1] memory return_value;

    assembly {
      let creator_address := sload(creator_slot)

      // Only the creator can add an exchange
      if iszero(eq(creator_address, caller)) {
        stop()
      }

      // Name must be 12 bytes long
      let name_len := mload(name)
      if iszero(eq(name_len, 12)) {
        stop()
      }

      // Do not overflow exchanges
      let exchange_count := sload(exchange_count_slot)
      if gt(exchange_count, 65535 /* 2^16 - 1 */) {
        stop()
      }

      let exchange_ptr := add(exchanges_slot, mul(exchange_count, 2))

      let name_data := mload(add(name, 32))
      let exchange_data := or(name_data, addr)
      sstore(exchange_ptr, exchange_data)

      mstore(return_value, exchange_count)
      exchange_count := add(exchange_count, 1)
      sstore(exchange_count_slot, exchange_count)

      log0(add(return_value, 28), 4)
    }
  }

  function get_exchange(uint32 id) public constant returns (string name, address addr, uint256 fee_balance) {
    uint256[5] memory return_value;

    assembly {
      let exchange_count := sload(exchange_count_slot)
      if iszero(lt(id, exchange_count)) {
        stop()
      }

      let exchange_ptr := add(exchanges_slot, mul(id, 2))
      let exchange_data := sload(exchange_ptr)

      // Store name
      mstore(return_value, 96 /* address 20 + uint256 32 */)
      mstore(add(return_value, 96), 12)
      mstore(add(return_value, 128), exchange_data)

      // Store addr
      mstore(add(return_value, 32), EXCHANGE(exchange_data).address)

      // Store fee_balance
      exchange_data := sload(add(exchange_ptr, 1))
      mstore(add(return_value, 64), exchange_data)

      return(return_value, 140)
    }
  }

  function get_exchange_count() public constant returns (uint256 count) {
    uint256[1] memory return_value;

    assembly {
      let data := sload(exchange_count_slot)
      mstore(return_value, data)
      return(return_value, 32)
    }
  }

  function add_asset(string symbol, uint64 unit_scale, address contract_address) public {
    uint256[1] memory return_value;

    assembly {
      let creator_address := sload(creator_slot)

      if iszero(eq(creator_address, caller)) {
        stop()
      }

      let asset_count := add(sload(asset_count_slot), 1)
      if gt(asset_count, 65535 /* 2^16 - 1 */) {
        stop()
      }

      let symbol_len := mload(symbol)
      if iszero(eq(symbol_len, 4)) {
        stop()
      }

      let asset_symbol := mload(add(symbol, 32))
      let data := or(asset_symbol, BUILD_ASSET{ 0, unit_scale, contract_address })
      sstore(add(assets_slot, asset_count), data)
      sstore(asset_count_slot, asset_count)

      mstore(return_value, asset_count)
      log0(add(return_value, 30), 2)
    }
  }

  function get_asset(uint16 asset_id) public constant
  returns (string symbol, uint64 unit_scale, address contract_address) {
    uint256[5] memory return_value;

    assembly {
      let data := sload(add(assets_slot, asset_id))

      mstore(return_value, 96)
      mstore(add(return_value, 96), 4)
      mstore(add(return_value, 128), data)

      mstore(add(return_value, 32), ASSET(data).unit_scale)
      mstore(add(return_value, 64), ASSET(data).address)

      return(return_value, 132)
    }
  }

  function get_asset_count() public constant returns (uint256 count) {
    uint256[1] memory return_value;

    assembly {
      let asset_count := sload(asset_count_slot)
      mstore(return_value, asset_count)
      return(return_value, 32)
    }
  }

  function add_user(address manage_address, address trade_address) public {
    uint256[1] memory return_value;

    assembly {
      // Must be sent from manage address
      if iszero(eq(manage_address, caller)) {
        stop()
      }

      // Do not overflow users
      let user_count := sload(user_count_slot)
      if gt(user_count, 4294967295 /* 2^32 - 1 */) {
        stop()
      }

      // Store data
      let user_ptr := add(users_slot, mul(user_count, 65539))
      sstore(user_ptr, manage_address)
      sstore(add(user_ptr, 1), trade_address)

      // Update user count
      sstore(user_count_slot, add(user_count, 1))

      // Add user id to log
      mstore(return_value, user_count)
      log0(add(return_value, 28), 4)
    }
  }

  function get_user(uint32 user_id) public constant
  returns (address manage_address, address trade_address) {
    uint256[2] memory return_value;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      let data := sload(user_ptr)
      mstore(return_value, data)

      data := sload(add(user_ptr, 1))
      mstore(add(return_value, 32), data)

      return(return_value, 64)
    }
  }

  function get_user_count() public constant returns (uint256 count) {
    uint256[1] memory return_value;

    assembly {
      let data := sload(user_count_slot)
      mstore(return_value, data)
      return(return_value, 32)
    }
  }

  function deposit_eth(uint32 user_id, bool check_self) public payable {
    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      switch check_self
      case 0 {
        let user_count := sload(user_count_slot)
        if iszero(lt(user_id, user_count)) {
          stop()
        }
      }
      default {
        let manage_address := sload(user_ptr)
        if iszero(eq(manage_address, caller)) {
          stop()
        }
      }

      let eth_ptr := add(user_ptr, 2)
      let current_balance := sload(eth_ptr)

      sstore(eth_ptr, add(current_balance, callvalue))
    }
  }

  function get_user_balance(uint32 user_id, uint16 asset_id) public constant returns (uint256 balance) {
    uint256[1] memory return_value;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      let asset_balance := sload(add(add(user_ptr, 2), asset_id))
      mstore(return_value, asset_balance)
      return(return_value, 32)
    }
  }

  function start_session(uint32 session_id, uint32 user_id,
                         uint32 exchange_id, uint64 expire_time) public {
    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      // Verify session is empty

      if SESSION(session_data).expire_time {
        stop()
      }

      // Authenticate user

      let user_ptr := add(users_slot, mul(user_id, 65539))
      let user_data := sload(user_ptr)
      if iszero(eq(user_data, caller)) {
        stop()
      }

      // Verify exchange id is valid

      let exchange_count := sload(exchange_count_slot)
      if iszero(gt(exchange_count, exchange_id)) {
        stop()
      }

      session_data := BUILD_SESSION {
        /* padding */ 0,
        /* position_count */ 0,
        /* user_id */ user_id,
        /* exchange_id */ exchange_id,
        /* max_ether_fees */ 0,
        /* expire_time */ expire_time
      }

      sstore(session_ptr, session_data)
    }
  }

  function position_deposit(uint32 session_id, uint32 user_id, uint16 asset_id, uint8 position_id, uint64 quantity) {
    assembly {
      /*
       * Validate Input
       */

      // Check for valid user_id
      let asset_count := sload(asset_count_slot)
      if gt(asset_id, asset_count) {
        stop()
      }

      let user_ptr := add(users_slot, mul(user_id, 65539))

      // Authenticate user
      let manage_address := sload(user_ptr)
      if iszero(eq(manage_address, caller)) {
        stop()
      }

      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      // Ensure session is valid
      if gt(SESSION(session_data).expire_time, timestamp) {
        stop()
      }

      // Autenticate session
      if iszero(eq(SESSION(session_data).user_id, user_id)) {
        stop()
      }

      let user_asset_ptr := add(add(user_ptr, 2), asset_id)
      let user_balance := sload(user_asset_ptr)

      let amount := quantity
      {
        let asset_data := sload(add(assets_slot, asset_id))
        let unit_scale := ASSET(asset_data).unit_scale
        amount := mul(amount, unit_scale)
      }

      // Ensure use has enough balance for deposit
      if gt(amount, user_balance) {
        stop()
      }

      let new_user_balance := sub(user_balance, amount)

      /*
       * Process
       */

      // deposit ETH
      if iszero(position_id) {
        // Asset id must be 0 (ETH)
        if asset_id {
          stop()
        }

        let session_eth_ptr := add(session_ptr, 1)
        let current_balance := sload(session_eth_ptr)

        current_balance := add(current_balance, quantity)

        // Protect against overflow
        if gt(current_balance, /* 2^64 - 1 */ 0xffffffffffffffff) {
          stop()
        }

        sstore(user_asset_ptr, new_user_balance)
        sstore(session_eth_ptr, current_balance)

        // TODO: log about success
        stop()
      }

      let position_count := SESSION(session_data).position_count

      // Cannot deposit into a position that doesn't exist
      if or(gt(position_id, position_count), gt(position_id, 16)) {
        stop()
      }

      // TODO: ids are 1 indexed, indexes are 0 indexed.

      let position_ptr := add(session_ptr, mul(position_id, 2))

      // Add position
      if eq(position_id, position_count) {
        sstore(user_asset_ptr, new_user_balance)

        sstore(position_ptr, BUILD_POSITION {
          asset_id,
          0, 0, 0,
          and(quantity, 0xffffffffffffffff)
        })

        sstore(add(position_ptr, 1), BUILD_LIMIT {
          1,
          0, 0,
          0, 0,
          0, 0,
          0, 0,
          0, 0
        })

        // TODO: log about success
        stop()
      }

      let position_data := sload(position_ptr)
      let current_balance := POSITION(position_data).asset_balance
      current_balance := add(current_balance, quantity)

      // Protect against overflow
      if gt(current_balance, /* 2^64 - 1 */ 0xffffffffffffffff) {
        stop()
      }

      position_data := and(position_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
      position_data := or(position_data, current_balance)

      sstore(user_asset_ptr, new_user_balance)
      sstore(position_ptr, position_data)

      // TODO: log about success
    }
  }

  function get_session(uint32 session_id) public constant
  returns (uint256 position_count, uint256 user_id, uint256 exchange_id, uint256 max_ether_fees, uint256 expire_time, uint256 ether_balance) {
    uint256[6] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      mstore(return_values, SESSION(session_data).position_count)
      mstore(add(return_values, 32), SESSION(session_data).user_id)
      mstore(add(return_values, 64), SESSION(session_data).exchange_id)
      mstore(add(return_values, 96), SESSION(session_data).max_ether_fees)
      mstore(add(return_values, 128), SESSION(session_data).expire_time)
      mstore(add(return_values, 160), sload(add(session_ptr, 1)))

      return(return_values, 192)
    }
  }

  function get_position(uint32 session_id, uint8 position_id) public constant
  returns (uint256 asset_id, uint256 ether_qty, uint256 asset_qty, uint256 asset_balance) {
    uint256[4] memory return_value;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      if or(lt(position_id, 1), gt(position_id, 16)) {
        stop()
      }

      let position_ptr := add(session_ptr, mul(position_id, 2))
      let position_data := sload(position_ptr)

      mstore(return_value, POSITION(position_data).asset_id)
      mstore(add(return_value, 32), POSITION(position_data).ether_qty)
      mstore(add(return_value, 64), POSITION(position_data).asset_qty)
      mstore(add(return_value, 96), POSITION(position_data).asset_balance)

      return(return_value, 128)
    }
  }

  function get_position_limit(uint32 session_id, uint8 position_id) public constant
  returns (uint256 version, uint256 long_asset_qty, uint256 short_asset_qty, uint256 long_price, uint256 short_price, uint256 ether_shift, uint256 asset_shift) {
    uint256[7] memory return_value;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      if or(lt(position_id, 1), gt(position_id, 16)) {
        stop()
      }

      let position_ptr := add(session_ptr, mul(position_id, 2))
      let limit_data := sload(add(position_ptr, 1))

      mstore(return_value, LIMIT(limit_data).version)

      let quant := mul(LIMIT(limit_data).long_asset_qty, exp(10, LIMIT(limit_data).long_asset_qty_pow))
      mstore(add(return_value, 32), quant)

      quant := mul(LIMIT(limit_data).short_asset_qty, exp(10, LIMIT(limit_data).short_asset_qty_pow))
      mstore(add(return_value, 64), quant)

      quant := mul(LIMIT(limit_data).long_price, exp(10, LIMIT(limit_data).long_price_pow))
      mstore(add(return_value, 96), quant)

      quant := mul(LIMIT(limit_data).short_price, exp(10, LIMIT(limit_data).short_price_pow))
      mstore(add(return_value, 128), quant)

      mstore(add(return_value, 160), LIMIT(limit_data).ether_shift)
      mstore(add(return_value, 192), LIMIT(limit_data).asset_shift)

      return(return_value, 224)
    }
  }

  function close_session(uint32 session_id) public {
    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      // Session still active
      if gt(SESSION(session_data).expire_time, timestamp) {
        stop()
      }

      let user_ptr := add(users_slot, mul(SESSION(session_data).user_id, 65539))
      let position_count := SESSION(session_data).position_count
      let positions_ptr := add(session_ptr, 2)

      let user_assets_ptr := add(user_ptr, 2)

      // Extract balances and clear used data
      for { let i := 0 } lt(i, position_count) { i := add(i, 1) } {
        let position_ptr := add(positions_ptr, mul(i, 2))
        let position_data := sload(position_ptr)

        // Update user balance with asset_balance in position
        let user_asset_ptr := add(user_assets_ptr, POSITION(position_data).asset_id)
        sstore(user_asset_ptr, add(sload(user_asset_ptr), POSITION(position_data).asset_balance))

        sstore(positions_ptr, 0)
        sstore(add(position_ptr, 1), 0)
      }
    }
  }

  /*
    WITHDRAW_DEF {
      session_id  :  32,
      _padding_1  :   4,
      position_id :   4,
      quantity    :  64,
      _padding_2  : 152,
    }
  */

  function process_withdraws(bytes requests) {
    assembly {
      let end := mload(requests)
      let cursor := add(requests, 32)
      end := add(cursor, end)

      for {} lt(cursor, end) { cursor := add(cursor, 13) } {
        let data := mload(cursor)

        let session_id := WITHDRAW(data).session_id
        let position_id := WITHDRAW(data).position_id
        let quantity := WITHDRAW(data).quantity

        let session_ptr := add(sessions_slot, mul(session_id, 34))
        let session_data := sload(session_ptr)

        let user_ptr := add(users_slot, mul(SESSION(session_data).user_id, 65539))

        switch position_id
        // Ethereum
        case 0 {
          let ether_ptr := add(session_ptr, 1)
          let ether_balance := sload(ether_ptr)

          if gt(quantity, ether_balance) {
            revert(0, 0)
          }

          // Decrement balance from position
          sstore(ether_ptr, sub(ether_balance, quantity))

          // TODO, think about overflow

          let user_ether_ptr := add(user_ptr, 2)
          let amount := mul(quantity, 100000000)
          sstore(user_ether_ptr, add(sload(user_ether_ptr), amount))
        }
        // Asset (not ethereum)
        default {
          if gt(position_id, SESSION(session_data).position_count) {
            revert(0, 0)
          }

          let position_ptr := add(session_ptr, mul(position_id, 2))
          let position_data := sload(position_ptr)
          let position_balance := POSITION(position_data).asset_balance

          if gt(quantity, position_balance) {
            revert(0, 0)
          }

          // Decrement balance from position
          position_balance := sub(position_balance, quantity)
          position_data := and(position_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          position_data := or(position_data, position_balance)
          sstore(position_ptr, position_data)

          let asset_id := POSITION(position_data).asset_id
          let asset_data := sload(add(assets_slot, asset_id))
          let amount := mul(quantity, ASSET(asset_data).unit_scale)

          let user_asset_ptr := add(add(user_ptr, 2), asset_id)
          sstore(user_asset_ptr, add(sload(user_asset_ptr), amount))
        }
      }
    }
  }
}
