pragma solidity ^0.4.23;

contract ERC20 {
  function transferFrom(address from, address to, uint256 value) public returns (bool);
  function approve(address spender, uint256 value) public returns (bool);
}

contract MerkleX {
  struct User {
    uint256 trade_allowed;
    uint256 owner_address;
    uint256 trade_address;
  }

  uint256 owner;
  uint256 fee_balance;

  // State
  bytes32[2**18]  settlement_windows_data;
  uint256         write_pos;
  uint256         read_pos;

  uint256[2**15]        token_addresses;
  uint256[(2**32) * 3]  users;
  uint256[2**(15+32)]   balances;

  uint256 next_user_pointer;

  constructor() public {
    assembly {
      sstore(owner_slot, address)
      sstore(next_user_pointer_slot, 64)
    }
  }

  /*
     TRADE_DEF {
is_long       :   1,
token_id      :  15,
user_id       :  32,
ether_qty_sig :  24,
ether_qty_pow :   8,
token_qty_sig :  24,
token_qty_pow :   8,
ether_fee     :   8,
allowance_id  :   8,

_padding      : 128,
}
   */

  function submit_window(bytes32[4] token_indexes, bytes submit_data) public {
    require(uint256(msg.sender) == owner);

    int256  [1]     memory net_eth_ptr;
    int256  [127]   memory net_tokens_ptr;
    uint256 [127]   memory toket_id_ptr;

    assembly {
      let window_ptr := sload(write_pos_slot)

      // no space
      if not(lt(sload(read_pos_slot), window_ptr)) {
        stop()
      }

      // point into data
      window_ptr := add(settlement_windows_data_slot, and(window_ptr, 262143))

      // set timestamp at begining of window
      sstore(window_ptr, timestamp)

      let submit_data_size := mload(submit_data)
      let submit_ptr := add(submit_data, 32)
      let submit_end_ptr := add(submit_ptr, submit_data_size)

      for {} lt(submit_ptr, add(submit_data, submit_data_size)) { submit_ptr := add(submit_ptr, 16) } {
        let trade_data := mload(submit_ptr)

        let eth_qty := mul(TRADE(trade_data, ether_qty_sig), exp(10, TRADE(trade_data, ether_qty_pow)))
        let tkn_qty := mul(TRADE(trade_data, token_qty_sig), exp(10, TRADE(trade_data, token_qty_pow)))

        // Prevent overflow, cannot be larger than (2^256 / 128)
        if gt(eth_qty, 0x200000000000000000000000000000000000000000000000000000000000000) {
          revert(0, 0)
        }
        if gt(tkn_qty, 0x200000000000000000000000000000000000000000000000000000000000000) {
          revert(0, 0)
        }
      }

      //    // Ensure we have the correct amount of data in the payload
      //    if not(eq(mload(data_ptr), mul(entry_count, 16))) {
      //      stop()
      //    }
    }
  }
}

