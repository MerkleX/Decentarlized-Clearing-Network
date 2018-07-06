pragma solidity ^0.4.23;

// contract ERC20 {
//   function balanceOf(address tokenOwner) public constant returns (uint balance);
//   function transfer(address to, uint tokens) public returns (bool success);
// }

contract MerkleX {
  uint64 constant PENDING_TRADE_BLOCKS = 1440; // ~5 hours

  /*
     Message Components

     Timestamp - 256
     |   2 |   2 | word_type=0 (timestamp) :: 0 = timestamp
     | 254 | 256 | timestamp


     Trade Header - 24
     |  2 |   2 | word_type=0 (trade) :: 0 = timestamp, 1 = trade_start, 2 = trade_continue
     | 11 |  13 | token_id
     |  1 |  14 | is_buy :: 0=sell, 1=buy
     |  2 |  16 | state :: 0=pending, 1=confirmed, 2=contested, 3=broken

     |  8 |  24 | timestamp_offset (100s)


     Account Header - 32
     |  2 |   2 | word_type=1 (trade_continue) :: 0 = timestamp, 1 = trade_start, 2 = trade_continue
     | 30 |  32 | account_id


     Word End - 2
     |  2 |   2 | ext_type=3 (end) :: 0/1 = order, 2 = trade_continue, 3 = end


     Order Id - 16
     | 16 |  16 | order_id


     Order Details - 48
     |  1 |   1 | ext_type=0 (order) :: 0 = order, 1 = account_header (fits with word_type)
     |  7 |   8 | quantity_sig

     |  5 |  13 | quantity_scale
     |  5 |  18 | price_scale
     | 14 |  32 | price_sig

     | 16 |  48 | order_id

     
     Enumerated Message Possiblities

     Trade
     |  TradeHeader     |  24 |     | header
     |  Account Header  |  32 |     | taker_account
     |  Order Id        |  16 |     | taker_order
     |  Account Header  |  32 |     | maker_account
     |  Order Details   |  48 |     | maker_order_1
     |  Order Details   |  48 |     | maker_order_2
     |  Order Details   |  48 |     | maker_order_3

     Trade
     |  TradeHeader     |  24 |     | header
     |  Account Header  |  32 |     | taker_account
     |  Order Id        |  16 |     | taker_order
     |  Account Header  |  32 |     | maker_account
     |  Order Details   |  48 |     | maker_order_1
     |  Order Details   |  48 |     | maker_order_2
     |  Word End        |  2  |     | word_end

     Trade
     |  TradeHeader     |  24 |     | header
     |  Account Header  |  32 |     | taker_account
     |  Order Id        |  16 |     | taker_order
     |  Account Header  |  32 |     | maker_account
     |  Order Details   |  48 |     | maker_order_1
     |  Word End        |  48 |     | end

     Trade 
     |  TradeHeader     |  24 |     | header
     |  Account Header  |  32 |     | taker_account
     |  Order Id        |  16 |     | taker_order
     |  Account Header  |  32 |     | maker_account_1
     |  Order Details   |  48 |     | maker_order_1
     |  Account Header  |  32 |     | maker_account_2
     |  Order Details   |  48 |     | maker_order_2

     Makers
     |  Account Header  |  32 |     | maker_account
     |  Order Details   |  48 |     | maker_order_1
     |  Order Details   |  48 |     | maker_order_2
     |  Order Details   |  48 |     | maker_order_3
     |  Order Details   |  48 |     | maker_order_4

     Makers
     |  Account Header  |  32 |     | maker_account_1
     |  Order Details   |  48 |     | maker_order_1
     |  Account Header  |  32 |     | maker_account_2
     |  Order Details   |  48 |     | maker_order_2
     |  Account Header  |  32 |     | maker_account_3
     |  Order Details   |  48 |     | maker_order_3
   */

  mapping(uint16 => address) token_addresses;
  mapping(uint32 => address) user_sessions;
  mapping(address => mapping(uint16 => uint256)) balances;
  mapping(uint256 => bytes32) data;

  uint256 base_timestamp;

  uint256 write_head;
  uint256 read_head;

  address owner;

  function push_data(uint256 position, uint8 word_count, bytes32[7] words) public {
    require (msg.sender == owner);
    require (write_head == position);
    require (write_head - read_head < 65538 - word_count);
    require (words[0][0] & 192 == 64);

    uint256 current_time = block.timestamp;
    uint256 time_offset = (current_time - base_timestamp) / 100;

    // Insert a timestamp if offset would overflow
    if (time_offset >= 256) {
      data[position & 65535] = bytes32(current_time & uint256(
          0x3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
      )); // mod for sanity check
      
      base_timestamp = current_time;
      time_offset = 0;
      position += 1;
    }

    uint256 timestamp = time_offset << 232;

    for (uint8 i = 0; i < word_count; i++) {
      bytes32 word = words[i];

      // is trade
      if (word[0] & 192 == 64) {
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
        require(word[0] & 192 == 128);
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

//  function updatedBalances() internal constant returns (mapping(uint32 => mapping(
//
//  function balanceOf(address account, address token) public constant returns (uint256 balance) {
//    return balances[accounts[account]][tokens[token]];
//  }
//
//  // Helpful function only to be used offline to check balance
//  function calcBalanceOf(address account, address token)
//  public constant returns (uint256 balnace) {
//    uint32 account_id = accounts[account];
//    uint16 token_id = tokens[token];
//
//    uint256 balance = balances[account_id][token_id];
//
//    for (uint64 pos = next_process_trade_id; pos < next_trade_id; pos++) {
//      Trade storage trade = pending_trades[pos];
//
//      // Already applied
//      if (trade.state >= 20) {
//        continue;
//      }
//
//      uint8 i;
//      uint256 amount;
//
//      if (trade.token_to_buyer == token_id) {
//        if (trade.buyer_account_id == account_id) {
//          for (i = 0; i < trade.sellers_count; i++) {
//            balance = balance + amt(trade.sellers[i].to_buyer);
//          }
//        }
//        else {
//          for (i = 0; i < trade.sellers_count; i++) {
//            if (trade.sellers[i].account_id == account_id) {
//              amount = amt(trade.sellers[i].to_buyer);
//              require(balance >= amount);
//              balance = balance - amount;
//            }
//          }
//        }
//      }
//      else if (trade.token_to_seller == token_id) {
//        if (trade.buyer_account_id == account_id) {
//          for (i = 0; i < trade.sellers_count; i++) {
//            amount = amt(trade.sellers[i].to_seller);
//            require(balance >= amount);
//            balance = balance - amount;
//          }
//        }
//        else {
//          for (i = 0; i < trade.sellers_count; i++) {
//            if (trade.sellers[i].account_id == account_id) {
//              balance = balance + amt(trade.sellers[i].to_seller);
//            }
//          }
//        }
//      }
//    }
//  }
//
//  function process_next() public returns (bool processed) {
//    if (next_process_trade_id >= next_trade_id) {
//      return false;
//    }
//
//    Trade storage trade = pending_trades[next_process_trade_id];
//
//    // Trade must not be contested or must be resolved
//    if (trade.state >= 10) {
//      return false;
//    }
//
//    // Is trade old enough to process
//    if (trade.block_number + PENDING_TRADE_BLOCKS <= block.number) {
//      return false;
//    }
//
//    uint256 buyer_in_balance = balances[trade.buyer_account_id][trade.token_to_buyer];
//    uint256 buyer_out_balance = balances[trade.buyer_account_id][trade.token_to_seller];
//
//    for (uint8 i = 0; i < trade.sellers_count; i++) {
//      Seller storage seller = trade.sellers[i];
//
//      uint256 to_buyer = amt(seller.to_buyer);
//      uint256 to_seller = amt(seller.to_seller);
//
//      require(buyer_out_balance >= to_seller);
//      buyer_out_balance = buyer_out_balance - to_seller;
//      buyer_in_balance  = buyer_in_balance + to_buyer;
//
//      uint256 seller_in_balance = balances[seller.account_id][trade.token_to_seller];
//      uint256 seller_out_balance = balances[seller.account_id][trade.token_to_buyer];
//
//      require(seller_out_balance >= to_buyer);
//      balances[seller.account_id][trade.token_to_buyer] = seller_out_balance - to_buyer;
//      balances[seller.account_id][trade.token_to_seller] = seller_in_balance - to_seller;
//    }
//
//    balances[trade.buyer_account_id][trade.token_to_buyer] = buyer_in_balance;
//    balances[trade.buyer_account_id][trade.token_to_seller] = buyer_out_balance;
//
//    next_process_trade_id = next_process_trade_id + 1;
//    return true;
//  }
//
//  function process_trades(uint64 max_steps) public {
//    while (max_steps > 0) {
//      max_steps = max_steps - 1;
//      if (!process_next()) {
//        return;
//      }
//    }
//  }
}
