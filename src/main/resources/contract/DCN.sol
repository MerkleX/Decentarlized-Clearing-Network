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
     //  65539: asset_65536_balance
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
     //   2: POSITION_DEF
     //   3: LIMIT_VERSION_DEF
     //   4: LIMIT_DEF
     // }

     SESSION_DEF {
      user_id           :  32,
      exchange_id       :  32,
      position_count    :   8,
      expire_time       :  64,
      max_ether_fees    :  64,
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

     LIMIT_VERSION_DEF {
      _                 : 192,
      version           :  64,
     }

     LIMIT_DEF {
      max_asset_qty     :  28,
      max_asset_qty_pow :   4,

      min_asset_qty     :  28,
      min_asset_qty_pow :   4,

      long_price        :  28,
      long_price_pow    :   4,

      short_price       :  28,
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
        if gt(exchange_count, 65535) {
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
        if iszero(lt(asset_count, 65536)) {
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

    // function add_user() public {
    // }

    // function get_balance(uint32 user_id, uint8 asset_index) public constant returns (uint256) {
    //   uint256[1] memory return_value;
    //   assembly {
    //     let ptr := add(sessions_slot, add(or(mul(user_id, 524288), mul(asset_id, 8)), 6))
    //     mstore(return_value, sload(ptr))
    //     return(return_value, 32)
    //   }
    // }
     }
