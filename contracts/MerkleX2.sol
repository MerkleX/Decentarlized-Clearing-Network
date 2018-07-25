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

  function submit_window(bytes32[4] token_indexes, bytes window_data) public {
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

      let window_data_size := mload(window_data)
      let window_ptr := add(window_data, 32)
      let window_end_ptr := add(window_ptr, window_data_size)

      for {} lt(window_ptr, add(window_data, window_data_size)) { entries_ptr := add(window_ptr, 16) } {
        let trade_data := mload(window_ptr)

          let eth_qty := mul(TRADE(trade_data, ether_qty_sig), exp(10, TRADE(trade_data, ether_qty_pow)))
          let tkn_qty := mul(TRADE(trade_data, token_qty_sig), exp(10, TRADE(trade_data, token_qty_pow)))
      }

      // Ensure we have the correct amount of data in the payload
      if not(eq(mload(data_ptr), mul(entry_count, 16))) {
        stop()
      }

      let entry_ptr := add(data_ptr, 32)
      let entry_end_ptr := add(entry_ptr, mul(entry_count, 16))
      let index_ptr := sub(entry_end_ptr, 30)

      let net_eth := 0

      for {} lt(entry_ptr, entry_end_ptr) {} {
        {
          let trade_data := mload(entry_ptr)
          let entry_indexes := mload(index_ptr)

          window_ptr := add(window_ptr, 32)
          sstore(window_ptr, trade_data)

          // Calc net for first trade
          for {} gt(trade_data, 0) { trade_data := div(trade_data, 0x100000000000000000000000000000000) } {
            let eth_qty := mul(TRADE(trade_data, ether_qty_sig), exp(10, TRADE(trade_data, ether_qty_pow)))
            let tkn_qty := mul(TRADE(trade_data, token_qty_sig), exp(10, TRADE(trade_data, token_qty_pow)))

            // Prevent overflow, cannot be larger than (2^256 / 128)
            if gt(eth_qty, 0x200000000000000000000000000000000000000000000000000000000000000) {
              revert(0, 0)
            }
            if gt(tkn_qty, 0x200000000000000000000000000000000000000000000000000000000000000) {
              revert(0, 0)
            }

            switch TRADE(trade_data, is_long)
            case 0 {
              tkn_qty := sub(0, tkn_qty)
            }
            default {
              eth_qty := sub(0, eth_qty)
            }

            net_eth := add(net_eth, eth_qty)

            let tkn_idx := and(div(entry_indexes, 256), 0xFF)

            // ensure that tkn_idx is properly setup
            {
              let trade_token := TRADE(trade_data, token_id)
              let tkn_idx_ptr := add(toket_id_ptr, mul(tkn_idx, 32))
              let tkn_idx_value := mload(tkn_idx_ptr)

              switch iszero(tkn_idx_value)
              case 0 {
                mstore(tkn_idx_ptr, trade_token)
              }
              default {
                if not(eq(trade_token, tkn_idx_value)) {
                  revert(0, 0)
                }
              }
            }

            let net_tkn_ptr := add(net_tokens_ptr, tkn_idx_ptr)
            mstore(net_tkn_ptr, add(mload(net_tkn_ptr), tkn_qty))
          }
        }

        entries_ptr := add(entries_ptr, 32)
        index_ptr := add(index_ptr, 2)
      }
    }
  }
  }

