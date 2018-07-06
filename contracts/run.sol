pragma solidity ^0.4.23;

contract MerkleX {
  uint64 constant PENDING_TRADE_BLOCKS = 1440; // ~5 hours

  mapping(uint16 => address) token_addresses;
  mapping(uint32 => address) user_sessions;
  mapping(uint32 => mapping(uint16 => uint256)) balances;
  mapping(uint256 => bytes32) data;

  uint256 write_timestamp;
  uint256 read_timestamp;

  uint256 write_head;
  uint256 read_head;

  address owner;

  function process(uint256 max_steps) public {
    // Update locally then apply to storage
    mapping(uint32 => mapping(uint16 => uint256)) balances memory;
    mapping(uint32 => uint8) visted memory;
    uint32[] modified_accounts memory;

    uint256 now = block.timestamp;
    uint32 taker_account_id;
    uint32 maker_account_id;
    uint256 pos = read_head;

    while (pos < write_head && max_steps > 0) {
      max_steps -= 1;

      bytes32 entry = data[pos & 65535];

      uint8 type = entry[0] & 0xC0;

      // timestamp
      if (type == 0) {
        read_timestamp = entry;
        pos += 1;
        continue;
      }

      // make sure we got a trade here
      assert(type == 64);

      uint16 state = entry[1] & 0x03;

      // broken
      if (state == 3) {
        pos += 1;
        while (pos < write_head) {
          if (data[pos & 65535][0] & 0xC0 == 64) {
            break;
          }
          pos += 1;
        }
        continue;
      }

      // cannot process contested trades
      if (state == 2) {
        break;
      }

      uint256 time = read_timestamp + entry[2] * 100;

      // trade must remain pending
      if (time + PENDING_TRADE_BLOCKS > now) {
        break;
      }

      uint16 token_id = uint16(
        ((entry[0] & 0x3F) << 5) | ((entry[1] & 0xF8) >> 3)
      );
    }
  }

  /*
    Trade
    
    |  1 |   1 | is_trade
    |  1 |   2 | is_buy
    | 12 |  22 | token_id
    |  2 |  24 | state
    |  8 |   8 | timestamp
    | 40 |  64 | taker
    | 40 | 104 | maker_1
    | 24 | 128 | maker_1_data
    | 40 | 168 | maker_2
    | 24 | 192 | maker_2_data
    | 40 | 232 | maker_3
    | 24 | 254 | maker_3_data

    Traker / Maker

    | 24 | 24 | session_id
    | 16 | 40 | order_id

    Maker Data

    |  7 |  7 | to_taker_sig
    |  5 | 12 | to_taker_pow
    |  7 | 19 | to_maker_sig
    |  5 | 24 | to_maker_pow
  */

  function push_data(uint256 position, uint8 word_count, bytes32[7] words) public {
    require (msg.sender == owner);
    require (write_head == position);
    require (write_head - read_head < 65538 - word_count);
    require (words[0][0] & 0xC0 == 64);

    uint256 current_time = block.timestamp;
    uint256 time_offset = (current_time - write_timestamp) / 100;

    // Insert a timestamp if offset would overflow
    if (time_offset >= 256) {
      data[position & 65535] = bytes32(current_time & uint256(
        0x3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
      )); // mod for sanity check

      write_timestamp = current_time;
      time_offset = 0;
      position += 1;
    }

    uint256 timestamp = time_offset << 232;

    for (uint8 i = 0; i < word_count; i++) {
      bytes32 word = words[i];

      // is trade
      if (word[0] & 0xC0 == 64) {
        // state is pending
        require(word[1] & 3 == 0);

        // set timestamp_offset
        word = bytes32(uint256(
          word & 
          0xFFFF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
        ) | timestamp);

        words[i] = word;
      }
      else {
        // is trade_continue
        require(word[0] & 0xCO == 128);
      }
    }

    for (i = 0; i < word_count; i++) {
      data[position & 65535] = words[i];
      position += 1;
    }

    write_head = position;
  }

  constructor() public {
    owner = msg.sender;
    write_head = 1;
    read_head = 1;
  }
}
