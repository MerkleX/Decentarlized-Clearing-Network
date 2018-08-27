contract DCN {
  uint256 creator;

  uint32 user_count;
  uint32 exchange_count;
  uint16 asset_count;

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

    max_asset_qty     :  20,
    max_asset_qty_pow :   4,

    min_asset_qty     :  20,
    min_asset_qty_pow :   4,

    long_price        :  20,
    long_price_pow    :   4,

    short_price       :  20,
    short_price_pow   :   4,

    ether_qty_shift   :  64,
    asset_qty_shift   :  64,
   }
  */

  constructor() public {
      assembly {
          sstore(creator_slot, address)
      }
  }

  function set_creator(address new_creator) public {
    assembly {
      let current_creator := sload(creator_slot)

      if eq(current_creator, address) {
        sstore(creator_slot, new_creator)
      }
    }
  }

  function add_exchange(string name, address addr) public {
    uint256[1] memory return_value;

    assembly {
      let creator_address := sload(creator_slot)

      // Only the creator can add an exchange
      if iszero(eq(address, creator_address)) {
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

  function add_asset(string symbol, uint64 unit_scale, address contract_address) public {
    uint256[1] memory return_value;

    assembly {
      let creator_addr := sload(creator_slot)

      if iszero(eq(address, creator_addr)) {
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

  function get_asset_count() public constant returns (uint16 count) {
    uint256[1] memory return_value;

    assembly {
      let asset_count := sload(asset_count_slot)
      mstore(return_value, asset_count)
      return(add(return_value, 30), 2)
    }
  }

  function add_user(address manage_address, address trade_address) public {
    uint256[1] memory return_value;

    assembly {
      // Must be sent from manage address
      if iszero(eq(manage_address, address)) {
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

  function get_balance(uint32 user_id, uint16 asset_id) public constant returns (uint64 balance) {
    uint256[1] memory return_value;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      let balance := sload(add(add(user_ptr, 2), asset_id))
      mstore(return_value, balance)
      return(add(return_value, 24), 8)
    }
  }

  function start_session(uint32 session_id, uint32 user_id,
                         uint32 exchange_id, uint64 expire_time) public {
    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      if SESSION(session_data).expire_time {
        stop()
      }

      session_data := BUILD_SESSION {
        0,
        0,
        user_id,
        exchange_id,
        0,
        expire_time,
      }

      sstore(session_ptr, session_data)
    }
  }

  function close_session(uint32 session_id) {
    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 35))
      let session_data := sload(session_ptr)

      // Session still active
      if gt(SESSION(session_data).timestamp, timestamp) {
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
}
