#ifndef __COMMON_H__
#define __COMMON_H__

#include <stdint.h>
#include <sys/types.h>
#include <cstdio>
#include <cstdlib>
#include <climits>

#include <iostream>

#define INLINE __attribute__((always_inline))
#define U64_LEADING_ZEROS __builtin_clzll
#define U64_TRAILING_ZEROS __builtin_ctzll
#define ATOMIC_CMP_SWAP __sync_val_compare_and_swap
#define LIKELY(x) __builtin_expect(!!(x), 1)
#define UNLIKELY(x) __builtin_expect(!!(x), 0)

#define mb() asm volatile("" ::: "memory")
#define rmb() mb()
#define wmb() mb()

#define SIMPLE_CASTER(TYPE) \
template <typename T> \
static constexpr TYPE to_##TYPE(T item) { \
  return static_cast<TYPE>(item); \
}

using u8 = uint8_t;
using u16 = uint16_t;
using u32 = uint32_t;
using u64 = uint64_t;
using u128 = unsigned __int128;

using u8_fast = uint_fast8_t;
using u16_fast = uint_fast16_t;
using u32_fast = uint_fast32_t;
using u64_fast = uint_fast64_t;

using i8 = int8_t;
using i16 = int16_t;
using i32 = int32_t;
using i64 = int64_t;
using i128 = __int128;

#define U64_MAX ULLONG_MAX

SIMPLE_CASTER(u8);
SIMPLE_CASTER(u16);
SIMPLE_CASTER(u32);
SIMPLE_CASTER(u64);
SIMPLE_CASTER(u128);

SIMPLE_CASTER(i8);
SIMPLE_CASTER(i16);
SIMPLE_CASTER(i32);
SIMPLE_CASTER(i64);
SIMPLE_CASTER(i128);

template <typename I>
INLINE static constexpr bool powerof2(I x) {
  return (x != 0) && !(x & (x - 1));
}

static inline u32 log10(u64 v) {
  static const uint64_t thr[64] = {
  10000000000000000000ULL, -1ULL, -1ULL, -1ULL, 1000000000000000000ULL, -1ULL, -1ULL, 100000000000000000ULL, -1ULL, -1ULL,
     10000000000000000ULL, -1ULL, -1ULL, -1ULL,    1000000000000000ULL, -1ULL, -1ULL,    100000000000000ULL, -1ULL, -1ULL,
        10000000000000ULL, -1ULL, -1ULL, -1ULL,       1000000000000ULL, -1ULL, -1ULL,       100000000000ULL, -1ULL, -1ULL,
           10000000000ULL, -1ULL, -1ULL, -1ULL,          1000000000ULL, -1ULL, -1ULL,          100000000ULL, -1ULL, -1ULL,
              10000000ULL, -1ULL, -1ULL, -1ULL,             1000000ULL, -1ULL, -1ULL,             100000ULL, -1ULL, -1ULL,
                 10000ULL, -1ULL, -1ULL, -1ULL,                1000ULL, -1ULL, -1ULL,                100ULL, -1ULL, -1ULL,
                    10ULL, -1ULL, -1ULL, -1ULL
  };

  uint32_t lz = U64_LEADING_ZEROS(v);
  return (63 - lz) * 3 / 10 + (v >= thr[lz]);
}

template <typename T>
static u64 pow_base_10(T number) {
  static u64 powers[20] = {
       1,
       10ULL,
       100ULL,
       1000ULL,
       10000ULL,
       100000ULL,
       1000000ULL,
       10000000ULL,
       100000000ULL,
       1000000000ULL,
       10000000000ULL,
       100000000000ULL,
       1000000000000ULL,
       10000000000000ULL,
       100000000000000ULL,
       1000000000000000ULL,
       10000000000000000ULL,
       100000000000000000ULL,
       1000000000000000000ULL,
       10000000000000000000ULL
  };

  return powers[number];
}

template <typename T>
static T* CACHE_ALLOC(size_t count) {
  return (T *) aligned_alloc(64, sizeof(T) * count);
}

// lifted from https://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetNaive
template <typename T>
static T bit_count(T v) {
  v = v - ((v >> 1) & (T)~(T)0/3);                           // temp
  v = (v & (T)~(T)0/15*3) + ((v >> 2) & (T)~(T)0/15*3);      // temp
  v = (v + (v >> 4)) & (T)~(T)0/255*15;                      // temp
  T c = (T)(v * ((T)~(T)0/255)) >> (sizeof(T) - 1) * CHAR_BIT; // count
  return c;
}

static inline u64 exp(u64 base, u64 exp) {
  u64 result = 1;
  while (true) {
    if (exp % 2 == 1) {
      result *= base;
    }
    exp >>= 1;
    if (exp == 0) {
      break;
    }
    base = base * base;
  }
  return result;
}

#define INIT_FAIL(MSG) \
  std::cerr << "System failed during init " << __FILE__ << ":" << __LINE__ << ", msg: " << MSG << std::endl; \
  assert(false); \
  exit (EXIT_FAILURE);

#define INIT_CHECK(COND, MSG) \
  if (!(COND)) { \
    INIT_FAIL(MSG) \
  }
  

#endif
