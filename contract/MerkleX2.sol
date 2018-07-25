pragma solidity ^0.4.23;

contract ERC20 {
  function transferFrom(address from, address to, uint256 value) public returns (bool);
  function approve(address spender, uint256 value) public returns (bool);
}

contract MerkleX {
  uint256 owner;
  uint256 fee_balance;

  /*
     ALLOWANCE_DEF {
      long_qty         :  60,
      long_qty_pow     :   4,

      short_qty        :  60,
      short_qty_pow    :   4,

      long_price       :  28,
      long_price_pow   :   4,

      short_price      :  28,
      short_price_pow  :   4,

      fees             :  28,
      fee_pow          :   4,

      expire_time      :  32,
     }

     POSITION_DEF {
      is_long          :   1,
      update_count     :  31,

      ether_qty        :  60,
      ether_qty_pow    :   4,

      token_qty        :  60,
      token_qty_pow    :   4,

      fees             :  28,
      fees_pow         :   4,
     }

   */

  struct UserPosition {
    uint256 balance;
    uint256 allowance_1;
    uint256 allowance_2;
    uint256 position_1;
    uint256 position_2;
  }

  uint256[2**32] user_addresses;
  uint256[2**15] token_addresses;
  UserPosition[(2**(15+32))] user_positions;

  constructor() public {
    assembly {
      sstore(owner_slot, address)
    }
  }

  /*
     WINDOW_HDR_DEF {
      _padding     :  1,
      token_id     : 15,
      count        :  8,
     }

     SETTLE_DEF {
      is_long      :  1,
      allowance_id :  1,
      _padding     :  6,

      user_id      : 32,

      eth_qty      : 60,
      eth_qty_pow  :  4,

      tkn_qty      : 60,
      tkn_qty_pow  :  4,

      fee          : 28,
      fee_pow      :  4,
     }
   */

  function submit_group(bytes data) public {
    // uint256[1] memory token_id;

    uint256[2] memory eth_qty;
    uint256[2] memory tkn_qty;

    assembly {
      let cursor_end := add(data, mload(data))
      let cursor := add(data, 32)

      for {} lt(cursor, cursor_end) {} {
        let header := mload(cursor)
        cursor := add(cursor, WINDOW_HDR.BYTES)

        let token_id := WINDOW_HDR(header).token_id
        let group_end := add(cursor, mul(WINDOW_HDR(header).count, SETTLE.BYTES))

        // Ensure there is enough space for group
        if gt(group_end, cursor_end) {
          stop()
        }

        for {} lt(cursor, group_end) { cursor := add(cursor, SETTLE.BYTES) } {
          let settlement := mload(cursor)

          let eth_qty := mul(SETTLE(settlement).eth_qty, exp(10, SETTLE(settlement).eth_qty_pow))
          let tkn_qty := mul(SETTLE(settlement).tkn_qty, exp(10, SETTLE(settlement).tkn_qty_pow))

          // update group totals
          {
            let eth_ptr := eth_qty
            let tkn_ptr := tkn_qty

            if SETTLE(settlement).is_long {
              eth_ptr := add(eth_qty, 32)
              tkn_ptr := add(tkn_qty, 32)
            }

            mstore(eth_ptr, add(mload(eth_ptr), eth_qty))
            mstore(tkn_ptr, add(mload(eth_ptr), tkn_qty))
          }

          /* 
             check against allowance
             update memory totals
             update user balance
             update position
          */
        }
      }
    }
  }
}

