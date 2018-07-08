pragma solidity ^0.4.23;

contract MerkleX {
  address owner;

  uint256 write_head;
  uint256 read_head;

  uint256 write_timestamp;
  uint256 read_timestamp;

  mapping(uint256 => bytes32) updates;
  mapping(uint256 => uint256) balances;

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

  function apply_update() public {
    uint256 pos = read_head;
    require(write_head > pos);

    if (_apply_update()) {
      read_head = pos + 1;
    }
  }

  function _apply_update(uint256 clock) internal returns (bool success) {
    {
      bytes32 entry = updates[read_head & 65535];

      // is_timestamp
      if ((entry & 0x8000000000000000000000000000000000000000000000000000000000000000) != 0) {
        read_timestamp = uint256(entry & 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF);
        return true;
      }

      uint256 timestamp = read_timestamp + uint256(entry[2]) * 100;
      if (clock <= timestamp + 7200) {
        return false;
      }

      // Skip broken trade
      uint8 state = uint8(entry[1] & 0x3);
      if (state == 3) {
        return true;
      }

      if (state == 2) {
        return false;
      }
    }

    uint256[6] memory balance_deltas;

    {
      uint256 quant = uint256((entry >> 125) & 0x7FFFFFF) * (uint256(10) ** uint256((entry >> 120) & 0x1F));
      uint256 price = uint256((entry >> 100) & 0xFFFFF) * (uint256(10) ** uint256((entry >> 96) & 0xF));
      uint256 total = (quant * price) / 100000000;

      balance_deltas[0] = total;
      balance_deltas[2] = total;
      balance_deltas[1] = quant;
      balance_deltas[3] = quant;

      quant = uint256((entry >> 29) & 0x7FFFFFF) * (uint256(10) ** uint256((entry >> 24) & 0x1F));
      price = uint256((entry >> 4) & 0xFFFFF) * (uint256(10) ** uint256(entry & 0xF));
      total = (quant * price) / 100000000;

      balance_deltas[4] = total;
      balance_deltas[0] += total;
      balance_deltas[5] = quant;
      balance_deltas[1] += quant;
    }

    uint256 token_id = uint256((entry >> 242) & 0xFFF);

    // Apply balance updates

    // is sell
    if ((entry & 0x4000000000000000000000000000000000000000000000000000000000000000) == 0) {
      // taker
      uint256 account_index = uint256((entry >> 192) & 0xFFFFFF0000);
      uint256 balance = balances[account_index | token_id];

      assert(balance >= balance_deltas[1]);
      balances[account_index] += balance_deltas[0];
      balances[account_index | token_id] = balance - balance_deltas[1];

      // maker 1
      account_index = uint256((entry >> 152) & 0xFFFFFF0000);
      balance = balances[account_index];

      assert(balance >= balance_deltas[2]);
      balances[account_index] = balance - balance_deltas[2];
      balances[account_index | token_id] += balance_deltas[3];

      // maker 2
      account_index = uint256((entry >> 152) & 0xFFFFFF0000);
      balance = balances[account_index];

      assert(balance >= balance_deltas[4]);
      balances[account_index] = balance - balance_deltas[4];
      balances[account_index | token_id] += balance_deltas[5];
    }
    // is buy
    else {
      // taker
      account_index = uint256((entry >> 192) & 0xFFFFFF0000);
      balance = balances[account_index];

      assert(balance >= balance_deltas[0]);
      balances[account_index] = balance - balance_deltas[0];
      balances[account_index | token_id] += balance_deltas[1];

      // maker 1
      account_index = uint256((entry >> 152) & 0xFFFFFF0000);
      balance = balances[account_index | token_id];

      assert(balance >= balance_deltas[3]);
      balances[account_index] += balance_deltas[2];
      balances[account_index | token_id] = balance - balance_deltas[3];

      // maker 2
      account_index = uint256((entry >> 152) & 0xFFFFFF0000);
      balance = balances[account_index | token_id];

      assert(balance >= balance_deltas[5]);
      balances[account_index] += balance_deltas[4];
      balances[account_index | token_id] = balance - balance_deltas[5];
    }

    return true;
  }

  function _apply_update(uint256 clock) internal returns (bool success) {
    bytes32 entry = updates[read_head & 65535];

    // is_timestamp
    if ((entry & 0x8000000000000000000000000000000000000000000000000000000000000000) != 0) {
      read_timestamp = uint256(entry & 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF);
      return true;
    }

    uint256 token_id = uint256((entry >> 242) & 0xFFF);

    uint256 timestamp = read_timestamp + uint256(entry[2]) * 100;
    if (clock <= timestamp + 7200) {
      return false;
    }

    uint8 state = uint8(entry[1] & 0x3);
    if (state == 3) {
      return true;
    }

    if (state == 2) {
      return false;
    }

    uint256 taker = uint256((entry >> 208) & 0xFFFFFF);
    uint256 maker = uint256((entry >> 168) & 0xFFFFFF);

    uint256 quant = uint256((entry >> 125) & 0x7FFFFFF) * uint256(10 ** uint256((entry >> 120) & 0x1F));
    uint256 price = uint256((entry >> 100) & 0xFFFFF) * uint256(10 ** uint256((entry >> 96) & 0xF));
    uint256 total = (quant * price) / 100000000;

    uint256 taker_weth_index = taker << 16;
    uint256 taker_token_index = taker_weth_index | token_id;

    uint256 maker_weth_index = maker << 16;
    uint256 maker_token_index = maker_weth_index | token_id;

    uint256 taker_weth_balance = balances[taker_weth_index];
    uint256 taker_token_balance = balances[taker_token_index];

    uint256 maker_weth_balance = balances[maker_weth_index];
    uint256 maker_token_balance = balances[maker_token_index];

    // is sell
    if ((entry & 0x4000000000000000000000000000000000000000000000000000000000000000) == 0) {
      if (taker_token_balance < quant || maker_weth_balance < total) {
        return false;
      }

      balances[taker_weth_index] = taker_weth_balance + total;
      balances[taker_token_index] = taker_token_balance - quant;

      balances[maker_weth_index] = maker_weth_balance - total;
      balances[maker_token_index] = maker_token_balance + quant;
    }
    // is buy
    else {
      if (maker_token_balance < quant || taker_weth_balance < total) {
        return false;
      }

      balances[taker_weth_index] = taker_weth_balance - total;
      balances[taker_token_index] = taker_token_balance + quant;

      balances[maker_weth_index] = maker_weth_balance + total;
      balances[maker_token_index] = maker_token_balance - quant;
    }

    return true;
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
