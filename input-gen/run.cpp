#include <iostream>
#include "./common.h"

/*
   | attr name     | len | cumlen | description
   |---------------|-----|--------|
   | is_timestamp  |   1 |      1 |
   | is_buy        |   1 |      2 |
   | token_id      |  12 |     14 |
   | state         |   2 |     16 | 0=pending, 1=confirmed, 2=contested, 3=broken
   | timestamp     |   8 |     24 | *100 + read_timestamp
   | taker_session |  24 |     48 |
   | taker_order   |  16 |     64 |

   | mk1_session   |  24 |     88 |
   | mk1_order     |  16 |    104 |

   | mk1_quant_sig |  27 |    131 |
   | mk1_quant_pow |   5 |    136 |
   | mk1_price_sig |  20 |    156 |
   | mk1_price_pow |   4 |    160 |

   | mk2_session   |  24 |    184 |
   | mk2_order     |  16 |    200 |

   | mk2_quant_sig |  27 |    227 |
   | mk2_quant_pow |   5 |    232 |
   | mk2_price_sig |  20 |    252 |
   | mk2_price_pow |   4 |    256 |
*/

#pragma pack(push, 1)
struct OrderDetails {
  char session_id[3];
  u16 order_id;

  void set(u32 session_id, u16 order_id) {
    char *s_id = (char *)&session_id;
    this->session_id[0] = s_id[1];
    this->session_id[1] = s_id[2];
    this->session_id[2] = s_id[3];
    this->order_id = order_id;
  }
};

struct Quantity {
  u32 data;

  void set(u32 sig, u8 pow) {
    data = (sig << 5) | (pow & 0x1F);
  }
};

struct Price {
  char data[3];

  void set(u32 sig, u8 pow) {
    u32 data_num = ((sig & 0x0FFFFF) << 4) | (pow & 0x0F);
    char *data_bin = (char *) &data_num;

    data[0] = data_bin[1];
    data[1] = data_bin[2];
    data[2] = data_bin[3];
  }
};

struct Maker {
  OrderDetails maker;
  Quantity quant;
  Price price;
};

struct Header {
  char data[3];

  void set(bool is_timestamp, bool is_buy, u16 token_id, u8 state, u8 timestamp) {
    u32 binary_num =
      (to_u32(is_timestamp & 1) << 23) |
      (to_u32(is_buy & 1) << 22) |
      (to_u32(token_id & 0x0FFF) << 10) |
      (to_u32(state & 0x3) << 8) |
      to_u32(timestamp);

    char* binary = (char *)&binary_num;
    data[0] = binary[1];
    data[1] = binary[2];
    data[2] = binary[3];
  }

  void set(bool is_buy, u16 token_id) {
    set(false, is_buy, token_id, 0, 0);
  }
};

char const hex_chars[16] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

struct TradeUpdate {
  Header header;
  OrderDetails taker;
  Maker maker1;
  Maker maker2;

  void print() {
    char *data = (char*) this;

    std::cout << "0x";
    for(int i = 0; i < 32; ++i) {
      char const byte = data[i];
      std::cout << hex_chars[(byte & 0xF0) >> 4] << hex_chars[byte & 0x0F];
    }
    std::cout << std::endl;
  }
};

static_assert(sizeof(TradeUpdate) == 32);
#pragma pack(pop)

int main() {
  TradeUpdate update;
  update.header.set(true, 12312);
  update.taker.set(/* session_id */ 1, /* order_id */ 100);

  update.maker1.maker.set(/* session_id */ 2, /* order_id */ 10);
  update.maker1.quant.set(152, 3);
  update.maker1.price.set(123456789, 1);

  update.maker2.maker.set(/* session_id */ 3, /* order_id */ 232);
  update.maker2.quant.set(152, 3);
  update.maker2.price.set(123459789, 1);
  update.print();
}
