#define TRANSPILE

#include "../../../src/main/resources/contracts/DCN.sol"

#define CONFLICT(TYPE1, TYPE2, ptr1, ptr2) \
  switch gt(ptr1, ptr2) \
  case 0 { /* ptr1 <= ptr2 */ \
    if iszero(lt(add(ptr1, sizeof(TYPE1)), ptr2)) { \
      REVERT(1) \
    } \
  } \
  default { \
    if iszero(lt(add(ptr2, sizeof(TYPE2)), ptr1)) { \
      REVERT(2) \
    } \
  }

#define CONTAINS(OUTER_TYPE, INNER_TYPE, outer_ptr, inner_ptr) \
  if lt(inner_ptr, outer_ptr) { REVERT(1) } \
  if gt(add(inner_ptr, sizeof(INNER_TYPE)), add(outer_ptr, sizeof(OUTER_TYPE))) { REVERT(2) }

contract TEST is DCN {
  function test_asset_exchange_no_conflict(uint32 asset_id, uint64 exchange_id) public {
    assembly {
      let asset_ptr := ASSET_PTR_(asset_id)
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)

      CONFLICT(Asset, Exchange, asset_ptr, exchange_ptr)
    }
  }

  function test_asset_user_no_conflict(uint32 asset_id, uint64 user_id) public {
    assembly {
      let asset_ptr := USER_PTR_(asset_id)
      let user_ptr := USER_PTR_(user_id)

      CONFLICT(User, Asset, user_ptr, asset_ptr)
    }
  }

  function test_user_exchange_no_conflict(uint64 user_id, uint64 exchange_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)

      CONFLICT(User, Exchange, user_ptr, exchange_ptr)
    }
  }

  function test_exchange_balance_in_exchange(uint64 exchange_id, uint32 asset_id) public {
    assembly {
      let exchange_ptr := EXCHANGE_PTR_(exchange_id)
      let balance_ptr := EXCHANGE_BALANCE_PTR_(exchange_ptr, asset_id)

      CONTAINS(Exchange, u256, exchange_ptr, balance_ptr)
    }
  }

  function test_user_balance_in_user(uint64 user_id, uint32 asset_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
      let balance_ptr := USER_BALANCE_PTR_(user_ptr, asset_id)

      CONTAINS(User, u256, user_ptr, balance_ptr)
    }
  }

  function test_user_session_in_user(uint64 user_id, uint64 exchange_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)

      CONTAINS(User, ExchangeSession, user_ptr, session_ptr)
    }
  }

  function test_user_session_balance_inside(uint64 user_id, uint64 exchange_id, uint32 asset_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, asset_id)

      CONTAINS(User, ExchangeSession, user_ptr, session_ptr)
      CONTAINS(ExchangeSession, SessionBalance, session_ptr, session_balance_ptr)
    }
  }

  function test_market_state_in_session(uint64 user_id, uint64 exchange_id, uint32 quote_asset_id, uint32 base_asset_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)
      let market_state_ptr := MARKET_STATE_PTR_(session_ptr, quote_asset_id, base_asset_id)

      CONTAINS(ExchangeSession, MarketState, session_ptr, market_state_ptr)
    }
  }

  function test_market_state_session_balance_no_conflict(uint64 user_id, uint64 exchange_id,
                                                         uint32 asset_id, uint32 quote_asset_id, uint32 base_asset_id) public {
    assembly {
      let user_ptr := USER_PTR_(user_id)
      let session_ptr := SESSION_PTR_(user_ptr, exchange_id)

      let session_balance_ptr := SESSION_BALANCE_PTR_(session_ptr, asset_id)
      let market_state_ptr := MARKET_STATE_PTR_(session_ptr, quote_asset_id, base_asset_id)

      CONFLICT(SessionBalance, MarketState, session_balance_ptr, market_state_ptr)
    }
  }
}
