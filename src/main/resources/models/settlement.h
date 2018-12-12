struct GroupsHeader {
    u32 exchange_id;
};

struct GroupHeader {
    u32 base_asset_id;
    u8 user_count;
};

struct SettlementData {
    u8 user_address[20];
    i64 quote_delta;
    i64 base_delta;
    u64 fees;
};