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

  /*
    Trade
    
    * |  1 |   1 | is_timestamp
      |  1 |   2 | is_buy
      | 12 |  14 | token_id
    * |  2 |  16 | state :: 0=pending, 1=confirmed, 2=contested, 3=broken
    * |  8 |  24 | timestamp
    * | 40 |  64 | taker
    * | 40 | 104 | maker_1
    * | 24 | 128 | maker_1_data
    * | 40 | 168 | maker_2
    * | 24 | 192 | maker_2_data
    * | 40 | 232 | maker_3
    * | 24 | 254 | maker_3_data

    Traker / Maker

    | 24 | 24 | session_id
    | 16 | 40 | order_id

    Maker Data

    |  7 |  7 | to_taker_sig
    |  5 | 12 | to_taker_pow
    |  7 | 19 | to_maker_sig
    |  5 | 24 | to_maker_pow
  */

  function process(bytes32 indexes, bytes32[4] trades) public {
    require(msg.sender == owner);

    uint8 batch_size = uint8(indexes[0]);
    
    // Handle special cases
    if (batch_size == 0) {
      require(write_head - read_head > 1);

      // is_timestamp?
      if ((trades[0] & 0x8000000000000000000000000000000000000000000000000000000000000000) == 1) {
        read_timestamp = trades[0] & 0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF;
        read_head += 1;
        return;
      }

      if ((trades[1] & 0x03) == 3) {
        read_head += 1;
        return;
      }

      return;
    }
    
    require(batch_size <= 4);
    require(write_head - read_head > batch_size);

    uint16[16] session_ids;
    uint256[32] balances;

    for (uint8 i = 0; i < batch_size; i++) {
      bytes32 entry = data[(read_head + i) & 65535];

      uint16 token_id = uint16((entry >> (256 - 14)) & 0x3F);

      uint32 taker_id = uint32((entry >> (256 - 48)) & 0xFFFFFF);
      uint32 maker1_id = uint32((entry >> (256 - 88)) & 0xFFFFFF);
      uint32 maker2_id = uint32((entry >> (256 - 152)) & 0xFFFFFF);
      uint32 maker3_id = uint32((entry >> (256 - 216)) & 0xFFFFFF); // TODO

      uint8 index_offset = 1 + i * 2;

      uint8 taker_index = indexes[index_offset];
      uint8 maker1_index = indexes[index_offset + 1];
      uint8 maker2_index = indexes[index_offset + 2];
    }
  }

  function push_data(uint256 position, uint8 word_count, bytes32[8] words) public {
    require (msg.sender == owner);
    require (write_head == position);
    require (write_head - read_head < 65538 - word_count);

    uint256 current_time = block.timestamp;
    uint256 time_offset = (current_time - write_timestamp) / 100;

    // Insert a timestamp if offset would overflow
    if (time_offset >= 256) {
      data[position & 65535] = bytes32(current_time | 
        0x8000000000000000000000000000000000000000000000000000000000000000
      ); // mod for sanity check

      write_timestamp = current_time;
      time_offset = 0;
      position += 1;
    }

    bytes32 timestamp = bytes32(time_offset << 248);

    for (uint8 i = 0; i < word_count; i++) {
      bytes32 word = words[i];
      data[(position + i) & 65535] = bytes32(
          uint256(word) & uint256(0x7FFFFC00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF)
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
