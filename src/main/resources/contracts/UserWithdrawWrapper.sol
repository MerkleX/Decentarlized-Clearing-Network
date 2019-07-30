pragma solidity 0.5.7;

/* required for transpiler */
#define TRANSPILE

#define WORD_0 0   /* 32*0 = 0 */
#define WORD_1 32   /* 32*1 = 32 */
#define WORD_2 64   /* 32*2 = 64 */
#define WORD_3 96   /* 32*3 = 96 */
#define WORD_4 128  /* 32*4 = 128 */
#define WORD_5 160  /* 32*5 = 160 */
#define WORD_6 192  /* 32*6 = 192 */
#define WORD_7 224  /* 32*7 = 224 */
#define WORD_8 256  /* 32*8 = 256 */
#define WORD_9 288  /* 32*9 = 288 */
#define WORD_10 320 /* 32*10 = 320 */
#define WORD_11 352 /* 32*11 = 352 */
#define WORD_12 384 /* 32*12 = 384 */
#define WORD_13 416 /* 32*13 = 416 */
#define WORD_14 448 /* 32*14 = 448 */

#define REVERT(code) \
  mstore(WORD_1, code) revert(const_add(WORD_1, 31), 1)

#define DCN_WETH_ASSET_ID 0
#define WETH_ADDRESS 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2
#define U256_MAX 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF

