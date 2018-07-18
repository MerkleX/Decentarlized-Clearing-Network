pragma solidity ^0.4.23;

contract ERC20 {
  function transferFrom(address from, address to, uint256 value) public returns (bool);
  function approve(address spender, uint256 value) public returns (bool);
}

contract MerkleX {
  struct User {
    uint256 owner_address;
    uint256 trade_address;
  }

  struct Settings {
    uint256 trade_fee;
    uint256 user_create_fee;
  }

  uint256 owner;
  uint256 fee_balance;

  // State
  bytes32[2**16] updates;
  uint256[2**(24+12)] balances;
  uint256[2**12] token_addresses;
  User[2**24] users;
  uint256 next_user_pointer;

  // Pointers into the updates ring buffer
  uint256 write_head;
  uint256 read_head;

  // Allows us to keep compresed timestamps in trade updates
  uint256 write_timestamp;
  uint256 read_timestamp;

  // Manage settings and updating
  Settings current_settings;
  Settings next_settings;
  uint256 next_settings_unlock;

  constructor() public {
    assembly {
      sstore(owner_slot, address)
      sstore(next_user_pointer_slot, 64)
    }
  }

  function create_user(address trade_address) public payable returns (uint256) {
    assembly {
      // Last valid pointer: (2^24 - 1) * 32 = 536870880
      let current_user_pointer := sload(next_user_pointer_slot)
      if gt(current_user_pointer, 536870880) {
        revert(0,0)
      }
      
      // Does caller have required fee
      let user_create_fee := sload(add(current_settings_slot, 32))
      if not(eq(callvalue, user_create_fee)) {
        revert(0,0)
      }

      // Store new user data
      sstore(add(users_slot, current_user_pointer), address)
      sstore(add(users_slot, add(current_user_pointer, 32)), trade_address)
      sstore(next_user_pointer_slot, add(current_user_pointer, 64))
      
      // Collect fee
      if gt(user_create_fee, 0) {
          sstore(fee_balance_slot, add(sload(fee_balance_slot), user_create_fee))
      }

      // return user id
      let return_idx := add(mload(0x40), 32)
      mstore(return_idx, div(current_user_pointer, 64))
      return(return_idx, 32)
    }
  }

  function deposit_eth(uint256 user_id) public payable {
    assembly {
      // Validate user_id
      if gt(user_id, 16777215) {
        stop()
      }

      // Make sure depositor is owner
      let user_owner := sload(add(users_slot, mul(user_id, 64)))
      if not(eq(user_owner, address)) {
        stop()
      }

      let idx := add(balances_slot, mul(user_id, 4096))
      let eth_balance := sload(idx)
      sstore(idx, add(eth_balance, callvalue))
    }
  }

  function deposit_token(uint256 user_id, uint256 token_id, uint256 quantity) public returns (bool) {
    // Validation
    assembly {
      // Validate user_id
      if gt(user_id, 16777215) {
        stop()
      }

      // Validate token_id
      if gt(token_id, 4095) {
        stop()
      }

      // Make sure depositor is owner
      let user_owner := sload(add(users_slot, mul(user_id, 64)))
      if not(eq(user_owner, address)) {
        stop()
      }
    }

    ERC20 token = ERC20(token_addresses[token_id]);
    if (token.transferFrom(msg.sender, this, quantity)) {
      uint256 idx = (user_id << 12) | token_id;
      balances[idx] += quantity;
      return true;
    }

    return false;
  }


  /*
     TRADE_DEF {
       is_timestamp  :   1,
       is_buy        :   1,
       token_id      :  12,
       state         :   2,
       timestamp     :   8,
       taker_user :  24,
       taker_order   :  16,

       mk1_user   :  24,
       mk1_order     :  16,

       mk1_quant_sig :  27,
       mk1_quant_pow :   5,
       mk1_price_sig :  20,
       mk1_price_pow :   4,

       mk2_user   :  24,
       mk2_order     :  16,

       mk2_quant_sig :  27,
       mk2_quant_pow :   5,
       mk2_price_sig :  20,
       mk2_price_pow :   4,
     }
   */

  function batch_update(bytes indexes) public {
    // contains data for netting updates
    uint256[256] memory account_ids;
    uint256[256] memory balance_changes;

    assembly {
      // load timestamp
      let local_timestamp := sload(read_timestamp_slot)

      let pos := sload(read_head_slot)
      let end := sload(write_head_slot)

      // Adjust end for number of indexes we have
      {
        let max_trades := div(calldataload(0), 6)
        if lt(max_trades, sub(end, pos)) {
          end := add(pos, max_trades)
        }
      }

      let index_pos := 1

      // calculate balance changes
      for {} lt(pos, end) { pos := add(pos, 1) } {
        let entry := sload(add(updates_slot, mul(and(pos, 65535), 32)))

        switch and(entry, 0x8000000000000000000000000000000000000000000000000000000000000000)
        case 0 {
          // Ensure trade has a chance to be contested
          {
            let entry_timestamp := add(mul(TRADE(entry, timestamp), 100), 7200)
            if gt(entry_timestamp, local_timestamp) {
              stop()
            }
          }

          // Verify indexes are correctly setup
          let token_id := TRADE(entry, token_id)

          // Taker
          {
            let account_id := TRADE(entry, taker_user, 12)

            // QUOTE
            {
              let account_id_pointer := add(account_ids, mul(mod(calldataload(index_pos), 0xff), 32))
              let saved_account_id := mload(account_id_pointer)

              switch saved_account_id
              case 0 { mstore(account_ids, account_id) }
              default {
                if not(eq(saved_account_id, account_id)) {
                  stop()
                }
              }
            }

            // BASE
            {
              account_id := or(account_id, token_id)
              let account_id_pointer := add(account_ids, mul(mod(calldataload(add(index_pos, 1)), 0xff), 32))
              let saved_account_id := mload(account_id_pointer)

              switch saved_account_id
              case 0 { mstore(account_ids, account_id) }
              default {
                if not(eq(saved_account_id, account_id)) {
                  stop()
                }
              }
            }
          }

          // Maker1
          {
            let account_id := TRADE(entry, mk1_user, 12)

            // QUOTE
            {
              let account_id_pointer := add(account_ids, mul(mod(calldataload(add(index_pos, 2)), 0xff), 32))
              let saved_account_id := mload(account_id_pointer)

              switch saved_account_id
              case 0 { mstore(account_ids, account_id) }
              default {
                if not(eq(saved_account_id, account_id)) {
                  stop()
                }
              }
            }

            // BASE
            {
              account_id := or(account_id, token_id)
              let account_id_pointer := add(account_ids, mul(mod(calldataload(add(index_pos, 3)), 0xff), 32))
              let saved_account_id := mload(account_id_pointer)

              switch saved_account_id
              case 0 { mstore(account_ids, account_id) }
              default {
                if not(eq(saved_account_id, account_id)) {
                  stop()
                }
              }
            }
          }

          // Maker2
          {
            let account_id := TRADE(entry, mk2_user, 12)

            if account_id {
              // QUOTE
              {
                let account_id_pointer := add(account_ids, mul(mod(calldataload(add(index_pos, 4)), 0xff), 32))
                let saved_account_id := mload(account_id_pointer)

                switch saved_account_id
                case 0 { mstore(account_ids, account_id) }
                default {
                  if not(eq(saved_account_id, account_id)) {
                    stop()
                  }
                }
              }

              // BASE
              {
                account_id := or(account_id, token_id)
                let account_id_pointer := add(account_ids, mul(mod(calldataload(add(index_pos, 5)), 0xff), 32))
                let saved_account_id := mload(account_id_pointer)

                switch saved_account_id
                case 0 { mstore(account_ids, account_id) }
                default {
                  if not(eq(saved_account_id, account_id)) {
                    stop()
                  }
                }
              }
            }
          }

          // Update changes
          let is_buy := TRADE(entry, is_buy)

          {
            let quantity := mul(TRADE(entry, mk1_quant_sig), exp(10, TRADE(entry, mk1_quant_pow)))
            let price := mul(TRADE(entry, mk1_price_sig), exp(10, TRADE(entry, mk1_price_pow)))
            let total := div(mul(quantity, price), 100000000)
          }
        }
        default {
          local_timestamp := and(entry, 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF)
        }
      }
    }

    // for (; local_memory[2] < local_memory[1]; local_memory[2]++) {
    //   bytes32 entry = updates[local_memory[2]];

    //   // Handle timestamp updates
    //   if ((entry & 0x8000000000000000000000000000000000000000000000000000000000000000) != 0) {
    //     /* read_timestamp */ local_memory[0] = uint256(entry & 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF);
    //     continue;
    //   }

    //   // Ensure trade has a chance to be contested
    //   if (block.timestamp <= /* read_timestamp */ local_memory[0] + uint256(entry[2]) * 100 + 7200) {
    //     return;
    //   }

    //   // Verify indexes are correctly setup

    //   // TAKER
    //   temp_5 = uint256((entry >> 242) & 0xFFF);

    //   // Verify taker quote account
    //   temp_3 = uint256((entry >> 192) & 0xFFFFFF0000);
    //   temp_4 = uint256(indexes[index_pos]);
    //   if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
    //   else { require(account_ids[temp_4] == temp_3); }

    //   // Verify taker base account
    //   temp_4 = uint256(indexes[index_pos]);
    //   temp_3 = temp_3 | temp_5;
    //   if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
    //   else { require(account_ids[temp_4] == temp_3); }

    //   // MAKER 1

    //   // Verify maker 1 quote account
    //   temp_3 = uint256((entry >> 152) & 0xFFFFFF0000);
    //   temp_4 = uint256(indexes[index_pos]);
    //   if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
    //   else { require(account_ids[temp_4] == temp_3); }

    //   // Verify maker 1 base account
    //   temp_4 = uint256(indexes[index_pos]);
    //   temp_3 = temp_3 | temp_5;
    //   if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
    //   else { require(account_ids[temp_4] == temp_3); }

    //   // MAKER 2

    //   // Verify maker 2 quote account
    //   temp_3 = uint256((entry >> 56) & 0xFFFFFF0000);
    //   local_memory[3] = temp_3;
    //   temp_4 = uint256(indexes[index_pos]);
    //   if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
    //   else { require(account_ids[temp_4] == temp_3); }

    //   // Verify maker 2 base account
    //   temp_4 = uint256(indexes[index_pos]);
    //   temp_3 = temp_3 | temp_5;
    //   if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
    //   else { require(account_ids[temp_4] == temp_3); }

    //   // MAKER 1 TRADE

    //   // quant := temp_1, total := temp_2
    //   temp_1 = uint256((entry >> 125) & 0x7FFFFFF) * (uint256(10) ** uint256((entry >> 120) & 0x1F));
    //   temp_2 = uint256((entry >> 100) & 0xFFFFF) * (uint256(10) ** uint256((entry >> 96) & 0xF));
    //   temp_2 = (temp_1 * temp_2) / 100000000;

    //   // Verify taker account ids

    //   temp_4 = (entry & 0x4000000000000000000000000000000000000000000000000000000000000000) == 0 ? uint256(1) : uint256(-1);

    //   // taker quote balance
    //   balance_changes[uint256(indexes[index_pos])] += int256(temp_4) * int256(temp_1);
    //   balance_changes[uint256(indexes[index_pos + 1])] -= int256(temp_4) * int256(temp_2);

    //   balance_changes[uint256(indexes[index_pos + 2])] -= int256(temp_4) * int256(temp_1);
    //   balance_changes[uint256(indexes[index_pos + 3])] += int256(temp_4) * int256(temp_2);

    //   // MAKER 2 TRADE

    //   if (local_memory[3] != 0) {
    //     temp_1 = uint256((entry >> 29) & 0x7FFFFFF) * (uint256(10) ** uint256((entry >> 24) & 0x1F));
    //     temp_2 = uint256((entry >> 4) & 0xFFFFF) * (uint256(10) ** uint256(entry & 0xF));
    //     temp_2 = (temp_1 * temp_2) / 100000000;

    //     balance_changes[uint256(indexes[index_pos])] += int256(temp_4) * int256(temp_1);
    //     balance_changes[uint256(indexes[index_pos + 1])] -= int256(temp_4) * int256(temp_2);

    //     balance_changes[uint256(indexes[index_pos + 4])] -= int256(temp_4) * int256(temp_1);
    //     balance_changes[uint256(indexes[index_pos + 5])] += int256(temp_4) * int256(temp_2);
    //   }

    //   index_pos += 6;
    // }

    // // Update balances
    // for (temp_1 = 0; temp_1 < 256; ++temp_1) {
    //   temp_2 = account_ids[temp_1];
    //   if (temp_2 == 0) {
    //     break;
    //   }

    //   // Balance change
    //   temp_3 = uint256(balance_changes[temp_1]);

    //   // Ensure balance can cover delta
    //   if (int256(temp_3) < 0) {
    //     assert(balances[temp_2] >= uint256(-int256(temp_3)));
    //   }

    //   // Update balance
    //   balances[temp_2] = uint256(int256(balances[temp_2]) + int256(temp_3));
    // }

    // if (read_timestamp != local_memory[0]) {
    //   read_timestamp = local_memory[0];
    // }

    // read_head = local_memory[2];
  }

//  function push_updates(uint256 position, uint8 word_count, bytes32[8] words) public {
//    require(msg.sender == owner);
//    require(write_head == position);
//    require(write_head - read_head < 65538 - word_count);
//
//    uint256 current_time = block.timestamp;
//    uint256 time_offset = (current_time - write_timestamp + 99) / 100;
//
//    // Insert a timestamp if offset would overflow
//    if (time_offset >= 256) {
//      updates[position & 65535] = bytes32(current_time | 
//                                          // set is_timestamp
//                                          0x8000000000000000000000000000000000000000000000000000000000000000
//                                         );
//
//                                         write_timestamp = current_time;
//                                         time_offset = 0;
//                                         position += 1;
//    }
//
//    // Shift value into entry position
//    bytes32 timestamp = bytes32(time_offset << 232);
//
//    for (uint8 i = 0; i < word_count; i++) {
//      bytes32 word = words[i];
//      updates[(position + i) & 65535] = bytes32(
//        // 0 1 111111111111 00 00000000 = 0111 1111 1111 1100 0000 0000 = 7 F F C 0 0
//        uint256(word) & uint256(0x7FFC00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF)
//      ) | timestamp;
//    }
//
//    write_head = position + word_count;
//  }
}
