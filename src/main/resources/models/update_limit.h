
struct UpdateLimit {
    u32 dcn_id;
    u64 user_id;
    u32 exchange_id;
    u32 quote_asset_id;
    u32 base_asset_id;
    u64 fee_limit;

    i64 min_quote_qty;
    i64 min_base_qty;
    u64 long_max_price;
    u64 short_min_price;

    u64 limit_version;
    u32 quote_shift_major;
    u64 quote_shift;
    u32 base_shift_major;
    u64 base_shift;

    u8 sig_r[32];
    u8 sig_s[32];
    u8 sig_v;
};
