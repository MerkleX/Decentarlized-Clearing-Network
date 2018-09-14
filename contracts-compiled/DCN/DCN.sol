pragma solidity ^0.4.24;

contract DCN {
  event SessionStarted(uint256 session_id);
  event PositionAdded(uint256 session_id);

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
    turn_over         :  60,
    position_count    :   4,
    user_id           :  32,
    exchange_id       :  32,
    max_ether_fees    :  64,
    expire_time       :  64,
   }

   ETHER_DEF {
    _                 :  32,
    trade_address     : 160,
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
          sstore(assets_slot, or(mul(/* symbol */ /* "ETH " */ 0x45544820, 0x100000000000000000000000000000000000000000000000000000000), mul(/* unit_scale */ 10000000000, 0x10000000000000000000000000000000000000000)))
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
      let exchange_ptr := add(exchanges_slot, mul(id, 2))
      let exchange_data := sload(exchange_ptr)

      // Store name
      mstore(return_value, 96 /* address 20 + uint256 32 */)
      mstore(add(return_value, 96), 12)
      mstore(add(return_value, 128), exchange_data)

      // Store addr
      mstore(add(return_value, 32), /* EXCHANGE(exchange_data).address */ and(div(exchange_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff))

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
      let data := or(asset_symbol, or(mul(/* unit_scale */ unit_scale, 0x10000000000000000000000000000000000000000), contract_address))
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

      mstore(add(return_value, 32), /* ASSET(data).unit_scale */ and(div(data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_value, 64), /* ASSET(data).address */ and(div(data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff))

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

  function jumpstart_user(address trade_address, uint32 exchange_id, uint32 session_id, uint64 expire_time,
                          uint16 trade_asset_1, uint16 trade_asset_2, uint16 trade_asset_3, uint16 trade_asset_4) public payable {
    uint32 user_id = uint32(user_count);
    add_user(trade_address);
    if (msg.value > 0) {
      deposit_eth(user_id, true);
    }
    jumpstart_session(user_id, exchange_id, session_id, expire_time, trade_asset_1, trade_asset_2, trade_asset_3, trade_asset_4);
  }

  function jumpstart_session(uint32 user_id, uint32 exchange_id, uint32 session_id, uint64 expire_time,
                          uint16 trade_asset_1, uint16 trade_asset_2, uint16 trade_asset_3, uint16 trade_asset_4) public {
    start_session(session_id, user_id, exchange_id, expire_time);

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))

      /* Scale ether down to 8 decimals and mask to 64 bit */
      let ether_deposit := and(div(callvalue, 10000000000), 0xffffffffffffffff)

      /* Set ether quantity */
      {
        let ether_ptr := add(session_ptr, 1)
        /* quantity is zero because we just created the session so can or */
        sstore(ether_ptr, or(sload(ether_ptr), ether_deposit))
      }

      let asset_count := sload(asset_count_slot)

      /* ASSET 1 */
      {
        /* Invalid trade asset */
        if or(iszero(trade_asset_1), gt(trade_asset_1, asset_count)) {
          stop()
        }

        /* Setup position */
        let position_ptr := add(session_ptr, 2)
        sstore(position_ptr, mul(/* asset_id */ trade_asset_1, 0x1000000000000000000000000000000000000000000000000000000000000))
        sstore(add(position_ptr, 1), mul(/* version */ 1, 0x100000000000000000000000000000000000000000000000000000000))
      }

      let session_data := sload(session_ptr)

      /* ASSET 2 */
      {
        /* Invalid trade asset */
        if or(iszero(trade_asset_2), gt(trade_asset_2, asset_count)) {
          sstore(session_ptr, or(
            and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
            mul(1, 0x1000000000000000000000000000000000000000000000000)
          ))
          stop()
        }

        /* Setup position */
        let position_ptr := add(session_ptr, 4)
        sstore(position_ptr, mul(/* asset_id */ trade_asset_2, 0x1000000000000000000000000000000000000000000000000000000000000))
        sstore(add(position_ptr, 1), mul(/* version */ 1, 0x100000000000000000000000000000000000000000000000000000000))
      }

      /* ASSET 3 */
      {
        /* Invalid trade asset */
        if or(iszero(trade_asset_3), gt(trade_asset_3, asset_count)) {
          sstore(session_ptr, or(
            and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
            mul(2, 0x1000000000000000000000000000000000000000000000000)
          ))
          stop()
        }

        /* Setup position */
        let position_ptr := add(session_ptr, 6)
        sstore(position_ptr, mul(/* asset_id */ trade_asset_3, 0x1000000000000000000000000000000000000000000000000000000000000))
        sstore(add(position_ptr, 1), mul(/* version */ 1, 0x100000000000000000000000000000000000000000000000000000000))
      }

      /* ASSET 4 */
      {
        /* Invalid trade asset */
        if or(iszero(trade_asset_4), gt(trade_asset_4, asset_count)) {
          sstore(session_ptr, or(
            and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
            mul(3, 0x1000000000000000000000000000000000000000000000000)
          ))
          stop()
        }

        /* Setup position */
        let position_ptr := add(session_ptr, 8)
        sstore(position_ptr, mul(/* asset_id */ trade_asset_4, 0x1000000000000000000000000000000000000000000000000000000000000))
        sstore(add(position_ptr, 1), mul(/* version */ 1, 0x100000000000000000000000000000000000000000000000000000000))
      }

      sstore(session_ptr, or(
        and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
        mul(4, 0x1000000000000000000000000000000000000000000000000)
      ))
    }
  }

  function add_user(address trade_address) public {
    uint256[1] memory return_value;

    assembly {
      // Do not overflow users
      let user_count := sload(user_count_slot)
      if gt(user_count, 4294967295 /* 2^32 - 1 */) {
        stop()
      }

      // Store data
      let user_ptr := add(users_slot, mul(user_count, 65539))
      sstore(user_ptr, caller)
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

  function update_user_trade_addresses(uint32 user_id, address trade_address) public {
    uint256[1] memory exit_log;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      let manage_address := sload(user_ptr)
      if iszero(eq(manage_address, caller)) {
        mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
      }

      sstore(add(user_ptr, 1), trade_address)
      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
    }
  }

  function deposit_asset(uint32 user_id, uint16 asset_id, uint256 quantity) public {
    uint256[1] memory exit_log;
    uint256[4] memory transfer_in;
    uint256[1] memory transfer_out;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      {
        let manage_address := sload(user_ptr)
        if iszero(eq(manage_address, caller)) {
          mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
        }
      }

      let asset_data := sload(add(assets_slot, asset_id))
      if or(iszero(asset_id), iszero(asset_data)) {
        mstore(exit_log, 2) log0(add(exit_log, 31), 1) stop()
      }

      mstore(transfer_in, /* transferFrom(address,address,uint256) */ 0x23b872dd00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), caller)
      mstore(add(transfer_in, 36), address)
      mstore(add(transfer_in, 68), quantity)

      let asset_address := /* ASSET(asset_data).address */ and(div(asset_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff)

      let success := call(
        gas,
        asset_address,
        /* don't send any ether */ 0,
        transfer_in,
        100,
        transfer_out,
        32
      )

      if iszero(success) {
        mstore(exit_log, 3) log0(add(exit_log, 31), 1) stop()
      }

      let result := mload(transfer_out)
      if iszero(result) {
        mstore(exit_log, 4) log0(add(exit_log, 31), 1) stop()
      }

      let asset_ptr := add(add(user_ptr, 2), asset_id)
      let current_balance := sload(asset_ptr)

      sstore(asset_ptr, add(current_balance, quantity))
      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
    }
  }

  function withdraw_asset(uint32 user_id, bool check_self, uint16 asset_id, address destination, uint256 quantity) {
    uint256[3] memory transfer_in;
    uint256[1] memory transfer_out;
    uint256[1] memory exit_log;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      // Authenticate user
      {
        let manage_address := sload(user_ptr)
        if iszero(eq(manage_address, caller)) {
          mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
        }

        if and(check_self, iszero(eq(manage_address, destination))) {
          mstore(exit_log, 2) log0(add(exit_log, 31), 1) stop()
        }
      }

      // Ensure asset_id is valid
      let asset_data := sload(add(assets_slot, asset_id))
      if or(iszero(asset_id), iszero(asset_data)) {
        mstore(exit_log, 3) log0(add(exit_log, 31), 1) stop()
      }

      let asset_ptr := add(add(user_ptr, 2), asset_id)
      let current_balance := sload(asset_ptr)
      if lt(current_balance, quantity) {
        mstore(exit_log, 4) log0(add(exit_log, 31), 1) stop()
      }

      mstore(transfer_in, /* transfer(address,uint256) */ 0xa9059cbb00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), destination)
      mstore(add(transfer_in, 36), quantity)

      let asset_address := /* ASSET(asset_data).address */ and(div(asset_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff)

      let success := call(
        gas,
        asset_address,
        /* don't send any ether */ 0,
        transfer_in,
        68,
        transfer_out,
        32
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

  function deposit_eth(uint32 user_id, bool check_self) public payable {
    uint256[1] memory exit_log;

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

  function withdraw_eth(uint32 user_id, address destination, bool check_self, uint256 quantity) public {
    uint256[1] memory empty_return;

    assembly {
      let user_ptr := add(users_slot, mul(user_id, 65539))

      // Authenticate caller
      let manage_address := sload(user_ptr)
      if iszero(eq(manage_address, caller)) {
        stop()
      }

      // Do we want to withdraw to self
      if and(check_self, iszero(eq(manage_address, destination))) {
        stop()
      }

      // Do we have enough balance
      let ether_ptr := add(user_ptr, 2)
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

  function get_user_balance(uint32 user_id, uint16 asset_id) public constant returns (uint256 return_balance) {
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

    uint256[1] memory session_id_ptr;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      /* ensure: expire_time >= timestamp + 12 hours && expire_time <= timestamp + 30 days */
      if or(gt(add(timestamp, 43200), expire_time), gt(expire_time, add(timestamp, 2592000))) {
        stop()
      }

      /* Verify session is empty */
      if /* SESSION(session_data).expire_time */ and(div(session_data, 0x1), 0xffffffffffffffff) {
        stop()
      }

      /* Authenticate user */
      let user_ptr := add(users_slot, mul(user_id, 65539))
      let user_manage_address := sload(user_ptr)
      if iszero(eq(user_manage_address, caller)) {
        stop()
      }

      // Verify exchange id is valid

      let exchange_count := sload(exchange_count_slot)
      if iszero(gt(exchange_count, exchange_id)) {
        stop()
      }

      session_data := or(mul(/* turn_over */ /* turn_over */ and(add(/* SESSION(session_data).turn_over */ and(div(session_data, 0x10000000000000000000000000000000000000000000000000), 0xfffffffffffffff), 1), 0xfffffffffffffff), 0x10000000000000000000000000000000000000000000000000), or(mul(/* position_count */ /* position_count */ 0, 0x1000000000000000000000000000000000000000000000000), or(mul(/* user_id */ /* user_id */ user_id, 0x10000000000000000000000000000000000000000), or(mul(/* exchange_id */ /* exchange_id */ exchange_id, 0x100000000000000000000000000000000), or(mul(/* max_ether_fees */ /* max_ether_fees */ 0, 0x10000000000000000), /* expire_time */ expire_time)))))

      sstore(session_ptr, session_data)
      sstore(add(session_ptr, 1), or(mul(/* _ */ /* padding */ 0, 0x100000000000000000000000000000000000000000000000000000000), or(mul(/* trade_address */ /* trade_address */ sload(add(user_ptr, 1)), 0x10000000000000000), /* ether_balance */ 0)))

      mstore(session_id_ptr, session_id)
      log1(session_id_ptr, 32,
           /* SessionStarted(uint256) */
           0xc3934e844399df6122666c45922384445cb616ed1402ecf7d2e39bd2529a2746
      )
    }
  }

  function position_deposit(uint32 session_id, uint32 user_id, uint16 asset_id, uint8 position_id, uint64 quantity) {
    uint256[1] memory exit_log;
    uint256[1] memory session_id_ptr;

    assembly {
      /*
       * Validate Input
       */

      {
        let asset_count := sload(asset_count_slot)
        if gt(asset_id, asset_count) {
          stop()
        }
      }

      let user_ptr := add(users_slot, mul(user_id, 65539))

      {
        // Authenticate user
        let manage_address := sload(user_ptr)
        if iszero(eq(manage_address, caller)) {
          mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
        }
      }

      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      // Ensure session is valid
      if gt(timestamp, /* SESSION(session_data).expire_time */ and(div(session_data, 0x1), 0xffffffffffffffff)) {
        mstore(exit_log, 2) log0(add(exit_log, 31), 1) stop()
      }

      // Autenticate session
      if iszero(eq(/* SESSION(session_data).user_id */ and(div(session_data, 0x10000000000000000000000000000000000000000), 0xffffffff), user_id)) {
        mstore(exit_log, 3) log0(add(exit_log, 31), 1) stop()
      }

      let user_asset_ptr := add(add(user_ptr, 2), asset_id)
      let user_balance := sload(user_asset_ptr)

      let amount := quantity
      {
        let asset_data := sload(add(assets_slot, asset_id))
        let unit_scale := /* ASSET(asset_data).unit_scale */ and(div(asset_data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff)
        amount := mul(amount, unit_scale)
      }

      // Ensure use has enough balance for deposit
      if gt(amount, user_balance) {
        mstore(exit_log, 4) log0(add(exit_log, 31), 1) stop()
      }

      let new_user_balance := sub(user_balance, amount)

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
        let current_balance := /* ETHER(ether_data).balance */ and(div(ether_data, 0x1), 0xffffffffffffffff)

        current_balance := add(current_balance, quantity)

        // Protect against overflow
        if gt(current_balance, /* 2^64 - 1 */ 0xffffffffffffffff) {
          mstore(exit_log, 6) log0(add(exit_log, 31), 1) stop()
        }

        sstore(user_asset_ptr, new_user_balance)
        sstore(ether_ptr, or(and(ether_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000), current_balance))

        mstore(exit_log, 0) log0(add(exit_log, 31), 1) stop()
      }

      let next_position_count := add(/* SESSION(session_data).position_count */ and(div(session_data, 0x1000000000000000000000000000000000000000000000000), 0xf), 1)

      // Cannot deposit into a position that doesn't exist
      if or(gt(position_id, next_position_count), gt(position_id, 16)) {
        mstore(exit_log, 7) log0(add(exit_log, 31), 1) stop()
      }

      let position_ptr := add(session_ptr, mul(position_id, 2))

      // Add position
      if eq(position_id, next_position_count) {
        sstore(user_asset_ptr, new_user_balance)

        // TODO: test that this increases
        // Increment position_count
        sstore(session_ptr, or(
          and(session_data, 0xfffffffffffffff0ffffffffffffffffffffffffffffffffffffffffffffffff),
          mul(next_position_count, 0x1000000000000000000000000000000000000000000000000)
        ))

        sstore(position_ptr, or(mul(/* asset_id */ asset_id, 0x1000000000000000000000000000000000000000000000000000000000000), and(quantity, 0xffffffffffffffff)))

        sstore(add(position_ptr, 1), mul(/* version */ 1, 0x100000000000000000000000000000000000000000000000000000000))

        mstore(session_id_ptr, session_id)
        log1(session_id_ptr, 32,
             /* PositionAdded(uint256) */
             0x3d0a2b1c6f8e72f333688e33b7fc1767f042d33eaef1d3b5db5968567033e91f
        )

        mstore(exit_log, 0) log0(add(exit_log, 31), 1) stop()
      }

      let position_data := sload(position_ptr)
      let current_balance := /* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff)
      current_balance := add(current_balance, quantity)

      // Protect against overflow
      if gt(current_balance, /* 2^64 - 1 */ 0xffffffffffffffff) {
        mstore(exit_log, 8) log0(add(exit_log, 31), 1) stop()
      }

      position_data := and(position_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
      position_data := or(position_data, current_balance)

      sstore(user_asset_ptr, new_user_balance)
      sstore(position_ptr, position_data)

      mstore(exit_log, 0) log0(add(exit_log, 31), 1)
    }
  }

  function get_session(uint32 session_id) public constant
  returns (uint256 turn_over, uint256 position_count, uint256 user_id, uint256 exchange_id, uint256 max_ether_fees, uint256 expire_time, address trade_address, uint256 ether_balance) {
    uint256[8] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(session_id, 34))
      let session_data := sload(session_ptr)

      mstore(return_values, /* SESSION(session_data).turn_over */ and(div(session_data, 0x10000000000000000000000000000000000000000000000000), 0xfffffffffffffff))
      mstore(add(return_values, 32), /* SESSION(session_data).position_count */ and(div(session_data, 0x1000000000000000000000000000000000000000000000000), 0xf))
      mstore(add(return_values, 64), /* SESSION(session_data).user_id */ and(div(session_data, 0x10000000000000000000000000000000000000000), 0xffffffff))
      mstore(add(return_values, 96), /* SESSION(session_data).exchange_id */ and(div(session_data, 0x100000000000000000000000000000000), 0xffffffff))
      mstore(add(return_values, 128), /* SESSION(session_data).max_ether_fees */ and(div(session_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 160), /* SESSION(session_data).expire_time */ and(div(session_data, 0x1), 0xffffffffffffffff))

      let ether_data := sload(add(session_ptr, 1))
      mstore(add(return_values, 192), /* ETHER(ether_data).trade_address */ and(div(ether_data, 0x10000000000000000), 0xffffffffffffffffffffffffffffffffffffffff))
      mstore(add(return_values, 224), /* ETHER(ether_data).balance */ and(div(ether_data, 0x1), 0xffffffffffffffff))

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

      mstore(return_value, /* POSITION(position_data).asset_id */ and(div(position_data, 0x1000000000000000000000000000000000000000000000000000000000000), 0xffff))
      mstore(add(return_value, 32), /* POSITION(position_data).ether_qty */ and(div(position_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_value, 64), /* POSITION(position_data).asset_qty */ and(div(position_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_value, 96), /* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff))

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

      mstore(return_value, /* LIMIT(limit_data).version */ and(div(limit_data, 0x100000000000000000000000000000000000000000000000000000000), 0xffffffff))

      let quant := mul(/* LIMIT(limit_data).long_asset_qty */ and(div(limit_data, 0x1000000000000000000000000000000000000000000000000000), 0xfffff), exp(10, /* LIMIT(limit_data).long_asset_qty_pow */ and(div(limit_data, 0x100000000000000000000000000000000000000000000000000), 0xf)))
      mstore(add(return_value, 32), quant)

      quant := mul(/* LIMIT(limit_data).short_asset_qty */ and(div(limit_data, 0x1000000000000000000000000000000000000000000000), 0xfffff), exp(10, /* LIMIT(limit_data).short_asset_qty_pow */ and(div(limit_data, 0x100000000000000000000000000000000000000000000), 0xf)))
      mstore(add(return_value, 64), quant)

      quant := mul(/* LIMIT(limit_data).long_price */ and(div(limit_data, 0x1000000000000000000000000000000000000000), 0xfffff), exp(10, /* LIMIT(limit_data).long_price_pow */ and(div(limit_data, 0x100000000000000000000000000000000000000), 0xf)))
      mstore(add(return_value, 96), quant)

      quant := mul(/* LIMIT(limit_data).short_price */ and(div(limit_data, 0x1000000000000000000000000000000000), 0xfffff), exp(10, /* LIMIT(limit_data).short_price_pow */ and(div(limit_data, 0x100000000000000000000000000000000), 0xf)))
      mstore(add(return_value, 128), quant)

      mstore(add(return_value, 160), /* LIMIT(limit_data).ether_shift */ and(div(limit_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_value, 192), /* LIMIT(limit_data).asset_shift */ and(div(limit_data, 0x1), 0xffffffffffffffff))

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
        let exchange_id := /* SESSION(session_data).exchange_id */ and(div(session_data, 0x100000000000000000000000000000000), 0xffffffff)
        let exchange_ptr := add(exchanges_slot, mul(exchange_id, 2))
        let exchange_data := sload(exchange_ptr)

        if iszero(eq(/* EXCHANGE(exchange_data).address */ and(div(exchange_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff), caller)) {
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
      let session_expire_time := /* SESSION(session_data).expire_time */ and(div(session_data, 0x1), 0xffffffffffffffff)
      if or(gt(session_expire_time, timestamp), iszero(session_expire_time)) {
        mstore(exit_log, 1) log0(add(exit_log, 31), 1) stop()
      }

      // Reset session
      sstore(session_ptr, mul(/* turn_over */ /* SESSION(session_data).turn_over */ and(div(session_data, 0x10000000000000000000000000000000000000000000000000), 0xfffffffffffffff), 0x10000000000000000000000000000000000000000000000000))

      let user_ptr := add(users_slot, mul(/* SESSION(session_data).user_id */ and(div(session_data, 0x10000000000000000000000000000000000000000), 0xffffffff), 65539))
      let user_assets_ptr := add(user_ptr, 2)

      // Update user's ether balance
      let ether_balance := sload(user_assets_ptr)
      let session_ether_data := sload(add(session_data, 1))
      ether_balance := add(ether_balance, mul(/* ETHER(session_ether_data).balance */ and(div(session_ether_data, 0x1), 0xffffffffffffffff), 10000000000))
      sstore(user_assets_ptr, ether_balance)

      let position_count := /* SESSION(session_data).position_count */ and(div(session_data, 0x1000000000000000000000000000000000000000000000000), 0xf)
      let positions_ptr := add(session_ptr, 2)

      // Iterate through all positions and move balance to user
      for { let i := 0 } lt(i, position_count) { i := add(i, 1) } {
        let position_ptr := add(positions_ptr, mul(i, 2))
        let position_data := sload(position_ptr)

        // Update user balance with asset_balance in position
        let asset_id := /* POSITION(position_data).asset_id */ and(div(position_data, 0x1000000000000000000000000000000000000000000000000000000000000), 0xffff)
        let asset_data := sload(add(assets_slot, asset_id))
        let asset_unit_scale := /* ASSET(asset_data).unit_scale */ and(div(asset_data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff)
        let user_asset_ptr := add(user_assets_ptr, asset_id)

        let asset_balance := mul(/* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff), asset_unit_scale)
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

        let session_id := /* WITHDRAW(data).session_id */ and(div(data, 0x100000000000000000000000000000000000000000000000000000000), 0xffffffff)
        let position_id := /* WITHDRAW(data).position_id */ and(div(data, 0x1000000000000000000000000000000000000000000000000000000), 0xf)
        let quantity := /* WITHDRAW(data).quantity */ and(div(data, 0x100000000000000000000000000000000000000), 0xffffffffffffffff)

        let session_ptr := add(sessions_slot, mul(session_id, 34))
        let session_data := sload(session_ptr)

        let user_ptr := add(users_slot, mul(/* SESSION(session_data).user_id */ and(div(session_data, 0x10000000000000000000000000000000000000000), 0xffffffff), 65539))

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
          if gt(position_id, /* SESSION(session_data).position_count */ and(div(session_data, 0x1000000000000000000000000000000000000000000000000), 0xf)) {
            revert(0, 0)
          }

          let position_ptr := add(session_ptr, mul(position_id, 2))
          let position_data := sload(position_ptr)
          let position_balance := /* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff)

          if gt(quantity, position_balance) {
            revert(0, 0)
          }

          // Decrement balance from position
          position_balance := sub(position_balance, quantity)
          position_data := and(position_data, 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          position_data := or(position_data, position_balance)
          sstore(position_ptr, position_data)

          let asset_id := /* POSITION(position_data).asset_id */ and(div(position_data, 0x1000000000000000000000000000000000000000000000000000000000000), 0xffff)
          let asset_data := sload(add(assets_slot, asset_id))
          let amount := mul(quantity, /* ASSET(asset_data).unit_scale */ and(div(asset_data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff))

          let user_asset_ptr := add(add(user_ptr, 2), asset_id)
          sstore(user_asset_ptr, add(sload(user_asset_ptr), amount))
        }
      }
    }
  }
}

