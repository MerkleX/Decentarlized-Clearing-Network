struct ExchangeTransfersHeader {
    u32 exchange_id;
};

struct ExchangeTransferGroup {
    u32 asset_id;
    bool allow_overdraft;
    u8 transfer_count;
};

struct ExchangeTransfer {
    u64 user_id;
    u64 quantity;
};