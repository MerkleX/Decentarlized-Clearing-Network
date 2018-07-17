pragma solidity ^0.4.23;

import './ERC20.sol';

contract MerkleX {
  enum State : bit_2 {
    PENDING   = 0,
    CONFIRMED = 1,
    CONTESTED = 2,
    BROKEN    = 3,
  }

  type SessionId = bit_24;
  type OrderId = bit_16;
  type TokenId = bit_12;
  type Timestamp = bit_8;

  struct Party : bit_40 {
    SessionId   session_id;
    OrderId     order_id;
  }

  struct Quantity : bit_32 {
    bit_27    sig_figs;
    bit_5     power;

    inline function value() returns uint {
      return uint(sig_figs) * (10 ** uint(power));
    }
  }

  struct Price : bit_24 {
    bit_20  sig_figs;
    bit_4   power;

    inline function value() returns uint {
      return uint(sig_figs) * (10 ** (8 + uint(power)));
    }
  }

  struct Order : bit_96 {
    Party     part;
    Quantity  quantity;
    Price     price;
  }

  struct Trade : bit_256 {
    bit_bool  is_timestamp;
    bit_bool  is_buy;
    TokenId   token_id;
    State     state;
    Timestamp timestamp;
    Party     taker;
    Order     maker_1;
    Order     maker_2;
  }

  struct TimeUpdate : bit_256 {
    bit_bool  is_timestamp;
    bit_255   timestamp;

    inline function set(uint timestamp) {
      this = timestamp | 0x8000000000000000000000000000000000000000000000000000000000000000;
    }
  }

  struct BalanceId : bit_36 {
    bit_24 session_id;
    bit_12 token_id;
  }

  // MerkleX's address
  address owner;

  // Store a ring buffer of 2^16 length
  // for all updates to the accounting
  mapping(uint => bit_256) updates;

  // Pointers into the updates ring buffer
  uint write_head;
  uint read_head;

  // Allows us to keep compresed timestamps in trade updates
  uint write_timestamp;
  uint read_timestamp;

  mapping(BalanceId => uint) balances;
  mapping(TokenId => address) token_addresses;

  struct Session {
    address owner_address;
    address trade_address;
    uint end_time;
  }

  mapping(uint => Session) sessions;

  function batch_update(bytes indexes) public {
    // contains data for netting updates
    BalanceId[256]  balance_ids;
    int[256]        balance_changes;

    // track where were we are in indexes
    uint index_pos = 0;
    uint local_read_timestamp = read_timestamp;
    uint end = write_head;
    uint pos = read_head;

    // Only process as long as we have indexes
    uint index_groups = indexes.length / 6;
    if (index_groups < end - pos) {
      end = pos + index_groups;
    }

    for (; pos < end; pos++) {
      bytes32 update = updates[pos & 65535];

      // Handle timestamp updates
      const TimeUpdate &time_update = update;
      if (time_update.is_timestamp) {
        local_read_timestamp = time_update.timestamp;
        continue;
      }

      const Trade &trade = update;

      // Ensure trade has a chance to be contested
      if (block.timestamp <= local_read_timestamp + trade.timestamp * 100 + 7200) {
        return;
      }

      // Verify that indexes are setup properly

      uint index;
      BalanceId balance_id;

      // Check taker quote
      index = indexes[index_pos];
      balance_id = { trade.taker.session_id, 0 };
      if (balance_ids[index] == 0) { balance_ids[index] = balance_id; }
      else { require(balance_ids[index] == balance_id); }

      // Check taker base
      index = indexes[index_pos + 1];
      balance_id = { trade.taker.session_id, trade.token_id };
      if (balance_ids[index] == 0) { balance_ids[index] = balance_id; }
      else { require(balance_ids[index] == balance_id); }

      // Check maker 1 quote
      index = indexes[index_pos + 2];
      balance_id = { trade.maker_1.party.session_id, 0 };
      if (balance_ids[index] == 0) { balance_ids[index] = balance_id; }
      else { require(balance_ids[index] == balance_id); }

      // Check maker 2 base
      index = indexes[index_pos + 3];
      balance_id = { trade.maker_1.party.session_id, trade.token_id };
      if (balance_ids[index] == 0) { balance_ids[index] = balance_id; }
      else { require(balance_ids[index] == balance_id); }

      // Check maker 3 quote
      index = indexes[index_pos + 4];
      balance_id = { trade.maker_2.party.session_id, 0 };
      if (balance_ids[index] == 0) { balance_ids[index] = balance_id; }
      else { require(balance_ids[index] == balance_id); }

      // Check maker 3 base
      index = indexes[index_pos + 5];
      balance_id = { trade.maker_2.party.session_id, trade.token_id };
      if (balance_ids[index] == 0) { balance_ids[index] = balance_id; }
      else { require(balance_ids[index] == balance_id); }


      // MAKER 1 TRADE

      {
        uint quantity = trade.maker_1.quantity.value();
        uint price = trade.maker_1.price.value();
        int total = int((quantity * price) / 100000000);
        if (trade.is_buy) { quantity = -quantity; } else { total = -total; }

        balance_changes[uint(indexes[index_pos + 0]) /* taker quote */] += total;
        balance_changes[uint(indexes[index_pos + 1]) /* taker base */] -= quantity;

        balance_changes[uint(indexes[index_pos + 2]) /* maker quote */] -= total;
        balance_changes[uint(indexes[index_pos + 3]) /* maker base */] += quantity;
      }

      // MAKER 2 TRADE

      if (taker.maker_2.party.session_id != 0) {
        uint quantity = trade.maker_2.quantity.value();
        uint price = trade.maker_2.price.value();
        int total = int((quantity * price) / 100000000);
        if (trade.is_buy) { quantity = -quantity; } else { total = -total; }

        balance_changes[uint(indexes[index_pos + 0]) /* taker quote */] += total;
        balance_changes[uint(indexes[index_pos + 1]) /* taker base */] -= quantity;

        balance_changes[uint(indexes[index_pos + 4]) /* maker quote */] -= total;
        balance_changes[uint(indexes[index_pos + 5]) /* maker base */] += quantity;
      }

      index_pos = index_pos + 6;
    }

    // Update balances
    for (uint i = 0; i < 256; ++i) {
      BalanceId balance_id = balance_ids[i];
      if (balance_id == 0) {
        break;
      }

      int balance_change = balance_changes[i];

      // Ensure balance can cover delta
      if (balance_change < 0) {
        assert(balances[balance_id] >= uint(-balance_change));
      }

      // Update balance
      balances[balance_id] = uint(int(balances[balance_id]) + balance_change);
    }

    if (read_timestamp != local_read_timestamp) {
      read_timestamp = local_read_timestamp;
    }
    read_head = pos;
  }

  function push_updates(uint position, uint8 word_count, bytes32[8] words) public {
    require(msg.sender == owner);
    require(write_head == position);
    require(write_head - read_head < 65538 - trade_count); // TODO: double check bounds

    uint current_time = block.timestamp;
    uint time_offset = (current_time - write_timestamp + 99) / 100;

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
        uint(word) & uint(0x7FFC00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF)
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
