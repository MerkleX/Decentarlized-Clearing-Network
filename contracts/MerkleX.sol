pragma solidity ^0.4.23;

contract MerkleX {
  address owner;

  uint256 write_head;
  uint256 read_head;

  uint256 write_timestamp;
  uint256 read_timestamp;

  mapping(uint256 => bytes32) updates;

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
   */

  function apply_updates(bytes32 indexes, bytes32[4] data) public {
    require(msg.sender == owner);

    uint8 data_len = uint8(indexes[31]);
    bytes32 current_update = updates[read_head];

    // Special case to process timestamp or broken trades
    if (data_len == 0) {
      require(write_head > read_head);

      // jump over broken trade
      if ((current_update & 0x8000000000000000000000000000000000000000000000000000000000000000) == 0) {
        // 0 0 000000000000 11 = 0000 0000 0000 0011
        require((current_update & 0x0003000000000000000000000000000000000000000000000000000000000000) ==
                0x0003000000000000000000000000000000000000000000000000000000000000);
        read_head += 1;
        return;
      }

      // apply timestamp
      read_timestamp = uint256(current_update & 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF);
      read_head += 1;
      return;
    }

    // Can only apply trades
    require((current_update & 0x8000000000000000000000000000000000000000000000000000000000000000) != 0);

    // Match for trade and token_id
    // 1 1 111111111111 = 1111 1111 1111 1100
    bytes32 same_type_check = current_update & 0xFFFC00000000000000000000000000000000000000000000000000000000000;
    uint256 pos = read_head;

    uint32[16] session_ids;
    uint256[32] balance_deltas;

    uint256 process_time_thres = block.timestamp + 10000;

    uint8 i = 0;
    while (true) {
      // Update balances

      // Make sure trade has had long enough time to be contested
      uint256 timestamp = read_timestamp + uint256(current_update >> 232) * 100;
      require(timestamp > process_time_thres);

      // Make sure state is such that we can process, either
      // 0 = pending, or
      // 1 = confirmed
      require((current_update[1] & 0x3) <= 1);

      uint8 index_offset = i * 4;

      uint8 taker_index = uint8(indexes[index_offset]);
      uint32 taker_session = uint32((current_update >> 208) & 0x000000FFFFFF);
      if (session_ids[taker_index] == 0) {
        session_ids[taker_index] = taker_session;
      }
      else {
        require(session_ids[taker_index] == taker_session);
      }

      for (uint m = 0; m < 3; m++) {
        uint8 maker_index = uint8(indexes[index_offset + m]);
        uint256 shift = 128 + (m + 1) * 40;

        uint32 maker_session = uint32((current_update >> shift) & 0x000000FFFFFF);

        // no data for maker
        if (maker_session == 0) {
          continue;
        }

        if (session_ids[maker_index] == 0) {
          session_ids[maker_index] = maker_session;
        }
        else {
          require(session_ids[maker_index] == maker_session);
        }

        // balance_deltas[taker_index * 2] += data[i];
        // balance_deltas[taker_index * 2 + 1] += data[i];
      }

      // Progress to next
      i += 1;
      if (i >= data_len) {
        break;
      }

      pos += 1;
      current_update = updates[pos];
      require((current_update & 0xBFFC00000000000000000000000000000000000000000000000000000000000) == same_type_check);
    }
  }

  function push_updates(uint256 position, uint8 word_count, bytes32[8] words) public {
    require(msg.sender == owner);
    require(write_head == position);
    require(write_head - read_head < 65538 - word_count);

    uint256 current_time = block.timestamp;
    uint256 time_offset = (current_time - write_timestamp) / 100;

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