contract UserWithdrawWrapper {
  struct Settings {
    uint64 user_id;
    uint32 exchange_id;
    uint160 dcn_address;

    uint96 withdraw_as_eth;
    uint160 withdraw_location;
  }

  Settings settings;

  constructor(address dcn_address, uint64 user_id, uint32 exchange_id, address withdraw_location) public {
    assembly {
      sstore(settings_slot, build(Settings, 0,
            user_id,
            exchange_id,
            dcn_address
      ))

      sstore(add(settings_slot, 1), build(Settings, 1,
            /* withdraw as eth */ 1,
            withdraw_location
      ))
    }
  }

  function get_version() public view returns (uint64 version) {
    return 1;
  }

  function get_settings() public view returns (uint64 user_id, uint32 exchange_id, uint160 dcn_address) {
    uint256[3] memory return_mem;

    assembly {
      let settings := sload(settings_slot)

      mstore(return_mem, attr(Settings, 0, settings, user_id))
      mstore(add(return_mem, WORD_1), attr(Settings, 0, settings, exchange_id))
      mstore(add(return_mem, WORD_2), attr(Settings, 0, settings, dcn_address))
      return(return_value_mem, WORD_3)
    }
  }

  function set_withdraw_location(address new_location) public {
    uint256[2] memory tx_in_mem;
    uint256[4] memory tx_out_mem;

    assembly {
      let settings := sload(settings_slot)
      let user_id := attr(Settings, 0, settings, user_id)
      let dcn_address := attr(Settings, 0, settings, dcn_address)

      /* read recovery address from DCN and auth based on that */
      mstore(tx_in_mem, fn_hash("get_user(uint64)"))
      mstore(add(tx_in_mem, 4), user_id)
      {
        let success := call(
          gas,
          dcn_address,
          /* not ether */ 0,
          tx_in_mem,
          /* tx_in_mem size (bytes) */ const_add(4, WORD_1),
          tx_out_mem,
          /* tx_out_mem size (bytes) */ WORD_4
        )

        if iszero(success) {
          REVERT(1)
        }
      }

      let recovery_address := mload(add(tx_out_mem, WORD_2))
      if iszero(eq(caller, recovery_address)) {
        REVERT(2)
      }

      sstore(withdraw_location_slot, new_location)
    }
  }

  function withdraw(uint32 asset_id) public return (uint256 withdraw_amount) {
    uint256[4] memory tx_in_mem;
    uint256[1] memory tx_out_mem;

    assembly {
      let settings_0 := sload(settings_slot)
      let user_id := attr(Settings, 0, settings_0, user_id)
      let dcn_address := attr(Settings, 0, settings_0, dcn_address)

      let settings_1 := sload(add(settings_slot, 1))
      let withdraw_location := attr(Settings, 1, settings_1, withdraw_location)

      /* get_balance(uint64 user_id, uint32 asset_id) public view returns (uint256 return_balance) */
      mstore(tx_in_mem, fn_hash("get_balance(uint64,uint32)"))
      mstore(add(tx_in_mem, const_add(4, WORD_0)), user_id)
      mstore(add(tx_in_mem, const_add(4, WORD_1)), asset_id)
      {
        let success := call(
          gas,
          dcn_address,
          /* not ether */ 0,
          /* in */ tx_in_mem,
          /* in len */ const_add(4, WORD_2),
          /* out */ tx_out_mem,
          /* out len */ WORD_1
        )

        if iszero(success) {
          REVERT(1)
        }
      }

      let current_balance := mload(tx_out_mem)
      if iszero(current_balance) {
        stop()
      }

      /* user_withdraw(uint64 user_id, uint32 asset_id, address destination, uint256 amount) */
      mstore(tx_in_mem, fn_hash("user_withdraw(uint64,uint32,address,uint256)"))
      mstore(add(tx_in_mem, const_add(4, WORD_0)), user_id)
      mstore(add(tx_in_mem, const_add(4, WORD_1)), asset_id)
      mstore(add(tx_in_mem, const_add(4, WORD_2)), withdraw_location)
      mstore(add(tx_in_mem, const_add(4, WORD_3)), current_balance)
      {
        let success := call(
          gas,
          dcn_address,
          /* not ether */ 0,
          /* in */ tx_in_mem,
          /* in len */ const_add(4, WORD_4),
          /* out */ 0, 0
        )

        if iszero(success) {
          REVERT(2)
        }
      }

      mstore(tx_out_mem, current_balance)
      return(tx_out_mem, WORD_1)
    }
  }

  function withdraw_eth() public returns (uint256 withdraw_amount) {
    uint256[4] memory tx_in_mem;
    uint256[4] memory tx_out_mem;

    assembly {
      let settings_0 := sload(settings_slot)
      let user_id := attr(Settings, 0, settings_0, user_id)
      let dcn_address := attr(Settings, 0, settings_0, dcn_address)

      let settings_1 := sload(add(settings_slot, 1))
      let withdraw_location := attr(Settings, 1, settings_1, withdraw_location)

      /* get_balance(uint64 user_id, uint32 asset_id) public view returns (uint256 return_balance) */
      mstore(tx_in_mem, fn_hash("get_balance(uint64,uint32)"))
      mstore(add(tx_in_mem, const_add(4, WORD_0)), user_id)
      mstore(add(tx_in_mem, const_add(4, WORD_1)), DCN_WETH_ASSET_ID)
      {
        let success := call(
          gas,
          dcn_address,
          /* not ether */ 0,
          /* in */ tx_in_mem,
          /* in len */ const_add(4, WORD_2),
          /* out */ tx_out_mem,
          /* out len */ WORD_1
        )

        if iszero(success) {
          REVERT(1)
        }
      }

      let current_balance := mload(tx_out_mem)
      if iszero(current_balance) {
        stop()
      }

      /* user_withdraw(uint64 user_id, uint32 asset_id, address destination, uint256 amount) */
      mstore(tx_in_mem, fn_hash("user_withdraw(uint64,uint32,address,uint256)"))
      mstore(add(tx_in_mem, const_add(4, WORD_0)), user_id)
      mstore(add(tx_in_mem, const_add(4, WORD_1)), DCN_WETH_ASSET_ID)
      mstore(add(tx_in_mem, const_add(4, WORD_2)), address)
      mstore(add(tx_in_mem, const_add(4, WORD_3)), current_balance)
      {
        let success := call(
          gas,
          dcn_address,
          /* not ether */ 0,
          /* in */ tx_in_mem,
          /* in len */ const_add(4, WORD_4),
          /* out */ 0, 0
        )

        if iszero(success) {
          REVERT(2)
        }
      }

      /* withdraw */
      mstore(tx_in_mem, fn_hash("withdraw(uint256)"))
      mstore(add(tx_in_mem, const_add(4, WORD_0)), current_balance)
      {
        let success := call(
          gas,
          WETH_ADDRESS,
          /* not ether */ 0,
          /* in */ tx_in_mem,
          /* in len */ const_add(4, WORD_1),
          /* out */ 0, 0
        )

        if iszero(success) {
          REVERT(3)
        }
      }

      /* send eth */
      {
        let success := call(
          0,
          withdraw_location,
          current_balance,
          /* in */ 0, 0
          /* out */ 0, 0
        )

        if iszero(success) {
          REVERT(4)
        }
      }
    }
  }
}

