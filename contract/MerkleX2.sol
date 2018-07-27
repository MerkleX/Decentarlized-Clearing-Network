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
      long_qty         :  64,
      long_price       :  28,
      long_price_pow   :   4,

      short_qty        :  64,
      short_price      :  28,
      short_price_pow  :   4,

      max_loss         :  64,
     }

     POSITION_DEF {
      _padding         :  30,
      is_long          :   1,
      is_loss          :   1,
      pl_value         :  64,
      eth_qty          :  64,
      tkn_qty          :  64,
      fees             :  32,
     }
   */

  /*
  struct UserPosition {
    uint256 position_1;
    uint256 allowance_1;

    uint256 position_2;
    uint256 allowance_2;

    uint256 balance;
    uint256 _padding_1;
    uint256 _padding_2;
    uint256 _padding_3;
  }
  */

  uint256[2**32] user_addresses;
  uint256[2**16] token_addresses;
  uint256[2**(32+16+3)] user_positions;

  constructor() public {
    assembly {
      sstore(owner_slot, address)
    }
  }

  function set_balance(uint32 user_id, uint16 token_id, uint64 new_balance) public {
    assembly {
      let ptr := add(user_positions_slot, add(or(mul(user_id, 524288), mul(token_id, 8)), 4))
      sstore(ptr, add(sload(ptr), new_balance))
    }
  }

  function get_balance(uint32 user_id, uint16 token_id) public constant returns (uint256) {
    uint256[1] memory return_value;
    assembly {
      let ptr := add(user_positions_slot, add(or(mul(user_id, 524288), mul(token_id, 8)), 4))
      mstore(return_value, sload(ptr))
      return(return_value, 32)
    }
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

      eth_qty      : 64,
      tkn_qty      : 64,

      fee          : 32,
     }
   */

  function submit_group(bytes data) public {
    // used to verify a group nets to 0
    uint256[1] memory token_id_ptr;
    uint256[2] memory total_eth_ptr;
    uint256[2] memory total_tkn_ptr;
    uint256[1] memory collected_fees_ptr;

    assembly {
      let cursor_end := mload(data)
      let cursor := add(data, 32)
      cursor_end := add(cursor, cursor_end)

      for {} lt(cursor, cursor_end) {} {
        let header := mload(cursor)
        cursor := add(cursor, WINDOW_HDR.BYTES)

        mstore(token_id_ptr, WINDOW_HDR(header).token_id)
        let group_end := add(cursor, mul(WINDOW_HDR(header).count, SETTLE.BYTES))

        // Ensure there is enough space for group
        if gt(group_end, cursor_end) {
          stop()
        }

        for {} lt(cursor, group_end) { cursor := add(cursor, SETTLE.BYTES) } {
          let settlement := mload(cursor)

          let eth_qty := SETTLE(settlement).eth_qty
          let tkn_qty := SETTLE(settlement).tkn_qty

          let is_long := SETTLE(settlement).is_long

          // update group totals
          {
            let eth_ptr := total_eth_ptr
            let tkn_ptr := total_tkn_ptr

            if is_long {
              eth_ptr := add(total_eth_ptr, 32)
              tkn_ptr := add(total_tkn_ptr, 32)
            }

            mstore(eth_ptr, add(mload(eth_ptr), eth_qty))
            mstore(tkn_ptr, add(mload(eth_ptr), tkn_qty))
          }

          // update fee total
          let fee := SETTLE(settlement).fee
          mstore(collected_fees_ptr, add(mload(collected_fees_ptr), fee))

          // extract position pointers
          let tkn_position_ptr := mul(mload(token_id_ptr), 8)

          // UPDATE BALANCES
          {
            /* shift of 19 because 16 bits for token_id + 3 bits for data space */ 
            let eth_balance_ptr := add(user_positions_slot, SETTLE(settlement).user_id(19))
            tkn_position_ptr := or(eth_balance_ptr, tkn_position_ptr)
            eth_balance_ptr := add(eth_balance_ptr, 4)

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

                let tkn_balance_ptr := add(tkn_position_ptr, 4)
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
                let tkn_balance_ptr := add(tkn_position_ptr, 4)
                sstore(tkn_balance_ptr, add(sload(tkn_balance_ptr), tkn_qty))
                let eth_balance := sload(eth_balance_ptr)
                let eth_change := add(eth_qty, fee)
                if lt(eth_balance, eth_change) {
                  revert(0,0)
                }
                sstore(eth_balance_ptr, sub(eth_balance, eth_change))
              }
            }
          }

          // UPDATE POSITION

          // point into position
          if SETTLE(settlement).allowance_id {
            tkn_position_ptr := add(tkn_position_ptr, 2)
          }

          let current_position := sload(tkn_position_ptr)

          // Update position's total fee
          {
            // Note, don't care about overflow. If we do user will be happy.
            let position_fees := and(add(POSITION(current_position).fees, fee), 0xFFFFFFFF)
            current_position := or(and(current_position, 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000), position_fees)
          }

          let position_is_long := POSITION(current_position).is_long
          // construct signed profit number
          let profit := or(POSITION(current_position).pl_value, POSITION(current_position).is_loss(255))

          let position_eth_qty := POSITION(current_position).eth_qty
          let position_tkn_qty := POSITION(current_position).tkn_qty

          switch eq(position_is_long, is_long)
          // Different direction
          case 0 {

            // If tkn_qty >= position_tkn_qty we closed our position

            switch lt(tkn_qty, position_tkn_qty)
            // Close position
            case 0 {
              // Note, tkn_qty >= position_tkn_qty

              // Quantity in new position
              position_tkn_qty := sub(tkn_qty, position_tkn_qty)

              // Apply profit / loss from closed position

              switch position_is_long
              // SHORT -> LONG
              case 0 {
                // Profit or loss?
                switch lt(position_eth_qty, eth_qty)
                // Purcahsed all tkn back and have left overs
                case 0 {
                  let gross := sub(position_eth_qty, eth_qty)
                  profit := add(profit, gross)
                  position_eth_qty := 0
                }
                // Sold all tkn have more eth than cost
                default {
                  let loss := sub(eth_qty, position_eth_qty)
                  profit := sub(profit, loss)
                  position_eth_qty := 0
                }

                position_is_long := 1
              }
              // LONG -> SHORT
              default {
                // Profit or loss?
                switch lt(position_eth_qty, eth_qty)
                // Sold all tkn have have cost left over
                case 0 {
                  let loss := sub(position_eth_qty, eth_qty)
                  profit := sub(profit, loss)
                  position_eth_qty := 0
                }
                // Sold all tkn have more eth than cost
                default {
                  let gross := sub(eth_qty, position_eth_qty)
                  profit := add(profit, gross)
                  position_eth_qty := 0
                }

                position_is_long := 0
              }
            }
            default {
              position_tkn_qty := sub(position_tkn_qty, tkn_qty)

              switch lt(position_eth_qty, eth_qty)
              case 0 {
                position_eth_qty := sub(position_eth_qty, eth_qty)
              }
              default {
                switch position_is_long
                // SHORT, don't have enough $ when buying back
                case 0 {
                  let loss := sub(eth_qty, position_eth_qty)
                  profit := sub(profit, loss)
                  position_eth_qty := 0
                }
                // LONG, sold and got more $ than started with
                default {
                  let gross := sub(eth_qty, position_eth_qty)
                  profit := add(profit, gross)
                  position_eth_qty := 0
                }
              }
            }
          }
          
          // Same direction
          default {
            position_eth_qty := and(add(position_eth_qty, eth_qty), 0xFFFFFFFFFFFFFFFF)
            position_tkn_qty := and(add(position_tkn_qty, tkn_qty), 0xFFFFFFFFFFFFFFFF)

            // Protect eth and tkn from overflow
            // Note, using "or" because its cheaper than another conditional jump
            if or(lt(position_eth_qty, eth_qty), lt(position_tkn_qty, tkn_qty)) {
              revert(0,0)
            }
          }

          current_position := BUILD_POSITION{
            0,
            position_is_long,
            rshift(profit, 255),
            and(profit, 0xFFFFFFFFFFFFFFFF),
            position_eth_qty,
            position_tkn_qty,
            0
          }

          sstore(tkn_position_ptr, current_position)

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

