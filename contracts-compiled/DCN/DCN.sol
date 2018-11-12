pragma solidity ^0.4.24;

contract DCN {
  event SessionUpdated(address user, uint64 exchange_id);
  event PositionUpdated(address user, uint64 exchange_id, uint32 asset_id); 

  uint256 creator;

  uint256 exchange_count;
  uint256 asset_count;

  /* Memory Layout */

  /*
     #define EXCHANGE_COUNT  4294967296
     #define EXCHANGE_SIZE   2

     EXCHANGE_DEF {
      name              :  96,
      address           : 160,
     }

     Layout
      0: EXCHANGE_DEF
      1: fee balance
  */
  uint256[/* EXCHANGE_SIZE */ 2 * /* EXCHANGE_COUNT */ 4294967296]  exchanges;


  /*
     #define ASSET_COUNT 4294967296

     ASSET_DEF {
      symbol            :  32,
      unit_scale        :  64,
      address           : 160,
     }
  */
  uint256[/* ASSET_COUNT */ 4294967296]  assets;

  /*
     #define USER_COUNT 1461501637330902918203684832716283019655932542976
     #define USER_SIZE  4294967296
     USER {
                  0: ether_balance
                  1: asset_1_balance
                  n: asset_n_balance
         4294967295: asset_4294967295_balance
     }
  */
  uint256[/* USER_SIZE */ 4294967296 * /* USER_COUNT */ 1461501637330902918203684832716283019655932542976] users;


  /*
     #define SESSION_COUNT 6277101735386680763835789423207666416102355444464034512896
     #define SESSION_SIZE  12884901888

     size = 3 * asset_count
     count = user_count * exchange_count

     sessions[address][exchange_id] = session
     session_ptr = (address * exchange_count + exchange_id) * session_size
  */
  uint256[/* SESSION_SIZE */ 12884901888 * /* SESSION_COUNT */ 6277101735386680763835789423207666416102355444464034512896]  sessions;

  /*
    SESSION {
               0: ether_position
               1: version + expire_time
               2: padding
             
               3: ASSET_1_POSITION_DEF
               4: ASSET_1_POS_LIMIT_DEF
               5: ASSET_1_PRICE_LIMIT_DEF
             
               6: ASSET_2_POSITION_DEF
               7: ASSET_2_POS_LIMIT_DEF
               8: ASSET_2_PRICE_LIMIT_DEF

             n*3: ASSET_N_POSITION_DEF
           n*3+1: ASSET_N_POS_LIMIT_DEF
           n*3+2: ASSET_N_PRICE_LIMIT_DEF
    
      12884901885: ASSET_4294967295_POSITION_DEF
      12884901886: ASSET_4294967295_PRICE_LIMIT_DEF
      12884901887: ASSET_4294967295_POS_LIMIT_DEF
    }

    ETHER_POSITION_DEF {
      fee_limit         :  64,
      fee_used          :  64,
      total_deposit     :  64,
      ether_balance     :  64,
    }

    TIME_DEF {
      padding           : 128,
      version           :  64,
      expire_time       :  64,
    }

    POSITION_DEF {
      ether_qty         :  64,
      asset_qty         :  64,
      total_deposit     :  64,
      asset_balance     :  64,
    }

    POS_LIMIT_DEF {
      min_ether         :  64,
      min_asset         :  64,
      ether_shift       :  64,
      asset_shift       :  64,
    }

    PRICE_LIMIT_DEF {
      padding           :  64,
      limit_version     :  64,
      long_max_price    :  64,
      short_min_price   :  64,
    }
  */

  constructor() public {
      assembly {
          sstore(creator_slot, caller)
          sstore(assets_slot, or(mul(/* symbol */ /* "ETH " */ 0x45544820, 0x100000000000000000000000000000000000000000000000000000000), mul(/* unit_scale */ 10000000000, 0x10000000000000000000000000000000000000000)))
      }
  }

  /* View functions */

  function get_creator() public view returns (address dcn_creator) {
    return address(creator);
  }

  function get_asset(uint32 asset_id) public view
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

  function get_exchange(uint32 id) public view returns (string name, address addr, uint64 fee_balance) {
    uint256[5] memory return_value;

    assembly {
      let exchange_ptr := add(exchanges_slot, mul(id, /* EXCHANGE_SIZE */ 2))
      let exchange_data := sload(exchange_ptr)

      // Store name
      mstore(return_value, 96 /* address(20) + uint256(32) */)
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

  function get_exchange_count() public view returns (uint32 count) {
    uint256[1] memory return_value;

    assembly {
      let data := sload(exchange_count_slot)
      mstore(return_value, data)
      return(return_value, 32)
    }
  }

  function get_asset_count() public view returns (uint32 count) {
    uint256[1] memory return_value;

    assembly {
      let asset_count := sload(asset_count_slot)
      mstore(return_value, asset_count)
      return(return_value, 32)
    }
  }

  function get_balance(address user, uint32 asset_id) public view returns (uint256 return_balance) {
    uint256[1] memory return_value;

    assembly {
      let user_ptr := add(users_slot, mul(user, /* USER_SIZE */ 4294967296))

      let asset_balance := sload(add(user_ptr, asset_id))
      mstore(return_value, asset_balance)
      return(return_value, 32)
    }
  }

  function get_session(address user, uint32 exchange_id) public view
  returns (uint64 version, uint64 expire_time, uint64 fee_limit, uint64 fee_used) {
    uint256[4] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(user, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let session_data := sload(session_ptr)
      let time_data := sload(add(session_ptr, 1))

      mstore(return_values, /* TIME(time_data).version */ and(div(time_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 32), /* TIME(time_data).expire_time */ and(div(time_data, 0x1), 0xffffffffffffffff))
      mstore(add(return_values, 64), /* ETHER_POSITION(session_data).fee_limit */ and(div(session_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 96), /* ETHER_POSITION(session_data).fee_used */ and(div(session_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))

      return(return_values, 128)
    }
  }

  function get_session_balance(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (uint64 total_deposit, uint64 asset_balance) {
    uint256[2] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(user, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let data := sload(add(session_ptr, mul(3, asset_id)))

      mstore(return_values, /* POSITION(data).total_deposit */ and(div(data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 32), /* POSITION(data).asset_balance */ and(div(data, 0x1), 0xffffffffffffffff))

      return(return_values, 64)
    }
  }

  function get_session_position(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (int64 ether_qty, int64 asset_qty, int64 ether_shift, int64 asset_shift) {
    uint256[4] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(user, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let ptr := add(session_ptr, mul(3, asset_id))
      let pos_data := sload(ptr)
      let limit_data := sload(add(ptr, 1))

      mstore(return_values, /* POSITION(pos_data).ether_qty */ and(div(pos_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 32), /* POSITION(pos_data).asset_qty */ and(div(pos_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 64), /* POS_LIMIT(limit_data).ether_shift */ and(div(limit_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 96), /* POS_LIMIT(limit_data).asset_shift */ and(div(limit_data, 0x1), 0xffffffffffffffff))

      return(return_values, 128)
    }
  }

  function get_session_limit(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (uint64 version, int64 min_ether, int64 min_asset, uint64 long_max_price, uint64 short_min_price) {
    uint256[4] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(user, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let ptr := add(session_ptr, mul(3, asset_id))
      let limit_data := sload(add(ptr, 1))
      let price_data := sload(add(ptr, 2))

      mstore(return_values, /* PRICE_LIMIT(price_data).limit_version */ and(div(price_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 32), /* POS_LIMIT(limit_data).min_ether */ and(div(limit_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 64), /* POS_LIMIT(limit_data).min_asset */ and(div(limit_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 96), /* PRICE_LIMIT(price_data).long_max_price */ and(div(price_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, 128), /* PRICE_LIMIT(price_data).short_min_price */ and(div(price_data, 0x1), 0xffffffffffffffff))

      return(return_values, 160)
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
    uint256[1] memory revert_reason;
    uint256[1] memory return_value;

    assembly {
      let creator_address := sload(creator_slot)

      // Only the creator can add an exchange
      if iszero(eq(creator_address, caller)) {
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      // Name must be 12 bytes long
      let name_len := mload(name)
      if iszero(eq(name_len, 12)) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      // Do not overflow exchanges
      let exchange_count := sload(exchange_count_slot)
      if gt(exchange_count, 4294967295 /* 2^32 - 1 */) {
        mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
      }

      let exchange_ptr := add(exchanges_slot, mul(exchange_count, /* EXCHANGE_SIZE */ 2))

      let name_data := mload(add(name, 32))
      let exchange_data := or(name_data, addr)
      sstore(exchange_ptr, exchange_data)

      mstore(return_value, exchange_count)
      exchange_count := add(exchange_count, 1)
      sstore(exchange_count_slot, exchange_count)

      log0(add(return_value, 28), 4)
    }
  }

  function add_asset(string symbol, uint64 unit_scale, address contract_address) public {
    uint256[1] memory return_value;
    uint256[1] memory revert_reason;

    assembly {
      let creator_address := sload(creator_slot)

      if iszero(eq(creator_address, caller)) {
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      let asset_count := add(sload(asset_count_slot), 1)
      if gt(asset_count, 4294967295 /* 2^32 - 1 */) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      /* Symbol must be 4 characters */
      let symbol_len := mload(symbol)
      if iszero(eq(symbol_len, 4)) {
        mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
      }

      /* Unit scale must be non zero */
      if iszero(unit_scale) {
        mstore(revert_reason, 4) revert(add(revert_reason, 31), 1)
      }

      let asset_symbol := mload(add(symbol, 32))
      let data := or(asset_symbol, or(mul(/* unit_scale */ unit_scale, 0x10000000000000000000000000000000000000000), contract_address))
      sstore(add(assets_slot, asset_count), data)
      sstore(asset_count_slot, asset_count)

      mstore(return_value, asset_count)
      log0(add(return_value, 28), 4)
    }
  }

  function deposit_eth() public payable {
    assembly {
      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))

      /* First item is asset 0 (ether) */
      let current_balance := sload(user_ptr)
      sstore(user_ptr, add(current_balance, callvalue))
    }
  }

  function withdraw_eth(address destination, uint256 amount) public {
    uint256[1] memory empty_return;

    assembly {
      let ether_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))

      /* Check balance */
      let ether_balance := sload(ether_ptr)
      if gt(amount, ether_balance) {
        revert(0, 0)
      }

      // Update balance
      sstore(ether_ptr, sub(ether_balance, amount))

      // Send funds
      let result := call(
        /* do not forward any gas, use min for transfer */
        0,
        destination,
        amount,
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

  function deposit_asset(uint32 asset_id, uint256 amount) public {
    uint256[1] memory revert_reason;
    uint256[4] memory transfer_in;
    uint256[1] memory transfer_out;

    assembly {
      /* Validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if or(iszero(asset_id), gt(asset_id, asset_count)) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }
      }

      mstore(transfer_in, /* transferFrom(address,address,uint256) */ 0x23b872dd00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), caller)
      mstore(add(transfer_in, 36), address)
      mstore(add(transfer_in, 68), amount)

      let asset_data := sload(add(assets_slot, asset_id))
      let asset_address := /* ASSET(asset_data).address */ and(div(asset_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff)

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

      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))
      let asset_ptr := add(user_ptr, asset_id)
      let current_balance := sload(asset_ptr)

      sstore(asset_ptr, add(current_balance, amount))
    }
  }

  function withdraw_asset(uint32 asset_id, address destination, uint256 amount) {
    uint256[3] memory transfer_in;
    uint256[1] memory transfer_out;
    uint256[1] memory revert_reason;

    assembly {
      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))

      let asset_data := sload(add(assets_slot, asset_id))

      /* Ensure asset_id is valid */
      if or(iszero(asset_id), iszero(asset_data)) {
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      let asset_ptr := add(user_ptr, asset_id)
      let current_balance := sload(asset_ptr)
      if lt(current_balance, amount) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      mstore(transfer_in, /* transfer(address,uint256) */ 0xa9059cbb00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), destination)
      mstore(add(transfer_in, 36), amount)

      let asset_address := /* ASSET(asset_data).address */ and(div(asset_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff)

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

  function update_session(uint32 exchange_id, uint64 expire_time) public payable {
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
        add(mul(caller, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      /* Update expire time */
      {
        let time_ptr := add(session_ptr, 1)
        let time_data := sload(time_ptr)
        sstore(time_ptr, or(mul(/* padding */ /* padding */ 0, 0x100000000000000000000000000000000), or(mul(/* version */ add(/* TIME(time_data).version */ and(div(time_data, 0x10000000000000000), 0xffffffffffffffff), 1), 0x10000000000000000), expire_time)))
      }

      /* Log expire time update */
      mstore(log_data_ptr, caller)
      mstore(add(log_data_ptr, 32), exchange_id)
      log1(
        log_data_ptr, 64,
        /* SessionUpdated */ 0x1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7
      )

      if not(callvalue) {
        stop()
      }
    }

    deposit_eth_to_session(exchange_id);
  }

  function transfer_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) {
    uint256[1] memory revert_reason;
    uint256[1] memory session_id_ptr;
    uint256[4] memory session_deposit_ptr;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(caller, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))
      let asset_ptr := add(user_ptr, asset_id)
      let asset_balance := sload(asset_ptr)

      /* Update asset_balance variable */
      {
        /* Convert quantity to amount using unit_scale */
        let asset_data := sload(add(assets_slot, asset_id))
        let unit_scale := /* ASSET(asset_data).unit_scale */ and(div(asset_data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff)

        /* Note mul cannot overflow as both numbers are 64 bit and result is 256 bits */
        let amount := mul(quantity, unit_scale)

        /* Ensure user has enough asset_balance for deposit */
        if gt(amount, asset_balance) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }

        asset_balance := sub(asset_balance, amount)
      }

      /* Update session asset_balance */
      let position_ptr := add(session_ptr, mul(3, asset_id))
      let position_data := sload(position_ptr)

      let total_deposit := and(add(/* POSITION(position_data).total_deposit */ and(div(position_data, 0x10000000000000000), 0xffffffffffffffff), quantity), 0xFFFFFFFFFFFFFFFF)
      let position_balance := add(/* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff), quantity)

      /* ensure position_balance doesn't overflow */
      if gt(position_balance, 0xFFFFFFFFFFFFFFFF) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      /* Update balances */
      sstore(asset_ptr, asset_balance)
      sstore(position_ptr, or(
        and(position_data, 0xffffffffffffffffffffffffffffffff00000000000000000000000000000000),
        or(mul(total_deposit, 0x10000000000000000), position_balance)
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

  function deposit_eth_to_session(uint32 exchange_id) public payable {
    uint256[3] memory log_data_ptr;
    uint256[1] memory revert_reason;

    assembly {
      let ether_deposit := callvalue

      /* ignore zero value deposits */
      if iszero(ether_deposit) {
        stop()
      }

      /* ensure exchange_id is valid */
      {
        let exchange_count := sload(exchange_count_slot)
        if iszero(lt(exchange_id, exchange_count)) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }
      }

      /* calculate ether amount to send to session (session_deposit) */
      let session_deposit := and(div(ether_deposit, 10000000000), 0xFFFFFFFFFFFFFFFF)
      ether_deposit := sub(ether_deposit, mul(session_deposit, 10000000000))

      let session_ptr := add(sessions_slot, mul(
        add(mul(caller, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let ether_position := sload(session_ptr)
      let total_deposit := and(add(/* ETHER_POSITION(ether_position).total_deposit */ and(div(ether_position, 0x10000000000000000), 0xffffffffffffffff), session_deposit), 0xFFFFFFFFFFFFFFFF)
      let ether_balance := add(/* ETHER_POSITION(ether_position).ether_balance */ and(div(ether_position, 0x1), 0xffffffffffffffff), session_deposit)

      /* check for ether_balance overflow */
      if gt(ether_balance, 0xFFFFFFFFFFFFFFFF) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      sstore(
        session_ptr,
        or(
          and(ether_position, 0xffffffffffffffffffffffffffffffff00000000000000000000000000000000),
          or(mul(total_deposit, 0x10000000000000000), ether_balance)
        )
      )

      /* Store the leftover funds in the user's wallet */
      if ether_deposit {
        let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))
        sstore(user_ptr, add(sload(user_ptr), ether_deposit))
      }

      mstore(log_data_ptr, caller)
      mstore(add(log_data_ptr, 32), exchange_id)
      mstore(add(log_data_ptr, 64), 0)
      log1(
        log_data_ptr, 96,
        /* PositionUpdated */ 0x80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500
      )
    }
  }

  function deposit_asset_to_session(uint32 exchange_id, uint32 asset_id, uint64 quantity) public {
    uint256[1] memory revert_reason;
    uint256[4] memory transfer_in;
    uint256[1] memory transfer_out;
    uint256[3] memory log_data_ptr;

    assembly {
      /* validate asset_id */
      {
        let asset_count := sload(asset_count_slot)
        if or(iszero(asset_id), gt(asset_id, asset_count)) {
          mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
        }
      }

      let asset_data := sload(add(assets_slot, asset_id))
      let amount := mul(quantity, /* ASSET(asset_data).unit_scale */ and(div(asset_data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff))
      let asset_address := /* ASSET(asset_data).address */ and(div(asset_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff)

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
        add(mul(caller, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let position_ptr := add(session_ptr, mul(3, asset_id))

      let position_data := sload(position_ptr)
      let total_deposit := and(add(/* POSITION(position_data).total_deposit */ and(div(position_data, 0x10000000000000000), 0xffffffffffffffff), quantity), 0xFFFFFFFFFFFFFFFF)
      let asset_balance := add(/* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff), quantity)

      /* check for asset_balance overflow */
      if gt(asset_balance, 0xFFFFFFFFFFFFFFFF) {
        mstore(revert_reason, 4) revert(add(revert_reason, 31), 1)
      }

      sstore(
        position_ptr,
        or(
          and(position_data, 0xffffffffffffffffffffffffffffffff00000000000000000000000000000000),
          or(mul(total_deposit, 0x10000000000000000), asset_balance)
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

}

