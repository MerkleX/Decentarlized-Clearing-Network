pragma solidity ^0.4.23;

// contracts ERC20 {
//   function transferFrom(address from, address to, uint256 value) public returns (bool);
//   function approve(address spender, uint256 value) public returns (bool);
// }

contract MerkleX {
  uint256 owner;
  uint256 fee_balance;

  /*
     Notes:

     Pos limit can be expanded,
     to make smaller can be done by
     exchange sending signed proof to client
     when expanded, exchange will only accept if nonce
     is higher than one supplied in restrict proof

  */

  /*

    TODO: Need to have update_nonce unique for each position limit
    
     SESSION_DEF {
      user_id           :  32,
      exchange_id       :  32,
      expire_time       :  64,
      max_ether_fees    :  64,
      update_nonce      :  64,
     }

     POS_LIMIT_DEF {
      max_asset_qty     :  28,
      max_asset_qty_pow :   4,

      min_asset_qty     :  28,
      min_asset_qty_pow :   4,

      long_price        :  28,
      long_price_pow    :   4,

      short_price       :  28,
      short_price_pow   :   4,

      ether_qty_shift   :  64,
      asset_qty_shift   :  64,
     }

     POSITION_DEF {
      asset_id          :  16,
      _padding          :  48,
      ether_qty         :  64,
      asset_qty         :  64,
      asset_balance     :  64,
     }

     ETHER_BALANCE_DEF {
      ether_balance     : 64,
      paid_ether_fees   : 64,
     }

     ASSET_ID_DEF {
      asset_id          :  16,
     }

     SESSION_DATA(34 words) {
       0: SESSION
       1: ETHER_BALANCE

       // [position_id * 2 + 0] => position
       // [position_id * 2 + 1] => limit
       2: POSITION
       3: POS_LIMIT
       4: POSITION
       5: POS_LIMIT
       6: POSITION
       7: POS_LIMIT
       8: POSITION
       9: POS_LIMIT
      10: POSITION
      11: POS_LIMIT
      12: POSITION
      13: POS_LIMIT
      14: POSITION
      15: POS_LIMIT
      16: POSITION
      17: POS_LIMIT
      18: POSITION
      19: POS_LIMIT
      20: POSITION
      21: POS_LIMIT
      22: POSITION
      23: POS_LIMIT
      24: POSITION
      25: POS_LIMIT
      26: POSITION
      27: POS_LIMIT
      28: POSITION
      29: POS_LIMIT
      30: POSITION
      31: POS_LIMIT
      32: POSITION
      33: POS_LIMIT
     }

     GROUP_HEADER_DEF {
      asset_id          : 16,
      count             :  8,
     }

     SETTLE_DEF {
      session_id        : 32,
      position_id       :  4,
      ether_fee         : 28,
      ether_qty         : 64,
      asset_qty         : 64,
     }
   */

  uint256[2**32] exchange_addresses;
  uint256[2**32] user_addresses;
  uint256[2**16] asset_addresses;
  uint256[(2**32) * 34] sessions;

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

  function submit_group(bytes data) public {
    // used to verify a group nets to 0
    uint256[1] memory asset_id_ptr;
    uint256[1] memory total_ether_ptr;
    uint256[1] memory total_asset_ptr;
    uint256[1] memory collected_fees_ptr;

    assembly {
      // Very first word is the size of data in bytes. This will
      // create cursor and cursor_end. When cursor reaches cursor_end
      // we will be done processing.

      let cursor_end := mload(data)
      let cursor := add(data, 32)
      cursor_end := add(cursor, cursor_end)

      // Walk through data and process

      for {} lt(cursor, cursor_end) {} {
        // Data is broken up by headers which describe the next section.
        // Load the header and process the entire next section.

        let header := mload(cursor)

        // Point cursor into the data
        cursor := add(cursor, GROUP_HEADER.BYTES)

        // Store the asset_id into memory. This gets us around issues of stack depth.
        let asset_id := GROUP_HEADER(header).asset_id

        let group_end := add(cursor, mul(GROUP_HEADER(header).count, SETTLE.BYTES))

        // Ensure there is enough space for group
        if gt(group_end, cursor_end) {
          stop()
        }

        // Process group item
        for {} lt(cursor, group_end) { cursor := add(cursor, SETTLE.BYTES) } {
          let settlement := mload(cursor)

          // Load position
          let session_ptr := add(sessions, mul(SETTLE(settlement).session_id, /* position size */ 34))
          let position_ptr := add(session_ptr, add(2, mul(SETTLE(settlement).position_id, 2)))
          let position := mload(position_ptr)

          // Settlement is in wrong group (asset ids don't match)
          if not(eq(POSITION(position).asset_id, asset_id)) {
            revert(0, 0)
          }

          let ether_qty := SETTLE(settlement).ether_qty
          let asset_qty := SETTLE(settlement).asset_qty

          // OVERFLOW PROTECTION

          // Check is absolute value is under an overflow threshold.
          // QTY can have a magnituted of 2^63. We want to ensure that
          // QTY * 255 < 2^63, therefore QTY < (2^63/2^8 = 2^55).

          // Binary mask
          //   1    00000000 1111111111111111111111111111111111111111111111111111111
          // [sign]  [*255]                         [magnitude]

          // If gt than we know one of the masked out bits has data

          if gt(ether_qty, and(ether_qty, 0x807FFFFFFFFFFFFF)) {
            revert(0, 0)
          }
          if gt(asset_qty, and(asset_qty, 0x807FFFFFFFFFFFFF)) {
            revert(0, 0)
          }

          // QTY is signed so this add will likely overflow u64. This is
          // fine as that data (the carry) is ingored in 2s complement.
          // We know the actual magnitude does not overflow from the
          // above check.

          mstore(total_ether_ptr, add(mload(total_ether_ptr), ether_qty))
          mstore(total_asset_ptr, add(mload(total_asset_ptr), asset_qty))

          // Safe Two's Complement Add
          let pos_ether_qty := POSITION(position).ether_qty
          {
            let new_ether_qty := add(pos_ether_qty, ether_qty)

            let ether_qty_sign := and(ether_qty, 0x8000000000000000)
            let pos_ether_qty_sign := and(pos_ether_qty, 0x8000000000000000)
            let new_ether_qty_sign := and(new_ether_qty, 0x8000000000000000)
            
            if end(eq(ether_qty_sign, pos_ether_qty_sign), xor(ether_qty_sign, new_ether_qty)) {
              revert(0, 0)
            }

            pos_ether_qty := new_ether_qty
          }

          // Safe Two's Complement Add
          let pos_asset_qty := POSITION(position).asset_qty
          {
            let new_asset_qty := add(pos_asset_qty, asset_qty)

            let asset_qty_sign := and(asset_qty, 0x8000000000000000)
            let pos_asset_qty_sign := and(pos_asset_qty, 0x8000000000000000)
            let new_asset_qty_sign := and(new_asset_qty, 0x8000000000000000)
            
            if end(eq(asset_qty_sign, pos_asset_qty_sign), xor(asset_qty_sign, new_asset_qty)) {
              revert(0, 0)
            }

            pos_asset_qty := new_asset_qty
          }

          let pos_asset_balance := POSITION(position).asset_balance
          pos_asset_balance := add(pos_asset_balance, asset_qty)

          // Did asset balance overflow?
          if gt(pos_asset_balance, 0x7FFFFFFFFFFFFFFF) {
            revert(0, 0)
          }

          // Update the position
          position = BUILD_POSITION(asset_id, 0, pos_ether_qty, pos_asset_qty, pos_asset_balance)
          mstore(position_ptr, position)

          // Update ETHER balance

          let ether_balance_ptr := add(session_ptr, 1)
          let ether_balance := sload(ether_balance_ptr)

          let pos_ether_balance := ETHER_BALANCE(ether_balance).ether_balance
          pos_ether_balance := add(pos_ether_balance, ether_qty)

          // Did dcn balance overflow?
          if gt(pos_ether_balance, 0x7FFFFFFFFFFFFFFF) {
            revert(0, 0)
          }

          let paid_ether_fees := ETHER_BALANCE(ether_balance).paid_ether_fees
          paid_ether_fees := add(paid_ether_fees, SESSION(settlement).ether_fee)

          // Note, not going to check for overflow on fees because it would take
          // 2^64/2^29=2^35=~34M updates before we could overflow. To protect just
          // make the limit under 2^64-2^29.

          ether_balance := BUILD_ETHER_BALANCE(pos_ether_balance, paid_ether_fees)
          sstore(ether_balance_ptr, ether_balance)

          // Check and update session

          let session := sload(session_ptr)

          // Ensure submitter is authorized
          let exchange_address := sload(add(exchange_addresses, SESSION(session).exchange_id))
          if not(eq(exchange_addresses, address)) {
            revert(0, 0)
          }

          // Ensure session did not expire
          if gt(mul(SESSION(session).expire_time, 100), timestamp) {
            revert(0, 0)
          }

          // Collected too much fees
          if gt(paid_ether_fees, SESSION(session).max_ether_fees) {
            revert(0, 0)
          }

          // CHECK POSITION FITS IN LIMITS

          let position_limit := sload(add(position_ptr, 1))

          switch gt(pos_asset_qty, 0x7FFFFFFFFFFFFFFF)
          // 
          case 0 {
          }
        }
      }
    }
  }
}

