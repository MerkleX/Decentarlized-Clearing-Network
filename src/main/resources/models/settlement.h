struct GroupsHeader {
    u32 exchange_id;
};

struct GroupHeader {
    u32 quote_asset_id;
    u32 base_asset_id;
    u8 user_count;
};

struct SettlementData {
    u64 user_id;
    i64 quote_delta;
    i64 base_delta;
    u64 fees;
};