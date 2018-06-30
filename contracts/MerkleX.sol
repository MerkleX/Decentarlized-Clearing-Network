pragma solidity ^0.4.23;

contract ERC20 {
  function balanceOf(address tokenOwner) public constant returns (uint balance);
  function transfer(address to, uint tokens) public returns (bool success);
}

contract MerkleX {
  uint64 constant PENDING_TRADE_BLOCKS = 1440; // ~5 hours

  struct Amount {
    uint32 figure;
    uint8 scale;
  }

  struct Seller {
    uint32 order_id;
    uint32 account_id;

    Amount to_buyer;
    Amount to_seller;
  }

  struct Trade {
    uint64 block_number;

    uint32 buyer_order_id;
    uint32 buyer_account_id;

    uint16 token_to_buyer;
    uint16 token_to_seller;

    uint8 sellers_count;

    Seller[10] sellers;

    /*
     * 0 = pending
     * 1 = proof supplied
     * 10 = proof requested
     * 20 = trade applied
     */
    uint8 state;
  }

  struct Signature {
    bytes32 hash;
    uint8 v;
    bytes32 r;
    bytes s;
  }

  struct Order {
    uint64 expire_block;
    uint32 order_id;
    uint16 buy_token;
    uint16 cost_token;
    Amount buy_amount;
    Amount cost_amount;
  }

  struct OrderProof {
    Order order;
    Signature signature;
  }

  struct Cancel {
    uint64 expire_block;
    uint32 order_id;
  }

  struct CancelProof {
    Cancel cancel;
    Signature signature;
  }

  address owner;
  uint64 next_trade_id;
  uint64 next_process_trade_id;

  constructor() {
    owner = msg.sender;
  }

  mapping(address => uint32) accounts;
  mapping(address => uint16) tokens;
  
  mapping(uint32 => mapping(uint16 => uint256)) balances;
  mapping(uint64 => Trade) pending_trades;

  function balanceOf(address account, address token) public constant returns (uint256 balance) {
    return balances[accounts[account]][tokens[token]];
  }

  function amt(Amount a) public constant returns (uint256 amount) {
    return uint256(a.figure) * (uint256(10) ** uint256(a.scale));
  }

  // Helpful function only to be used offline to check balance
  function calcBalanceOf(address account, address token)
  public constant returns (uint256 balnace) {
    uint32 account_id = accounts[account];
    uint16 token_id = tokens[token];

    uint256 balance = balances[account_id][token_id];

    for (uint64 pos = next_process_trade_id; pos < next_trade_id; pos++) {
      Trade trade = trades[pos];

      // Already applied
      if (trade.status >= 20) {
        continue;
      }

      if (trade.token_to_buyer == token_id) {
        if (trade.buyer_account_id == account_id) {
          for (uint8 i = 0; i < trade.sellers_count; i++) {
            balance = balance + amt(trade.sellers[i].to_buyer);
          }
        }
        else {
          for (uint8 i = 0; i < trade.sellers_count; i++) {
            Seller seller = trade.sellers[i];
            if (seller.account_id == account_id) {
              uint256 amount = amt(trade.sellers[i].to_buyer);
              require(balance >= amount);
              balance = balance - amount;
            }
          }
        }
      }
      else if (trade.token_to_seller == token_id) {
        if (trade.buyer_account_id == account_id) {
          for (uint8 i = 0; i < trade.sellers_count; i++) {
            uint256 amount = amt(trade.sellers[i].to_seller);
            require(balance >= amount);
            balance = balance - amount;
          }
        }
        else {
          for (uint8 i = 0; i < trade.sellers_count; i++) {
            Seller seller = trade.sellers[i];
            if (seller.account_id == account_id) {
              balance = balance + amt(seller.to_seller);
            }
          }
        }
      }
    }
  }

  function add_trades(uint64 trade_id, Trade[] trades) public {
    // Only MerkleX can add trades
    if (msg.sender != owner) {
      return;
    }

    // Only add trades in sequence
    if (next_trade_id != trade_id) {
      return;
    }

    // Delete old records and collect gas credits :)
    while (next_process_trade_id < next_trade_id) {
      if (pending_trades[next_process_trade_id].state < 20) {
        break;
      }
      delete pending_trades[next_process_trade_id];
    }

    // Insert new records
    for (uint256 i = 0; i < trades.length; i++) {
      pending_trades[i] = trades[i];

      pending_trades[i].block_number = block.number;
      pending_trades[i].state = 0;
    }

    next_trade_id = next_trade_id + trades.length;
  }

  function process_next() public returns (bool processed) {
    if (next_process_trade_id >= next_trade_id) {
      return false;
    }

    Trade trade = pending_trades[next_process_trade_id];

    // Trade must not be contested or must be resolved
    if (trade.status >= 10) {
      return false;
    }

    // Is trade old enough to process
    if (trade.block_number + PENDING_TRADE_BLOCKS <= block.number) {
      return false;
    }

    uint256 buyer_in_balance = accounts[trader.buyer_account_id][trade.token_to_buyer];
    uint256 buyer_out_balance = accounts[trader.buyer_account_id][trade.token_to_seller];

    for (uint8 i = 0; i < trade.sellers_count; i++) {
      Seller seller = trade.sellers[i];

      uint256 to_buyer = amt(seller.to_buyer);
      uint256 to_seller = amt(seller.to_seller);

      require(buyer_out_balance >= to_seller);
      buyer_out_balance = buyer_out_balance - to_seller;
      buyer_in_balance  = buyer_in_balance + to_buyer;

      mapping (uint16 => uint256) seller_balances = accounts[trader.seller_account_id];

      uint256 seller_in_balance = seller_balances[trade.token_to_seller];
      uint256 seller_out_balance = seller_balances[trade.token_to_buyer];

      require(seller_out_balance >= to_buyer);
      seller_balances[trade.token_to_buyer] = seller_out_balance - to_buyer;
      seller_balances[trade.token_to_seller] = seller_in_balance - to_seller;
    }

    next_process_trade_id = next_process_trade_id + 1;
    return true;
  }

  function process_trades(uint64 max_steps) public {
    while (next_process_trade_id < next_trade_id && max_steps) {
      max_steps -= 1;

      Trade trade = pending_trades[next_process_trade_id];

      // Trade must not be contested or must be resolved
      if (trade.status >= 10) {
        return;
      }

      // Is trade old enough to process
      if (trade.block_number + PENDING_TRADE_BLOCKS <= block.number) {
        return;
      }

      uint256 buyer_in_balance = accounts[trader.buyer_account_id][trade.token_to_buyer];
      uint256 buyer_out_balance = accounts[trader.buyer_account_id][trade.token_to_seller];

      for (uint8 i = 0; i < trade.sellers_count; i++) {
        Seller seller = trade.sellers[i];

        uint256 to_buyer = amt(seller.to_buyer);
        uint256 to_seller = amt(seller.to_seller);

        require(buyer_out_balance >= to_seller);
        buyer_out_balance = buyer_out_balance - to_seller;
        buyer_in_balance  = buyer_in_balance + to_buyer;

        mapping (uint16 => uint256) seller_balances = accounts[trader.seller_account_id];

        uint256 seller_in_balance = seller_balances[trade.token_to_seller];
        uint256 seller_out_balance = seller_balances[trade.token_to_buyer];

        require(seller_out_balance >= to_buyer);
        seller_balances[trade.token_to_buyer] = seller_out_balance - to_buyer;
        seller_balances[trade.token_to_seller] = seller_in_balance - to_seller;
      }
    }
  }
}
