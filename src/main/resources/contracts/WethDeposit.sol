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
#define DCN_WETH_UNIT_SCALE 10000000000
#define DCN_ADDRESS 0x84f6451efe944ba67bedb8e0cf996fa1feb4031d
#define WETH_ADDRESS 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2
#define U256_MAX 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF

contract WethDeposit {
  constructor() public {
    uint256[3] memory transfer_in_mem;

    assembly {
      /* approve(address guy, uint256 wad) WETH transfers */
      mstore(transfer_in_mem, fn_hash("approve(address,uint256)"))
      mstore(add(transfer_in_mem, 4), DCN_ADDRESS)
      mstore(add(transfer_in_mem, const_add(4, WORD_1)), U256_MAX)
      {
        let success := call(
          gas,
          WETH_ADDRESS,
          /* don't send any ether */ 0,
          transfer_in_mem,
          /* transfer_in_mem size (bytes) */ const_add(4, WORD_2),
          0, 0
        )

        if iszero(success) {
          REVERT(1)
        }
      }
    }
  }

  function deposit(uint64 user_id, uint32 exchange_id) public payable {
    uint256[5] memory transfer_in_mem;
    uint256[1] memory transfer_out_mem;

    assembly {
      /* ensure there is no extra change */
      if mod(callvalue, DCN_WETH_UNIT_SCALE) {
        REVERT(1)
      }

      /* deposit() into WETH */
      mstore(transfer_in_mem, fn_hash("deposit()"))
      {
        let success := call(
          gas,
          WETH_ADDRESS,
          /* relay all funds */ callvalue,
          transfer_in_mem,
          /* transfer_in_mem size (bytes) */ 4,
          transfer_out_mem,
          /* transfer_out_mem size (bytes) */ 0
        )

        if iszero(success) {
          REVERT(2)
        }
      }

      /* user_deposit_to_session(uint64 user_id, uint32 exchange_id, uint32 asset_id, uint64 quantity) into DCN */
      mstore(transfer_in_mem, fn_hash("user_deposit_to_session(uint64,uint32,uint32,uint64)"))
      mstore(add(transfer_in_mem, 4), user_id)
      mstore(add(transfer_in_mem, const_add(4, WORD_1)), exchange_id)
      mstore(add(transfer_in_mem, const_add(4, WORD_2)), DCN_WETH_ASSET_ID)
      mstore(add(transfer_in_mem, const_add(4, WORD_3)), div(callvalue, DCN_WETH_UNIT_SCALE))
      {
        let success := call(
          gas,
          DCN_ADDRESS,
          /* don't send any ether */ 0,
          transfer_in_mem,
          /* transfer_in_mem size (bytes) */ const_add(4, WORD_4),
          transfer_out_mem,
          /* transfer_out_mem size (bytes) */ 0
        )

        if iszero(success) {
          REVERT(3)
        }
      }
    }
  }
}

