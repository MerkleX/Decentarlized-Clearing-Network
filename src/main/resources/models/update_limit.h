
struct UpdateLimit {
    u8 user_address[20];

    u32 exchange_id;
    u32 asset_id;
    u64 version;
    u64 max_long_price;
    u64 min_short_price;
    i64 min_quote_qty;
    i64 min_base_qty;
    i64 quote_shift;
    i64 base_shift;

    u8 sig_r[32];
    u8 sig_s[32];
    u8 sig_v;
};