pragma solidity ^0.4.24;

contract DCN {
  event SessionStarted(uint256 session_id);
  event PositionAdded(uint256 session_id);
  event PositionDeposit(uint256 session_id, uint256 position_id); 

  uint256 creator;

  uint256 exchange_count;
  uint256 asset_count;

  /* Memory Layout */

  uint256[/* size */ 2                   /* count */ * (2 **  64)]  exchanges;
  /*
    EXCHANGES {
     0: name + address
     1: fee balance
    }

    EXCHANGE_DEF {
     name              :  96,
     address           : 160,
    }
  */


  uint256[/* size */ 1                   /* count */ * (2 **  32)]  assets;
  /*
    ASSET_DEF {
     symbol            :  32,
     unit_scale        :  64,
     address           : 160,
    }
  */


  uint256[/* size */ (2 ** 32)           /* count */ * (2 ** 160)] users;
  /*
    USER {
              0: ether_balance
              1: asset_1_balance
              n: asset_n_balance
     4294967295: asset_4294967295_balance
    }
  */


  uint256[/* size */ (3 * (1 + 2 ** 32)) /* count */ * (2 **  64)]  sessions;
  /*
    SESSION {
               0: turnover + exchange_id + fee_limit + expire_time
               1: fee_limit + expire_time + total_deposit
               2: fee_used + ether_balance
             
               3: ASSET_1_POS_LIMIT_DEF
               4: ASSET_1_PRICE_LIMIT_DEF
               5: ASSET_1_POSITION_DEF
             
               6: ASSET_2_POS_LIMIT_DEF
               7: ASSET_2_PRICE_LIMIT_DEF
               8: ASSET_2_POSITION_DEF

             n*3: ASSET_N_POS_LIMIT_DEF
           n*3+1: ASSET_N_PRICE_LIMIT_DEF
           n*3+2: ASSET_N_POSITION_DEF
    
      12884901888: ASSET_4294967296_POS_LIMIT_DEF
      12884901889: ASSET_4294967296_PRICE_LIMIT_DEF
      12884901890: ASSET_4294967296_POSITION_DEF
    }

    SESSION_ID_DEF {
      turnover          :  32,
      exchange_id       :  64,
      user_address      : 160,
    }

    SESSION_LIMIT_DEF {
      padding           :  32,
      active_assets     :  32,
      expire_time       :  64,
      fee_limit         :  64,
      fee_used          :  64,
    }

    ETHER_STATE_DEF {
      padding           : 128,
      total_deposit     :  64,
      ether_balance     :  64,
    }

    POSITION_LIMIT {
      min_ether         :  64,
      min_asset         :  64,
      ether_shift       :  64,
      asset_shift       :  64,
    }

    PRICE_LIMIT {
      padding           :  96,
      limit_version     :  32,
      long_max_price    :  64,
      short_max_price   :  64,
    }

  */

  constructor() public {
      assembly {
          sstore(creator_slot, caller)
          sstore(assets_slot, BUILD_ASSET{ /* "ETH " */ 0x45544820, 10000000000, 0 })
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

  function get_creator() public constant returns (address dcn_creator) {
    return address(creator);
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
      if gt(exchange_count, 18446744073709551615 /* 2^64 - 1 */) {
        stop()
      }

      let exchange_ptr := add(exchanges_slot, mul(exchange_count, /* EXCHANGE_SIZE */ 2))

      let name_data := mload(add(name, 32))
      let exchange_data := or(name_data, addr)
      sstore(exchange_ptr, exchange_data)

      mstore(return_value, exchange_count)
      exchange_count := add(exchange_count, 1)
      sstore(exchange_count_slot, exchange_count)

      log0(add(return_value, 24), 8)
    }
  }

  function get_exchange(uint64 id) public constant returns (string name, address addr, uint256 fee_balance) {
    uint256[5] memory return_value;

    assembly {
      let exchange_ptr := add(exchanges_slot, mul(id, /* EXCHANGE_SIZE */ 2))
      let exchange_data := sload(exchange_ptr)

      // Store name
      mstore(return_value, 96 /* address(20) + uint256(32) */)
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
      if gt(asset_count, 4294967295 /* 2^32 - 1 */) {
        stop()
      }

      /* Symbol must be 4 characters */
      let symbol_len := mload(symbol)
      if iszero(eq(symbol_len, 4)) {
        stop()
      }

      /* Unit scale must be non zero */
      if iszero(unit_scale) {
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

  function get_asset(uint32 asset_id) public constant
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

//  function jumpstart_user(address trade_address, uint32 exchange_id, uint32 session_id, uint64 expire_time,
//                          uint16 trade_asset_1, uint16 trade_asset_2, uint16 trade_asset_3, uint16 trade_asset_4) public payable {
//    uint32 user_id = uint32(user_count);
//    add_user(trade_address);
//    jumpstart_session(user_id, trade_address, exchange_id, session_id, expire_time, trade_asset_1, trade_asset_2, trade_asset_3, trade_asset_4);
//  }
//
//  function jumpstart_session(uint32 user_id, address trade_address, uint32 exchange_id, uint32 session_id, uint64 expire_time,
//                          uint16 trade_asset_1, uint16 trade_asset_2, uint16 trade_asset_3, uint16 trade_asset_4) public payable {
//    if (msg.value > 0) {
//      deposit_eth(user_id, true);
//    }
//
//    start_session(session_id, user_id, exchange_id, expire_time);
//
//    assembly {
//      let session_ptr := add(sessions_slot, mul(session_id, 34))
//
//      /* Scale ether down to 8 decimals and mask to 64 bit */
//      let ether_deposit := and(div(callvalue, 10000000000), 0xffffffffffffffff)
//
//      /* Set ether quantity */
//      {
//        let ether_ptr := add(session_ptr, 1)
//        /* quantity is zero because we just created the session so can or */
//        sstore(ether_ptr, BUILD_ETHER {
//          /* padding */ 0,
//          /* trade_address */ trade_address,
//          /* ether_balance */ ether_deposit
//        })
//      }
//
//      let asset_count := sload(asset_count_slot)
//
//      /* ASSET 1 */
//      {
//        /* Invalid trade asset */
//        if or(iszero(trade_asset_1), gt(trade_asset_1, asset_count)) {
//          stop()
//        }
//
//        /* Setup position */
//        let position_ptr := add(session_ptr, 2)
//        sstore(position_ptr, BUILD_POSITION {
//          trade_asset_1, 0, 0, 0, 0
//        })
//        sstore(add(position_ptr, 1), BUILD_LIMIT {
//          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
//        })
//      }
//
//      let session_data := sload(session_ptr)
//
//      /* ASSET 2 */
//      {
//        /* Invalid trade asset */
//        if or(iszero(trade_asset_2), gt(trade_asset_2, asset_count)) {
//          sstore(session_ptr, or(
//            and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
//            mul(1, 0x1000000000000000000000000000000000000000000000000)
//          ))
//          stop()
//        }
//
//        /* Setup position */
//        let position_ptr := add(session_ptr, 4)
//        sstore(position_ptr, BUILD_POSITION {
//          trade_asset_2, 0, 0, 0, 0
//        })
//        sstore(add(position_ptr, 1), BUILD_LIMIT {
//          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
//        })
//      }
//
//      /* ASSET 3 */
//      {
//        /* Invalid trade asset */
//        if or(iszero(trade_asset_3), gt(trade_asset_3, asset_count)) {
//          sstore(session_ptr, or(
//            and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
//            mul(2, 0x1000000000000000000000000000000000000000000000000)
//          ))
//          stop()
//        }
//
//        /* Setup position */
//        let position_ptr := add(session_ptr, 6)
//        sstore(position_ptr, BUILD_POSITION {
//          trade_asset_3, 0, 0, 0, 0
//        })
//        sstore(add(position_ptr, 1), BUILD_LIMIT {
//          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
//        })
//      }
//
//      /* ASSET 4 */
//      {
//        /* Invalid trade asset */
//        if or(iszero(trade_asset_4), gt(trade_asset_4, asset_count)) {
//          sstore(session_ptr, or(
//            and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
//            mul(3, 0x1000000000000000000000000000000000000000000000000)
//          ))
//          stop()
//        }
//
//        /* Setup position */
//        let position_ptr := add(session_ptr, 8)
//        sstore(position_ptr, BUILD_POSITION {
//          trade_asset_4, 0, 0, 0, 0
//        })
//        sstore(add(position_ptr, 1), BUILD_LIMIT {
//          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
//        })
//      }
//
//      sstore(session_ptr, or(
//        and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
//        mul(4, 0x1000000000000000000000000000000000000000000000000)
//      ))
//    }
//  }

  function deposit_asset(uint32 asset_id, uint256 quantity) public {
    uint256[1] memory exit_log;
    uint256[4] memory transfer_in;
    uint256[1] memory transfer_out;

    assembly {
      /* Validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if or(iszero(asset_id), gt(asset_id, asset_count)) {
          mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
        }
      }

      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE 2^32 */ 4294967296))

      mstore(transfer_in, /* transferFrom(address,address,uint256) */ 0x23b872dd00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), caller)
      mstore(add(transfer_in, 36), address)
      mstore(add(transfer_in, 68), quantity)

      let asset_data := sload(add(assets_slot, asset_id))
      let asset_address := ASSET(asset_data).address

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
        mstore(exit_log, 3) log0(add(exit_log, 31), 1) stop()
      }

      let result := mload(transfer_out)
      if iszero(result) {
        mstore(exit_log, 4) log0(add(exit_log, 31), 1) stop()
      }

      let asset_ptr := add(user_ptr, asset_id)
      let current_balance := sload(asset_ptr)

      sstore(asset_ptr, add(current_balance, quantity))
      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
    }
  }

  function deposit_eth() public payable {
    assembly {
      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE 2^32 */ 4294967296))

      /* First item is asset 0 (ether) */
      let current_balance := sload(user_ptr)
      sstore(user_ptr, add(current_balance, callvalue))
    }
  }

  function withdraw_asset(uint32 asset_id, address destination, uint256 quantity) {
    uint256[3] memory transfer_in;
    uint256[1] memory transfer_out;
    uint256[1] memory exit_log;

    assembly {
      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE 2^32 */ 4294967296))

      let asset_data := sload(add(assets_slot, asset_id))

      /* Ensure asset_id is valid */
      if or(iszero(asset_id), iszero(asset_data)) {
        mstore(exit_log, 3) log0(add(exit_log, 31), 1) stop()
      }

      let asset_ptr := add(user_ptr, asset_id)
      let current_balance := sload(asset_ptr)
      if lt(current_balance, quantity) {
        mstore(exit_log, 4) log0(add(exit_log, 31), 1) stop()
      }

      mstore(transfer_in, /* transfer(address,uint256) */ 0xa9059cbb00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), destination)
      mstore(add(transfer_in, 36), quantity)

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
        mstore(exit_log, 5) log0(add(exit_log, 31), 1) stop()
      }

      let result := mload(transfer_out)
      if iszero(result) {
        mstore(exit_log, 6) log0(add(exit_log, 31), 1) stop()
      }

      sstore(asset_ptr, sub(current_balance, quantity))
      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
    }
  }

  function withdraw_eth(address destination, uint256 quantity) public {
    uint256[1] memory empty_return;

    assembly {
      let ether_ptr := add(users_slot, mul(caller, /* USER_SIZE 2^32 */ 4294967296))

      /* Check balance */
      let ether_balance := sload(ether_ptr)
      if gt(quantity, ether_balance) {
        stop()
      }

      // Update balance
      sstore(ether_ptr, sub(ether_balance, quantity))

      // Send funds
      let result := call(
        /* do not forward any gas, use min for transfer */
        0,
        destination,
        quantity,
        empty_return,
        0,
        empty_return,
        0
      )

      if iszero(result) {
        revert(0, 0)
      }
    }
  }

  function get_user_balance(address user, uint32 asset_id) public constant returns (uint256 return_balance) {
    uint256[1] memory return_value;

    assembly {
      let user_ptr := add(users_slot, mul(user, /* USER_SIZE */ 4294967296))

      let asset_balance := sload(add(user_ptr, asset_id))
      mstore(return_value, asset_balance)
      return(return_value, 32)
    }
  }

  function start_session(uint64 session_id, uint64 exchange_id, uint64 expire_time) public {
    uint256[1] memory session_id_ptr;

    assembly {
      /* ensure: expire_time >= timestamp + 12 hours && expire_time <= timestamp + 30 days */
      if or(gt(add(timestamp, 43200), expire_time), gt(expire_time, add(timestamp, 2592000))) {
        stop()
      }

      let session_ptr := add(sessions_slot, mul(session_id, /* SESSION_SIZE 3*(1+2^32) */ 12884901891))
      let limit_data := sload(add(session_ptr, 1))

      /* verify session is empty */
      if SESSION_LIMIT(session_data).expire_time {
        stop()
      }

      /* authenticate user */
      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE 2^32 */ 4294967296))

      /* verify exchange id is valid */
      let exchange_count := sload(exchange_count_slot)
      if iszero(gt(exchange_count, exchange_id)) {
        stop()
      }

      session_data := BUILD_SESSION {
        /* turnover */ and(add(SESSION(session_data).turnover, 1), 0xfffffffffffffff),
        /* position_count */ 0,
        /* user_id */ user_id,
        /* exchange_id */ exchange_id,
        /* max_ether_fees */ 0,
        /* expire_time */ expire_time
      }

      sstore(session_ptr, session_data)
      sstore(add(session_ptr, 1), BUILD_ETHER {
        /* padding */ 0,
        /* trade_address */ sload(add(user_ptr, 1)),
        /* ether_balance */ 0
      })

      mstore(session_id_ptr, session_id)
      log1(session_id_ptr, 32,
           /* SessionStarted(uint256) */
           0xc3934e844399df6122666c45922384445cb616ed1402ecf7d2e39bd2529a2746
      )
    }
  }

  function position_deposit(uint32 session_id, uint8 position_id, uint64 quantity) {
    uint256[1] memory exit_log;
    uint256[1] memory session_id_ptr;
    uint256[4] memory session_deposit_ptr;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      /* Must expire in the future */
      if gt(timestamp, SESSION(session_data).expire_time) {
        mstore(exit_log, 2) log0(add(exit_log, 31), 1) stop()
      }

      let user_ptr := add(users_slot, mul(SESSION(session_data).user_id, 65539))

      /* Make sure caller is the manager */
      {
        let manage_address := sload(user_ptr)
        if iszero(eq(manage_address, caller)) {
          mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
        }
      }

      let user_asset_ptr := add(add(user_ptr, 2), asset_id)
      let user_balance := sload(user_asset_ptr)

      /* Decrement user_balance */
      {
        let debit := quantity

        /* Convert from trading units to accounting units */
        {
          let asset_data := sload(add(assets_slot, asset_id))
          let unit_scale := ASSET(asset_data).unit_scale
          debit := mul(debit, unit_scale)
        }

        /* Ensure user has enough balance for deposit */
        if gt(debit, user_balance) {
          mstore(exit_log, 4) log0(add(exit_log, 31), 1) stop()
        }

        user_balance := sub(user_balance, debit)
      }

      /* Prepare log */
      mstore(session_deposit_ptr, session_id)
      mstore(add(session_deposit_ptr, 32), SESSION(session_data).turnover)
      mstore(add(session_deposit_ptr, 64), position_id)
      mstore(add(session_deposit_ptr, 96), quantity)

      /*
       * Process
       */

      // deposit ETH
      if iszero(position_id) {
        // Asset id must be 0 (ETH)
        if asset_id {
          mstore(exit_log, 5) log0(add(exit_log, 31), 1) stop()
        }

        let ether_ptr := add(session_ptr, 1)
        let ether_data := sload(ether_ptr)
        let current_balance := ETHER(ether_data).balance

        current_balance := add(current_balance, quantity)

        // Protect against overflow
        if gt(current_balance, /* 2^64 - 1 */ 0xffffffffffffffff) {
          mstore(exit_log, 6) log0(add(exit_log, 31), 1) stop()
        }

        sstore(user_asset_ptr, user_balance)
        sstore(ether_ptr, or(and(ether_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000), current_balance))

        log1(session_deposit_ptr, 128, /* PositionDeposit */ 0x11366adca5bb057478533eee49fcecd18156fffc05023ffa66625ac00ad488bb)
        mstore(exit_log, 0) log0(add(exit_log, 31), 1) stop()
      }

      let next_position_count := add(SESSION(session_data).position_count, 1)

      // Cannot deposit into a position that doesn't exist
      if or(gt(position_id, next_position_count), gt(position_id, 16)) {
        mstore(exit_log, 7) log0(add(exit_log, 31), 1) stop()
      }

      let position_ptr := add(session_ptr, mul(position_id, 2))

      // Add position
      if eq(position_id, next_position_count) {
        sstore(user_asset_ptr, user_balance)

        // TODO: test that this increases
        // Increment position_count
        sstore(session_ptr, or(
          and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
          mul(next_position_count, 0x1000000000000000000000000000000000000000000000000)
        ))

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

        mstore(session_id_ptr, session_id)
        log1(session_id_ptr, 32,
             /* PositionAdded(uint256) */
             0x3d0a2b1c6f8e72f333688e33b7fc1767f042d33eaef1d3b5db5968567033e91f
        )

        log1(session_deposit_ptr, 128, /* PositionDeposit */ 0x11366adca5bb057478533eee49fcecd18156fffc05023ffa66625ac00ad488bb)
        mstore(exit_log, 0) log0(add(exit_log, 31), 1) stop()
      }

      let position_data := sload(position_ptr)
      let current_balance := POSITION(position_data).asset_balance
      current_balance := add(current_balance, quantity)

      // Protect against overflow
      if gt(current_balance, /* 2^64 - 1 */ 0xffffffffffffffff) {
        mstore(exit_log, 8) log0(add(exit_log, 31), 1) stop()
      }

      position_data := and(position_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
      position_data := or(position_data, current_balance)

      sstore(user_asset_ptr, user_balance)
      sstore(position_ptr, position_data)

      log1(session_deposit_ptr, 128, 0x11366adca5bb057478533eee49fcecd18156fffc05023ffa66625ac00ad488bb)
      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
    }
  }

  function get_session(uint32 session_id) public constant
  returns (uint256 turnover, uint256 position_count, uint256 user_id, uint256 exchange_id, uint256 max_ether_fees, uint256 expire_time, address trade_address, uint256 ether_balance) {
    uint256[8] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      mstore(return_values, SESSION(session_data).turnover)
      mstore(add(return_values, 32), SESSION(session_data).position_count)
      mstore(add(return_values, 64), SESSION(session_data).user_id)
      mstore(add(return_values, 96), SESSION(session_data).exchange_id)
      mstore(add(return_values, 128), SESSION(session_data).max_ether_fees)
      mstore(add(return_values, 160), SESSION(session_data).expire_time)

      let ether_data := sload(add(session_ptr, 1))
      mstore(add(return_values, 192), ETHER(ether_data).trade_address)
      mstore(add(return_values, 224), ETHER(ether_data).balance)

      return(return_values, 256)
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
  returns (uint256 version, uint256 min_asset_qty, uint256 min_ether_qty, uint256 long_price, uint256 short_price, uint256 ether_shift, uint256 asset_shift) {
    uint256[7] memory return_value;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      if or(lt(position_id, 1), gt(position_id, 16)) {
        stop()
      }

      let position_ptr := add(session_ptr, mul(position_id, 2))
      let limit_data := sload(add(position_ptr, 1))

      mstore(return_value, LIMIT(limit_data).version)

      let quant := mul(LIMIT(limit_data).min_asset_qty, exp(10, LIMIT(limit_data).min_asset_qty_pow))
      mstore(add(return_value, 32), quant)

      quant := mul(LIMIT(limit_data).min_ether_qty, exp(10, LIMIT(limit_data).min_ether_qty_pow))
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

  function end_session(uint32 session_id) public {
    uint256[1] memory exit_log;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      // Check that caller is the exchange
      {
        let exchange_id := SESSION(session_data).exchange_id
        let exchange_ptr := add(exchanges_slot, mul(exchange_id, 2))
        let exchange_data := sload(exchange_ptr)

        if iszero(eq(EXCHANGE(exchange_data).address, caller)) {
          mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
        }
      }

      session_data := and(session_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
      session_data := or(session_data, and(timestamp, 0xffffffffffffffff))
      sstore(session_ptr, session_data)
      mstore(exit_log, 0) log0(add(exit_log, 31), 1) stop()
    }
  }

  function close_session(uint32 session_id) public {
    uint256[1] memory exit_log;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      // Cannot process if session is still active or closed
      let session_expire_time := SESSION(session_data).expire_time
      if or(gt(session_expire_time, timestamp), iszero(session_expire_time)) {
        mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
      }

      // Reset session
      sstore(session_ptr, BUILD_SESSION {
        SESSION(session_data).turnover,
        0, 0, 0, 0, 0
      })

      let user_ptr := add(users_slot, mul(SESSION(session_data).user_id, 65539))
      let user_assets_ptr := add(user_ptr, 2)

      // Update user's ether balance
      let ether_balance := sload(user_assets_ptr)
      let session_ether_data := sload(add(session_data, 1))
      ether_balance := add(ether_balance, mul(ETHER(session_ether_data).balance, 10000000000))
      sstore(user_assets_ptr, ether_balance)

      let position_count := SESSION(session_data).position_count
      let positions_ptr := add(session_ptr, 2)

      // Iterate through all positions and move balance to user
      for { let i := 0 } lt(i, position_count) { i := add(i, 1) } {
        let position_ptr := add(positions_ptr, mul(i, 2))
        let position_data := sload(position_ptr)

        // Update user balance with asset_balance in position
        let asset_id := POSITION(position_data).asset_id
        let asset_data := sload(add(assets_slot, asset_id))
        let asset_unit_scale := ASSET(asset_data).unit_scale
        let user_asset_ptr := add(user_assets_ptr, asset_id)

        let asset_balance := mul(POSITION(position_data).asset_balance, asset_unit_scale)
        sstore(user_asset_ptr, add(sload(user_asset_ptr), asset_balance))
      }

      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
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
          let amount := mul(quantity, 10000000000)
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
