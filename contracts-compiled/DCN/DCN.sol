pragma solidity ^0.5.0;

contract DCN {
  event SessionUpdated(address user, uint64 exchange_id);
  event PositionUpdated(address user, uint64 exchange_id, uint32 asset_id); 

  /*
     Contract Constants

     #define CHAIN_ID 1111
     #define VERSION  1
  */

  uint256 creator;
  uint256 exchange_count;
  uint256 asset_count;

  /* Memory Layout */

  /*
     #define EXCHANGE_COUNT  4294967296
     #define MAX_EXCHANGE_ID 4294967295
     #define EXCHANGE_SIZE   2

     EXCHANGE_DEF {
      name              :  64,
      quote_asset_id    :  32,
      address           : 160,
     }

     Layout
      0: EXCHANGE_DEF
      1: fee balance
  */
  uint256[/* EXCHANGE_SIZE */ 2 * /* EXCHANGE_COUNT */ 4294967296]  exchanges;


  /*
     #define ASSET_COUNT 4294967296
     #define MAX_ASSET_ID 4294967295

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
     #define SESSION_COUNT       6277101735386680763835789423207666416102355444464034512896
     #define SESSION_SIZE        12884901888
     #define SESSION_ASSET_SIZE  3

     size = session_asset_size * asset_count
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
      quote_qty         :  64,
      base_qty          :  64,
      total_deposit     :  64,
      asset_balance     :  64,
    }

    POS_LIMIT_DEF {
      min_quote         :  64,
      min_base          :  64,
      quote_shift       :  64,
      base_shift        :  64,
    }

    PRICE_LIMIT_DEF {
      padding           :  64,
      limit_version     :  64,
      long_max_price    :  64,
      short_min_price   :  64,
    }
  */

  /*
   * Tests:
   *
   * CreatorTests
   * - contract creator should be creator
   * AssetTests
   * - eth should exist at asset_id=0
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
  returns (string memory symbol, uint64 unit_scale, address contract_address) {
    uint256[5] memory return_value;

    assembly {
      let data := sload(add(assets_slot, asset_id))

      mstore(return_value, 96)
      mstore(add(return_value, 96), 4)
      mstore(add(return_value, 128), data)

      mstore(add(return_value, /* 1_WORD */ 32), /* ASSET(data).unit_scale */ and(div(data, 0x10000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_value, /* 2_WORD */ 64), /* ASSET(data).address */ and(div(data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff))

      return(return_value, 132)
    }
  }

  function get_exchange(uint32 id) public view returns (string memory name, uint64 quote_asset_id, address addr, uint64 fee_balance) {
    /* [ name_offset, quote_asset_id, addr, fee_balance, name_len, name_data(8) ] */
    uint256[6] memory return_value;

    assembly {
      let exchange_ptr := add(exchanges_slot, mul(id, /* EXCHANGE_SIZE */ 2))
      let exchange_data := sload(exchange_ptr)

      /* Store name */
      mstore(return_value, /* 4_WORD */ 128)
      mstore(add(return_value, /* 4_WORD */ 128), 8)
      mstore(add(return_value, /* 5_WORD */ 160), exchange_data)

      /* Store quote_asset_id */
      mstore(add(return_value, /* 1_WORD */ 32), /* EXCHANGE(exchange_data).quote_asset_id */ and(div(exchange_data, 0x10000000000000000000000000000000000000000), 0xffffffff))

      /* Store addr */
      mstore(add(return_value, /* 2_WORD */ 64), /* EXCHANGE(exchange_data).address */ and(div(exchange_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff))

      /* Store fee_balance */
      exchange_data := sload(add(exchange_ptr, 1))
      mstore(add(return_value, /* 3_WORD */ 96), exchange_data)

      return(return_value, 168)
    }
  }

  function get_exchange_count() public view returns (uint32 count) {
    uint256[1] memory return_value;

    assembly {
      let data := sload(exchange_count_slot)
      mstore(return_value, data)
      return(return_value, /* 1_WORD */ 32)
    }
  }

  function get_asset_count() public view returns (uint32 count) {
    uint256[1] memory return_value;

    assembly {
      let asset_count := sload(asset_count_slot)
      mstore(return_value, asset_count)
      return(return_value, /* 1_WORD */ 32)
    }
  }

  function get_balance(address user, uint32 asset_id) public view returns (uint256 return_balance) {
    uint256[1] memory return_value;

    assembly {
      let user_ptr := add(users_slot, mul(user, /* USER_SIZE */ 4294967296))

      let asset_balance := sload(add(user_ptr, asset_id))
      mstore(return_value, asset_balance)
      return(return_value, /* 1_WORD */ 32)
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
      mstore(add(return_values, /* 1_WORD */ 32), /* TIME(time_data).expire_time */ and(div(time_data, 0x1), 0xffffffffffffffff))
      mstore(add(return_values, /* 2_WORD */ 64), /* ETHER_POSITION(session_data).fee_limit */ and(div(session_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 3_WORD */ 96), /* ETHER_POSITION(session_data).fee_used */ and(div(session_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))

      return(return_values, /* 4_WORD */ 128)
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
      mstore(add(return_values, /* 1_WORD */ 32), /* POSITION(data).asset_balance */ and(div(data, 0x1), 0xffffffffffffffff))

      return(return_values, /* 2_WORD */ 64)
    }
  }

  function get_session_position(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (int64 quote_qty, int64 base_qty, int64 quote_shift, int64 base_shift) {
    uint256[4] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(user, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let ptr := add(session_ptr, mul(3, asset_id))
      let pos_data := sload(ptr)
      let limit_data := sload(add(ptr, 1))

      mstore(return_values, /* POSITION(pos_data).quote_qty */ and(div(pos_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 1_WORD */ 32), /* POSITION(pos_data).base_qty */ and(div(pos_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 2_WORD */ 64), /* POS_LIMIT(limit_data).quote_shift */ and(div(limit_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 3_WORD */ 96), /* POS_LIMIT(limit_data).base_shift */ and(div(limit_data, 0x1), 0xffffffffffffffff))

      return(return_values, /* 4_WORD */ 128)
    }
  }

  function get_session_limit(address user, uint32 exchange_id, uint32 asset_id) public view
  returns (uint64 version, int64 min_quote, int64 min_base, uint64 long_max_price, uint64 short_min_price) {
    uint256[4] memory return_values;

    assembly {
      let session_ptr := add(sessions_slot, mul(
        add(mul(user, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
        /* SESSION_SIZE */ 12884901888
      ))

      let ptr := add(session_ptr, mul(asset_id, 3))
      let limit_data := sload(add(ptr, 1))
      let price_data := sload(add(ptr, 2))

      mstore(return_values, /* PRICE_LIMIT(price_data).limit_version */ and(div(price_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 1_WORD */ 32), /* POS_LIMIT(limit_data).min_quote */ and(div(limit_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 2_WORD */ 64), /* POS_LIMIT(limit_data).min_base */ and(div(limit_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 3_WORD */ 96), /* PRICE_LIMIT(price_data).long_max_price */ and(div(price_data, 0x10000000000000000), 0xffffffffffffffff))
      mstore(add(return_values, /* 4_WORD */ 128), /* PRICE_LIMIT(price_data).short_min_price */ and(div(price_data, 0x1), 0xffffffffffffffff))

      return(return_values, /* 5_WORD */ 160)
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
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      /* Name must be 8 bytes long */
      let name_len := mload(name)
      if iszero(eq(name_len, 8)) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      /* Quote asset must exist */
      let asset_count := sload(asset_count_slot)
      if gt(quote_asset_id, asset_count) {
        mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
      }

      /* Do not overflow exchanges */
      let exchange_count := sload(exchange_count_slot)
      if gt(exchange_count, /* MAX_EXCHANGE_ID */ 4294967295) {
        mstore(revert_reason, 4) revert(add(revert_reason, 31), 1)
      }

      let exchange_ptr := add(exchanges_slot, mul(exchange_count, /* EXCHANGE_SIZE */ 2))

      /*
       * name_data will start with 12 bytes of name data
       * and addr is 20 bytes. Total 32 bytes (one word)
       */
      let name_data := mload(add(name, 32))
      sstore(exchange_ptr, or(name_data, or(mul(/* quote_asset_id */ quote_asset_id, 0x10000000000000000000000000000000000000000), addr)))

      exchange_count := add(exchange_count, 1)
      sstore(exchange_count_slot, exchange_count)
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
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      let asset_count := add(sload(asset_count_slot), 1)
      if gt(asset_count, /* MAX_ASSET_ID */ 4294967295) {
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
    }
  }


  /*
   * Tests:
   *
   * BalanceTests
   * - manage ether
   * -- should be able to deposit
   */
  function deposit_eth() public payable {
    assembly {
      /* First item is asset 0 (ether) */
      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))
      let current_balance := sload(user_ptr)

      /* Note, total ether supply should never overflow 2^256 */
      sstore(user_ptr, add(current_balance, callvalue))
    }
  }

  /*
   * Tests:
   *
   * BalanceTests
   * - manage ether
   * -- should be able to partially withdraw
   * -- should fail to over withdraw
   * -- should be able to withdraw to zero
   * -- should not be able to withdraw at zero
   */
  function withdraw_eth(address destination, uint256 amount) public {
    uint256[1] memory empty_return;

    assembly {
      let ether_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))

      /* Check balance */
      let ether_balance := sload(ether_ptr)
      if gt(amount, ether_balance) {
        revert(0, 0)
      }

      /* Update balance */
      sstore(ether_ptr, sub(ether_balance, amount))

      /* Send funds */
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

      let user_ptr := add(users_slot, mul(caller, /* USER_SIZE */ 4294967296))
      let asset_ptr := add(user_ptr, asset_id)
      let current_balance := sload(asset_ptr)
      if lt(current_balance, amount) {
        mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
      }

      mstore(transfer_in, /* transfer(address,uint256) */ 0xa9059cbb00000000000000000000000000000000000000000000000000000000)
      mstore(add(transfer_in, 4), destination)
      mstore(add(transfer_in, 36), amount)

      let asset_data := sload(add(assets_slot, asset_id))
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

      /* Log */
      mstore(log_data_ptr, caller)
      mstore(add(log_data_ptr, 32), exchange_id)
      log1(
        log_data_ptr, 64,
        /* SessionUpdated */ 0x1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7
      )
    }

    deposit_eth_to_session(exchange_id);
  }

  /*
   * Tests
   *
   * DepositEthToSessionTests
   * - should not rever, update, nor log with no value
   * - should move remainder to balance
   * - should fail on session balance overflow
   * - should just move to balance if under unit scale
   * - should move overflow to balance
   */
  function deposit_eth_to_session(uint32 exchange_id) public payable {
    uint256[3] memory log_data_ptr;
    uint256[1] memory revert_reason;

    assembly {
      let ether_deposit := callvalue

      /* ignore zero value deposits */
      if iszero(ether_deposit) {
        stop()
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

     #define 1_WORD 32
     #define 2_WORD 64
     #define 3_WORD 96
     #define 4_WORD 128
     #define 5_WORD 160
     #define 6_WORD 192
     #define 7_WORD 224
     #define 8_WORD 256
     #define 9_WORD 288
     #define 10_WORD 320

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

      if iszero(eq(data_size, /* LIMIT_UPDATE_SIZE */ 149)) {
        mstore(revert_reason, 1) revert(add(revert_reason, 31), 1)
      }

      let update_data := mload(cursor)
      cursor := add(cursor, /* UPDATE_LIMIT_ADDR_SIZE */ 20)

      user_addr := /* UPDATE_LIMIT_ADDR(update_data).user_address */ and(div(update_data, 0x1000000000000000000000000), 0xffffffffffffffffffffffffffffffffffffffff)

      /* fill data_hash_buffer */

      mstore(data_hash_buffer, /* UPDATE_LIMIT_TYPE_HASH */ 0x74be7520fc933d8061b6cf113d28a772f7a40539ab5e0e8276dd066dd71a7d69)

      update_data := mload(cursor)
      cursor := add(cursor, /* UPDATE_LIMIT_1_SIZE */ 32)

      let position_ptr := 0
      {
        let exchange_id := /* UPDATE_LIMIT_1(update_data).exchange_id */ and(div(update_data, 0x100000000000000000000000000000000000000000000000000000000), 0xffffffff)
        mstore(add(data_hash_buffer, /* 1_WORD */ 32), exchange_id)

        let asset_id := /* UPDATE_LIMIT_1(update_data).asset_id */ and(div(update_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffff)
        mstore(add(data_hash_buffer, /* 3_WORD */ 96), asset_id)

        /* exchange address must be caller */
        {
          let exchange_data := sload(add(
            exchanges_slot,
            mul(exchange_id, /* EXCHANGE_SIZE */ 2)
          ))

          let exchange_address := /* EXCHANGE(exchange_data).address */ and(div(exchange_data, 0x1), 0xffffffffffffffffffffffffffffffffffffffff)

          if iszero(eq(caller, exchange_address)) {
            mstore(revert_reason, 2) revert(add(revert_reason, 31), 1)
          }
        }

        position_ptr := add(
          add(sessions_slot, mul(
            add(mul(user_addr, /* EXCHANGE_COUNT */ 4294967296), exchange_id),
            /* SESSION_SIZE */ 12884901888
          )),
          mul(asset_id, /* SESSION_ASSET_SIZE */ 3)
        )
      }

      {
        let version := /* UPDATE_LIMIT_1(update_data).version */ and(div(update_data, 0x100000000000000000000000000000000), 0xffffffffffffffff)
        // mstore(revert_reason, update_data) revert(revert_reason, 32)

        mstore(add(data_hash_buffer, /* 2_WORD */ 64), version)

        /* version must increase */
        {
          let version_data := sload(add(position_ptr, 2))
          let current_version := /* PRICE_LIMIT(version_data).limit_version */ and(div(version_data, 0x100000000000000000000000000000000), 0xffffffffffffffff)

          if iszero(lt(current_version, version)) {
            mstore(revert_reason, 3) revert(add(revert_reason, 31), 1)
          }
        }

        let max_long_price := /* UPDATE_LIMIT_1(update_data).max_long_price */ and(div(update_data, 0x10000000000000000), 0xffffffffffffffff)
        mstore(add(data_hash_buffer, /* 4_WORD */ 128), max_long_price)

        let min_short_price := /* UPDATE_LIMIT_1(update_data).min_short_price */ and(div(update_data, 0x1), 0xffffffffffffffff)
        mstore(add(data_hash_buffer, /* 5_WORD */ 160), min_short_price)

        sstore(add(position_ptr, 2), or(mul(/* limit_version */ version, 0x100000000000000000000000000000000), or(mul(/* long_max_price */ max_long_price, 0x10000000000000000), min_short_price)))
      }

      update_data := mload(cursor)
      cursor := add(cursor, /* UPDATE_LIMIT_2_SIZE */ 32)

      {
        let pos_limit := 0

        /* Note, set pos_limit before handling neg so we don't need to mask */
        {
          let min_quote_qty := /* UPDATE_LIMIT_2(update_data).min_quote_qty */ and(div(update_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff)
          pos_limit := mul(/* min_quote */ min_quote_qty, 0x1000000000000000000000000000000000000000000000000)

          if and(min_quote_qty, /* NEG_64_FLAG */ 0x8000000000000000) {
            min_quote_qty := or(min_quote_qty, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          }
          mstore(add(data_hash_buffer, /* 6_WORD */ 192), min_quote_qty)
        }

        {
          let min_base_qty := /* UPDATE_LIMIT_2(update_data).min_base_qty */ and(div(update_data, 0x100000000000000000000000000000000), 0xffffffffffffffff)
          pos_limit := or(pos_limit, mul(/* min_base */ min_base_qty, 0x100000000000000000000000000000000))

          if and(min_base_qty, /* NEG_64_FLAG */ 0x8000000000000000) {
            min_base_qty := or(min_base_qty, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          }
          mstore(add(data_hash_buffer, /* 7_WORD */ 224), min_base_qty)
        }

        let quote_shift := /* UPDATE_LIMIT_2(update_data).quote_shift */ and(div(update_data, 0x10000000000000000), 0xffffffffffffffff)
        pos_limit := or(pos_limit, mul(/* quote_shift */ quote_shift, 0x10000000000000000))

        if and(quote_shift, /* NEG_64_FLAG */ 0x8000000000000000) {
          quote_shift := or(quote_shift, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
        }
        mstore(add(data_hash_buffer, /* 8_WORD */ 256), quote_shift)

        let base_shift := /* UPDATE_LIMIT_2(update_data).base_shift */ and(div(update_data, 0x1), 0xffffffffffffffff)
        pos_limit := or(pos_limit, base_shift)

        if and(base_shift, /* NEG_64_FLAG */ 0x8000000000000000) {
          base_shift := or(base_shift, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
        }
        mstore(add(data_hash_buffer, /* 9_WORD */ 288), base_shift)

        /* Normalize ether shift against existing */
        {
          let current_pos_limit_data := sload(add(position_ptr, 1))

          {
            let current_quote_shift := /* POS_LIMIT(current_pos_limit_data).quote_shift */ and(div(current_pos_limit_data, 0x10000000000000000), 0xffffffffffffffff)
            if and(current_quote_shift, /* NEG_64_FLAG */ 0x8000000000000000) {
              current_quote_shift := or(current_quote_shift, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
            }
            quote_shift := sub(quote_shift, current_quote_shift)
          }

          {
            let current_base_shift := /* POS_LIMIT(current_pos_limit_data).base_shift */ and(div(current_pos_limit_data, 0x1), 0xffffffffffffffff)
            if and(current_base_shift, /* NEG_64_FLAG */ 0x8000000000000000) {
              current_base_shift := or(current_base_shift, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
            }
            base_shift := sub(base_shift, current_base_shift)
          }
        }

        let position_data := sload(position_ptr)
        let quote_qty := add(quote_shift, /* POSITION(position_data).quote_qty */ and(div(position_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff))
        let base_qty := add(base_shift, /* POSITION(position_data).base_qty */ and(div(position_data, 0x100000000000000000000000000000000), 0xffffffffffffffff))

        sstore(position_ptr, or(
          and(position_data, /* U128_MASK */ 0xffffffffffffffffffffffffffffffff),
          or(mul(/* quote_qty */ quote_qty, 0x1000000000000000000000000000000000000000000000000), mul(/* base_qty */ base_qty, 0x100000000000000000000000000000000))
        ))
        sstore(add(position_ptr, 1), pos_limit)
      }

      let hash := keccak256(data_hash_buffer, /* 10_WORD */ 320)

      {
        let final_ptr := data_hash_buffer
        mstore(final_ptr, /* SIG_HASH_HEADER */ 0x1901000000000000000000000000000000000000000000000000000000000000)
        final_ptr := add(final_ptr, 2)
        mstore(final_ptr, /* DCN_HEADER_HASH */ 0x8bdc799ab1e4f88b464481578308e5bde325b7ed088fe2b99495c7924d58c7f9)
        final_ptr := add(final_ptr, /* 1_WORD */ 32)
        mstore(final_ptr, hash)
      }

      hash := keccak256(data_hash_buffer, 66)
      mstore(data_hash_buffer, hash)

      update_data := mload(cursor)
      cursor := add(cursor, /* 1_WORD */ 32)
      mstore(add(data_hash_buffer, /* 1_WORD */ 32), update_data)

      update_data := mload(cursor)
      cursor := add(cursor, /* 1_WORD */ 32)
      mstore(add(data_hash_buffer, /* 2_WORD */ 64), update_data)

      update_data := mload(cursor)
      mstore(add(data_hash_buffer, /* 3_WORD */ 96), /* SIG_V(update_data).sig_v */ and(div(update_data, 0x100000000000000000000000000000000000000000000000000000000000000), 0xff))
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
      ether_delta : 64,
      asset_delta : 64,
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
    assembly {
      let cursor := add(data, 1)
      let data_end := add(cursor, mload(data))

      let header_data := mload(cursor)
      let exchange_id := /* GROUPS_HEADER(header_data).exchange_id */ and(div(header_data, 0x100000000000000000000000000000000000000000000000000000000), 0xffffffff)
      cursor := add(cursor, /* GROUPS_HEADER_SIZE */ 4)

      /* keep looping while there is space for a header */
      for {} iszero(lt(sub(data_end, cursor), /* GROUP_HEADER_SIZE */ 40)) {} {
        header_data := mload(cursor)
        let user_count := /* GROUP_HEADER(header_data).user_count */ and(div(header_data, 0x1000000000000000000000000000000000000000000000000000000), 0xff)

        // TODO: validate asset_id?

        let asset_id := /* GROUP_HEADER(header_data).asset_id */ and(div(header_data, 0x100000000000000000000000000000000000000000000000000000000), 0xffffffff)
        let cursor_end := add(cursor, add(mul(user_count, /* SETTLEMENT_SIZE */ 352), /* GROUP_HEADER_SIZE */ 40))

        /* make sure there is enough size for the group */
        if gt(cursor_end, data_end) {
          revert(0, 0)
        }

        let ether_net := 0
        let asset_net := 0

        for {} lt(cursor, cursor_end) {} {
          header_data := mload(cursor)

          let session_ptr := add(
            sessions_slot,
            mul(
              add(
                mul(
                  /* SETTLEMENT_ADDR(header_data).user_address */ and(div(header_data, 0x1000000000000000000000000), 0xffffffffffffffffffffffffffffffffffffffff),
                  /* EXCHANGE_COUNT */ 4294967296
                ),
                exchange_id
              ),
              /* SESSION_SIZE */ 12884901888
            )
          )

          cursor := add(cursor, 20)
          let settlement_data := mload(cursor)

          let ether_delta := /* SETTLEMENT_DATA(settlement_data).ether_delta */ and(div(settlement_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff)
          let asset_delta := /* SETTLEMENT_DATA(settlement_data).asset_delta */ and(div(settlement_data, 0x100000000000000000000000000000000), 0xffffffffffffffff)
          let fees := /* SETTLEMENT_DATA(settlement_data).fees */ and(div(settlement_data, 0x10000000000000000), 0xffffffffffffffff)

          /* convert i64 to i256 */
          if and(ether_delta, /* NEG_64_FLAG */ 0x8000000000000000) {
            ether_delta := or(ether_delta, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          }
          if and(asset_delta, /* NEG_64_FLAG */ 0x8000000000000000) {
            asset_delta := or(asset_delta, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          }

          /* update net totals */
          ether_net := add(ether_net, ether_delta)
          asset_net := add(asset_net, asset_delta)

          /* update ether balance */
          {
            let ether_data := sload(session_ptr)
            let ether_balance := /* ETHER_POSITION(ether_data).ether_balance */ and(div(ether_data, 0x1), 0xffffffffffffffff)
            ether_balance := add(ether_balance, ether_delta)
            ether_balance := sub(ether_balance, /* SETTLEMENT_DATA(settlement_data).fees */ and(div(settlement_data, 0x10000000000000000), 0xffffffffffffffff))

            /* make sure ether balance is positive and doesn't overflow */
            if gt(ether_balance, /* U64_MASK */ 0xffffffffffffffff) {
              revert(0, 0)
            }
            sstore(session_ptr, or(and(ether_data, /* U64_INV_MASK */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000), ether_balance))
          }

          let position_ptr := add(session_ptr, mul(asset_id, /* SESSION_ASSET_SIZE */ 3))
          let position_data := sload(position_ptr)
          let asset_balance := /* POSITION(position_data).asset_balance */ and(div(position_data, 0x1), 0xffffffffffffffff)

          asset_balance := add(asset_balance, asset_delta)
          if gt(asset_balance, /* U64_MASK */ 0xffffffffffffffff) {
            revert(0, 0)
          }

          /* load position and convert i64 to i256 */
          let quote_qty := /* POSITION(position_data).quote_qty */ and(div(position_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff)
          if and(quote_qty, /* NEG_64_FLAG */ 0x8000000000000000) {
            quote_qty := or(quote_qty, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          }
          let base_qty := /* POSITION(position_data).base_qty */ and(div(position_data, 0x100000000000000000000000000000000), 0xffffffffffffffff)
          if and(base_qty, /* NEG_64_FLAG */ 0x8000000000000000) {
            base_qty := or(base_qty, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
          }

          /* Note, shift is applied in limit update and is factored into _qty */

          quote_qty := add(quote_qty, ether_delta)
          base_qty := add(base_qty, asset_delta)

          position_data := or(mul(/* quote_qty */ quote_qty, 0x1000000000000000000000000000000000000000000000000), or(mul(/* base_qty */ base_qty, 0x100000000000000000000000000000000), or(mul(/* total_deposit */ /* POSITION(position_data).total_deposit */ and(div(position_data, 0x10000000000000000), 0xffffffffffffffff), 0x10000000000000000), asset_balance)))
          sstore(position_ptr, position_data)

          if or(sgt(quote_qty, /* I64_MAX */ 0x7fffffffffffffff), sgt(base_qty, /* I64_MAX */ 0x7fffffffffffffff)) {
            revert(0, 0)
          }

          /* Ensure position fits min limits */
          {
            let limit_data := sload(add(position_ptr, 1))

            let min_quote := /* POS_LIMIT(limit_data).min_quote */ and(div(limit_data, 0x1000000000000000000000000000000000000000000000000), 0xffffffffffffffff)
            if and(min_quote, /* NEG_64_FLAG */ 0x8000000000000000) {
              min_quote := or(min_quote, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
            }

            let min_base := /* POS_LIMIT(limit_data).min_base */ and(div(limit_data, 0x100000000000000000000000000000000), 0xffffffffffffffff)
            if and(min_base, /* NEG_64_FLAG */ 0x8000000000000000) {
              min_base := or(min_base, /* I64_TO_NEG */ 0xffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000)
            }

            if or(slt(quote_qty, min_quote), slt(base_qty, min_base)) {
              revert(0, 0)
            }
          }

          /* Ensure there is no overflow */
          if or(slt(quote_qty, /* I64_MIN */ 0xffffffffffffffffffffffffffffffffffffffffffffffff8000000000000000), slt(base_qty, /* I64_MIN */ 0xffffffffffffffffffffffffffffffffffffffffffffffff8000000000000000)) {
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

            let current_price := div(mul(sub(0, quote_qty), /* PRICE_UNITS */ 100000000), base_qty)
            if gt(current_price, /* PRICE_LIMIT(price_limit_data).long_max_price */ and(div(price_limit_data, 0x10000000000000000), 0xffffffffffffffff)) {
              revert(0, 0)
            }
          }
          /* short: base_qty negative */
          case 2 {
            if iszero(quote_qty) {
              revert(0, 0)
            }

            let current_price := div(mul(quote_qty, /* PRICE_UNITS */ 100000000), sub(0, base_qty))
            if lt(current_price, /* PRICE_LIMIT(price_limit_data).short_min_price */ and(div(price_limit_data, 0x1), 0xffffffffffffffff)) {
              revert(0, 0)
            }
          }
        }

        /* ensure net balance is 0 for settlement group */
        if or(ether_net, asset_net) {
          revert(0, 0)
        }
      }
    }
  }
}

