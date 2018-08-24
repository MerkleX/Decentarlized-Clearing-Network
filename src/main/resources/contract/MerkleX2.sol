pragma solidity ^0.4.23;

// contract ERC20 {
//   function transferFrom(address from, address to, uint256 value) public returns (bool);
//   function approve(address spender, uint256 value) public returns (bool);
// }

contract MerkleX {
  uint256 owner;
  uint256 fee_balance;

  /*
     POS_LIMIT_DEF {
      qty              :  64,
      price            :  28,
      price_pow        :   4,
      expire_time      :  32,
      max_eth_loss     :  64,
      max_tkn_loss     :  64,
     }

     POSITION_DEF {
      _padding         :  30,
      is_long          :   1,
      is_loss          :   1,
      pnl_value        :  64,
      eth_qty          :  64,
      tkn_qty          :  64,
      fees             :  32,
     }
   */

  /*
  struct UserPosition {
    uint256 position_1;
    uint256 limit_1_long;
    uint256 limit_1_short;

    uint256 position_2;
    uint256 limit_2_long;
    uint256 limit_2_short;

    uint256 balance;
    uint256 _padding_1;
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

//  function set_position_limit(uint64 long_qty, uint32 long_price, uint8 long_price_pow, 

  function set_balance(uint32 user_id, uint16 token_id, uint64 new_balance) public {
    assembly {
      let ptr := add(user_positions_slot, add(or(mul(user_id, 524288), mul(token_id, 8)), 6))
      sstore(ptr, add(sload(ptr), new_balance))
    }
  }

  function get_balance(uint32 user_id, uint16 token_id) public constant returns (uint256) {
    uint256[1] memory return_value;
    assembly {
      let ptr := add(user_positions_slot, add(or(mul(user_id, 524288), mul(token_id, 8)), 6))
      mstore(return_value, sload(ptr))
      return(return_value, 32)
    }
  }

  /*
     GET_POS_RETURN_DEF {
      is_long :  8,
      is_loss :  8,
      pnl     : 64,
      eth_qty : 64,
      tkn_qty : 64,
      fees    : 32,
     }
  */

  function get_position(uint32 user_id, uint16 token_id, bool first) public constant
  returns (uint8 is_long, uint8 is_loss, uint64 pnl, uint64 eth_qty, uint64 tkn_qty, uint32 fees) {
    assembly {
      let ptr := add(user_positions_slot, or(mul(user_id, 524288), mul(token_id, 8)))
      if iszero(first) {
        ptr := add(ptr, 2)
      }

      let data := sload(ptr)

      is_long := POSITION(data).is_long
      is_loss := POSITION(data).is_loss
      pnl := POSITION(data).pnl_value
      eth_qty := POSITION(data).eth_qty
      tkn_qty := POSITION(data).tkn_qty
      fees := POSITION(data).fees
    }
  }

  /*
     WINDOW_HDR_DEF {
      token_id     : 16,
      count        :  8,
     }

     SETTLE_DEF {
      is_long      :  1,
      limit_id     :  1,
      _padding     :  6,

      user_id      : 32,

      eth_qty      : 64,
      tkn_qty      : 64,

      fee          : 32,
     }

     TMP_PROFIT_DEF {
      sign : 1,
      overflow : 191,
      data : 64,
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
            eth_balance_ptr := add(eth_balance_ptr, 6)

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

                let tkn_balance_ptr := add(tkn_position_ptr, 6)
                let tkn_balance := sload(tkn_balance_ptr)

                // make sure user has enough token
                if lt(tkn_balance, tkn_qty) {
                  revert(0,0)
                }
                sstore(tkn_balance_ptr, sub(tkn_balance, tkn_qty))
              }
            }
            default {
              let tkn_balance_ptr := add(tkn_position_ptr, 6)
              sstore(tkn_balance_ptr, add(sload(tkn_balance_ptr), tkn_qty))
              let eth_balance := sload(eth_balance_ptr)
              let eth_change := add(eth_qty, fee)
              if lt(eth_balance, eth_change) {
                revert(0,0)
              }
              sstore(eth_balance_ptr, sub(eth_balance, eth_change))
            }
          }

          // UPDATE POSITION

          // point into position
          let limit_id := SETTLE(settlement).limit_id
          if limit_id {
            tkn_position_ptr := add(tkn_position_ptr, 3)
          }

          let current_position := sload(tkn_position_ptr)

          // Update position's total fee
          {
            // Note, don't care about overflow. If we do user will be happy.
            // TODO: store in memory, better than all of these ORs and ANDs if cannot fit on stack
            let position_fees := and(add(POSITION(current_position).fees, fee), 0xFFFFFFFF)
            current_position := or(and(current_position, 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000), position_fees)
          }

          let position_is_long := POSITION(current_position).is_long
          let position_eth_qty := POSITION(current_position).eth_qty
          let position_tkn_qty := POSITION(current_position).tkn_qty

          // Tracked from position_is_long perspective
          let profit := 0

          switch eq(position_is_long, is_long)
          /* if (position_is_long != is_long) */ case 0 {

            switch lt(tkn_qty, position_tkn_qty)
            /* if (tkn_qty >= position_tkn_qty) */ case 0 {
              // Quantity in new position
              position_tkn_qty := sub(tkn_qty, position_tkn_qty)
              position_is_long := and(not(position_is_long), 0x1)

              switch lt(eth_qty, position_eth_qty)
              // Purcahsed all tkn back and have left overs
              /* if (eth_qty >= position_eth_qty) { */ case 0 {
                // New position is larger so we can swing over to it
                // after decrementing by our current position
                position_eth_qty := sub(eth_qty, position_eth_qty)
              }
              // Decrease position size in purchase amount for long
              default {
                // Old position is too large, need to log into profit/loss

                // position_eth_qty = cost to open position
                // eth_qty = $ from selling tkn
                // profit received from trade: eth_qty - position_eth_qty
                // Note, opposite for short position
                profit := sub(eth_qty, position_eth_qty)
              }
            }
            default {
              // Decrease position
              position_tkn_qty := sub(position_tkn_qty, tkn_qty)

              if lt(position_eth_qty, eth_qty) {
                profit := sub(eth_qty, position_eth_qty)
                position_eth_qty := 0
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

          if is_long {
            profit := sub(0, profit)
          }
          profit := add(or(POSITION(current_position).is_loss(255), POSITION(current_position).pnl_value), profit)

          if TMP_PROFIT(profit).overflow {
            revert(0,0)
          }

          current_position := BUILD_POSITION {
            0,
            position_is_long,
            TMP_PROFIT(profit).sign,
            TMP_PROFIT(profit).data,
            position_eth_qty,
            position_tkn_qty,
            POSITION(current_position).fees
          }

          sstore(tkn_position_ptr, current_position)

          let position_limit := tkn_position_ptr
          {
            switch is_long
            case 0 {
              position_limit := add(position_limit, 2)
            }
            default {
              position_limit := add(position_limit, 1)
            }
            position_limit := sload(position_limit)
          }

          // Ensure position size is under limit
          if gt(position_tkn_qty, POS_LIMIT(position_limit).qty) {
            revert(0, 0)
          }

          switch is_long
          case 0 {
            let max_loss := POS_LIMIT(position_limit).max_eth_loss
            if gt(position_eth_qty, max_loss) {
              position_eth_qty := sub(position_eth_qty, max_loss)

              let pos_price := div(
                mul(position_tkn_qty, exp(10, POS_LIMIT(position_limit).price_pow)),
                position_eth_qty
              )
              if gt(pos_price, POS_LIMIT(position_limit).price) {
                revert(0, 0)
              }
            }
          }
          default {
            let max_loss := POS_LIMIT(position_limit).max_tkn_loss
            if gt(position_tkn_qty, max_loss) {
              position_tkn_qty := sub(position_tkn_qty, max_loss)

              let pos_price := div(
                mul(position_tkn_qty, exp(10, POS_LIMIT(position_limit).price_pow)),
                position_eth_qty
              )
              if lt(pos_price, POS_LIMIT(position_limit).price) {
                revert(0, 0)
              }
            }
          }

          if gt(mul(POS_LIMIT(position_limit).expire_time, 1000), timestamp) {
            revert(0, 0)
          }
        }

        // Ensure group nets to 0

        if not(eq(mload(total_eth_ptr), mload(add(total_eth_ptr, 32)))) {
          revert(0, 0)
        }

        if not(eq(mload(total_tkn_ptr), mload(add(total_tkn_ptr, 32)))) {
          revert(0, 0)
        }
      }
    }
  }
}

