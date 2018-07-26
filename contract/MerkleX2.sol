pragma solidity ^0.4.23;

// contract ERC20 {
//   function transferFrom(address from, address to, uint256 value) public returns (bool);
//   function approve(address spender, uint256 value) public returns (bool);
// }

contract MerkleX {
  uint256 owner;
  uint256 fee_balance;

  /*
     ALLOWANCE_DEF {
      long_qty         :  60,
      long_qty_pow     :   4,

      long_price       :  28,
      long_price_pow   :   4,

      short_qty        :  60,
      short_qty_pow    :   4,

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

  /*
  struct UserPosition {
    uint256 allowance_1;
    uint256 position_1;
    uint256 allowance_2;
    uint256 position_2;

    uint256 balance;
    uint256 _padding_1;
    uint256 _padding_2;
    uint256 _padding_3;
  }
  */

  uint256[2**32] user_addresses;
  uint256[2**16] token_addresses;
  uint256[2**32][2**16][2**3] user_positions;
  // uint256[2**(32+16+3)] user_positions;

  constructor() public {
    assembly {
      sstore(owner_slot, address)
    }
  }

  function set_balance(uint32 user_id, uint16 token_id, uint64 balance_sig, uint64 pow) public {
    assembly {
      let ptr := add(user_positions_slot, add(or(mul(user_id, 524288), mul(token_id, 8)), 4))
      sstore(ptr, add(sload(ptr), mul(balance_sig, exp(10, pow))))
    }
  }

  function get_balance(uint32 user_id, uint16 token_id) constant returns (uint256) {
    uint256[1] memory return_value;
    assembly {
      let ptr := add(user_positions_slot, add(or(mul(user_id, 524288), mul(token_id, 8)), 4))
      mstore(return_value, sload(ptr))
      return(return_value, 32)
    }
  }

  function get_balance2(uint32 user_id, uint16 token_id) constant returns (uint256) {
    return user_positions[user_id][token_id][4];
  }

  function set_balance2(uint32 user_id, uint16 token_id, uint256 balance) public {
    user_positions[user_id][token_id][4] = balance;
  }

  /*
     WINDOW_HDR_DEF {
      token_id     : 16,
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

    // used to verify a group nets to 0
    uint256[2] memory total_eth_qty;
    uint256[2] memory total_tkn_qty;

    uint256[1] memory collected_fees;

    assembly {
      let cursor_end := mload(data)
      let cursor := add(data, 32)
      cursor_end := add(cursor, cursor_end)

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

          let is_long := SETTLE(settlement).is_long

          // update group totals
          {
            let eth_ptr := total_eth_qty
            let tkn_ptr := total_tkn_qty

            if is_long {
              eth_ptr := add(total_eth_qty, 32)
              tkn_ptr := add(total_tkn_qty, 32)
            }

            mstore(eth_ptr, add(mload(eth_ptr), eth_qty))
            mstore(tkn_ptr, add(mload(eth_ptr), tkn_qty))
          }

          // update fee total
          let fee := mul(SETTLE(settlement).fee, exp(10, SETTLE(settlement).fee_pow))
          mstore(collected_fees, add(mload(collected_fees), fee))

          // extract position pointers
          /* 5 container + 16 token_id + 3 count */ 
          let eth_balance_ptr := SETTLE(settlement).user_id(24)
          let tkn_position_ptr := or(eth_balance_ptr, mul(token_id, 256))
          eth_balance_ptr := add(eth_balance_ptr, 128)

          // apply balance updates
          switch is_long
          case 0 {
            {
              let eth_balance := sload(eth_balance_ptr)

              // is the fee larger than the received ETH?
              switch gt(fee, eth_qty)
              case 0 {
                // nope, increase the balance with the difference
                sstore(eth_balance_ptr, add(eth_balance, sub(eth_qty, fee)))
              }
              default {
                // yup, make sure user has enough $
                let eth_sub := sub(fee, eth_qty)
                if lt(eth_balance, eth_sub) {
                  revert(0,0)
                }
                sstore(eth_balance_ptr, sub(eth_balance, eth_sub))
              }

              let tkn_balance_ptr := add(tkn_position_ptr, 128)
              let tkn_balance := sload(tkn_balance_ptr)

              // make sure user has enough token
              if lt(tkn_balance, tkn_qty) {
                revert(0,0)
              }
              sstore(tkn_balance_ptr, sub(tkn_balance, tkn_qty))
            }
          }
          default {
            {
              let tkn_balance_ptr := add(tkn_position_ptr, 128)
              sstore(tkn_balance_ptr, add(sload(tkn_balance_ptr), tkn_qty))
              let eth_balance := sload(eth_balance_ptr)
              let eth_change := add(eth_qty, fee)
              if lt(eth_balance, eth_change) {
                revert(0,0)
              }
              sstore(eth_balance_ptr, sub(eth_balance, eth_change))
            }
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

