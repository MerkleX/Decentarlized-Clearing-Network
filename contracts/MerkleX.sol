pragma solidity ^0.4.23;

import './ERC20.sol';

contract MerkleX {
  // MerkleX's address
  address owner;

  // Store a ring buffer of 2^16 length
  // for all updates to the accounting
  mapping(uint256 => bytes32) updates;

  // Pointers into the updates ring buffer
  uint256 write_head;
  uint256 read_head;

  // Allows us to keep compresed timestamps in trade updates
  uint256 write_timestamp;
  uint256 read_timestamp;

  // Key = 24 session_id | 12 token_id
  mapping(uint256 => uint256) balances;
  mapping(uint256 => address) token_addresses;

  struct Session {
    address owner_address;
    address trade_address;
  }

  mapping(uint256 => Session) sessions;

  function create_session(address trade_address) public {

  }

  function deposit_eth() public {
  }

  /*
     Hex helpers
     0x1 = 0001
     0x2 = 0010
     0x3 = 0011
     0x4 = 0100
     0x6 = 0110
     0x7 = 0111
     0x8 = 1000
     0xB = 1011
     0xC = 1100
     0XE = 1110
     0xF = 1111

     Trade Update

     | attr name     | len | cumlen | description
     |---------------|-----|--------|
     | is_timestamp  |   1 |      1 |
     | is_buy        |   1 |      2 |
     | token_id      |  12 |     14 |
     | state         |   2 |     16 | 0=pending, 1=confirmed, 2=contested, 3=broken
     | timestamp     |   8 |     24 | *100 + read_timestamp
     | taker_session |  24 |     48 |
     | taker_order   |  16 |     64 |

     | mk1_session   |  24 |     88 |
     | mk1_order     |  16 |    104 |

     | mk1_quant_sig |  27 |    131 |
     | mk1_quant_pow |   5 |    136 |
     | mk1_price_sig |  20 |    156 |
     | mk1_price_pow |   4 |    160 |

     | mk2_session   |  24 |    184 |
     | mk2_order     |  16 |    200 |

     | mk2_quant_sig |  27 |    227 |
     | mk2_quant_pow |   5 |    232 |
     | mk2_price_sig |  20 |    252 |
     | mk2_price_pow |   4 |    256 |
   */

  function batch_update(bytes indexes) public {
    // contains data for netting updates
    uint256[256] memory account_ids;
    int256[256] memory balance_changes;

    // stack variables 
    uint256 temp_1;
    uint256 temp_2;
    uint256 temp_3;
    uint256 temp_4;
    uint256 temp_5;

    // track where were we are in indexes
    uint256 index_pos = 0;

    // expensive temps
    uint256[4] memory local_memory; // [read_timestamp, end, pos, market2_id]
    local_memory[0] = read_timestamp;
    local_memory[1] = write_head;

    // Only process as long as we have indexes
    temp_1 = indexes.length / 6;
    temp_2 = read_head;
    if (temp_1 < local_memory[1] - temp_2) {
      local_memory[1] = temp_2 + temp_1;
    }
    local_memory[2] = temp_2;

    for (; local_memory[2] < local_memory[1]; local_memory[2]++) {
      bytes32 entry = updates[local_memory[2]];

      // Handle timestamp updates
      if ((entry & 0x8000000000000000000000000000000000000000000000000000000000000000) != 0) {
        /* read_timestamp */ local_memory[0] = uint256(entry & 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF);
        continue;
      }

      // Ensure trade has a chance to be contested
      if (block.timestamp <= /* read_timestamp */ local_memory[0] + uint256(entry[2]) * 100 + 7200) {
        return;
      }

      // Verify indexes are correctly setup

      // TAKER
      temp_5 = uint256((entry >> 242) & 0xFFF);

      // Verify taker quote account
      temp_3 = uint256((entry >> 192) & 0xFFFFFF0000);
      temp_4 = uint256(indexes[index_pos]);
      if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
      else { require(account_ids[temp_4] == temp_3); }

      // Verify taker base account
      temp_4 = uint256(indexes[index_pos]);
      temp_3 = temp_3 | temp_5;
      if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
      else { require(account_ids[temp_4] == temp_3); }

      // MAKER 1

      // Verify maker 1 quote account
      temp_3 = uint256((entry >> 152) & 0xFFFFFF0000);
      temp_4 = uint256(indexes[index_pos]);
      if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
      else { require(account_ids[temp_4] == temp_3); }

      // Verify maker 1 base account
      temp_4 = uint256(indexes[index_pos]);
      temp_3 = temp_3 | temp_5;
      if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
      else { require(account_ids[temp_4] == temp_3); }

      // MAKER 2

      // Verify maker 2 quote account
      temp_3 = uint256((entry >> 56) & 0xFFFFFF0000);
      local_memory[3] = temp_3;
      temp_4 = uint256(indexes[index_pos]);
      if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
      else { require(account_ids[temp_4] == temp_3); }

      // Verify maker 2 base account
      temp_4 = uint256(indexes[index_pos]);
      temp_3 = temp_3 | temp_5;
      if (account_ids[temp_4] == 0) { account_ids[temp_4] = temp_3; }
      else { require(account_ids[temp_4] == temp_3); }

      // MAKER 1 TRADE

      // quant := temp_1, total := temp_2
      temp_1 = uint256((entry >> 125) & 0x7FFFFFF) * (uint256(10) ** uint256((entry >> 120) & 0x1F));
      temp_2 = uint256((entry >> 100) & 0xFFFFF) * (uint256(10) ** uint256((entry >> 96) & 0xF));
      temp_2 = (temp_1 * temp_2) / 100000000;

      // Verify taker account ids

      temp_4 = (entry & 0x4000000000000000000000000000000000000000000000000000000000000000) == 0 ? uint256(1) : uint256(-1);

      // taker quote balance
      balance_changes[uint256(indexes[index_pos])] += int256(temp_4) * int256(temp_1);
      balance_changes[uint256(indexes[index_pos + 1])] -= int256(temp_4) * int256(temp_2);

      balance_changes[uint256(indexes[index_pos + 2])] -= int256(temp_4) * int256(temp_1);
      balance_changes[uint256(indexes[index_pos + 3])] += int256(temp_4) * int256(temp_2);

      // MAKER 2 TRADE

      if (local_memory[3] != 0) {
        temp_1 = uint256((entry >> 29) & 0x7FFFFFF) * (uint256(10) ** uint256((entry >> 24) & 0x1F));
        temp_2 = uint256((entry >> 4) & 0xFFFFF) * (uint256(10) ** uint256(entry & 0xF));
        temp_2 = (temp_1 * temp_2) / 100000000;

        balance_changes[uint256(indexes[index_pos])] += int256(temp_4) * int256(temp_1);
        balance_changes[uint256(indexes[index_pos + 1])] -= int256(temp_4) * int256(temp_2);

        balance_changes[uint256(indexes[index_pos + 4])] -= int256(temp_4) * int256(temp_1);
        balance_changes[uint256(indexes[index_pos + 5])] += int256(temp_4) * int256(temp_2);
      }

      index_pos += 6;
    }

    // Update balances
    for (temp_1 = 0; temp_1 < 256; ++temp_1) {
      temp_2 = account_ids[temp_1];
      if (temp_2 == 0) {
        break;
      }

      // Balance change
      temp_3 = uint256(balance_changes[temp_1]);

      // Ensure balance can cover delta
      if (int256(temp_3) < 0) {
        assert(balances[temp_2] >= uint256(-int256(temp_3)));
      }

      // Update balance
      balances[temp_2] = uint256(int256(balances[temp_2]) + int256(temp_3));
    }

    if (read_timestamp != local_memory[0]) {
      read_timestamp = local_memory[0];
    }

    read_head = local_memory[2];
  }

  function push_updates(uint256 position, uint8 word_count, bytes32[8] words) public {
    require(msg.sender == owner);
    require(write_head == position);
    require(write_head - read_head < 65538 - word_count);

    uint256 current_time = block.timestamp;
    uint256 time_offset = (current_time - write_timestamp + 99) / 100;

    // Insert a timestamp if offset would overflow
    if (time_offset >= 256) {
      updates[position & 65535] = bytes32(current_time | 
                                          // set is_timestamp
                                          0x8000000000000000000000000000000000000000000000000000000000000000
                                         );

                                         write_timestamp = current_time;
                                         time_offset = 0;
                                         position += 1;
    }

    // Shift value into entry position
    bytes32 timestamp = bytes32(time_offset << 232);

    for (uint8 i = 0; i < word_count; i++) {
      bytes32 word = words[i];
      updates[(position + i) & 65535] = bytes32(
        // 0 1 111111111111 00 00000000 = 0111 1111 1111 1100 0000 0000 = 7 F F C 0 0
        uint256(word) & uint256(0x7FFC00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF)
      ) | timestamp;
    }

    write_head = position + word_count;
  }

  constructor() public {
    owner = msg.sender;
    write_head = 1;
    read_head = 1;
  }
}
