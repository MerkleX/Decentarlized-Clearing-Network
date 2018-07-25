const fs = require('fs');

const data = fs.readFileSync('contracts/MerkleX2.sol', 'utf-8');

const start_index = data.indexOf('{', data.indexOf('TRADE_DEF'));
const end_index = data.indexOf('}', start_index);

const def_body = data.substr(start_index + 1, end_index - start_index - 1);

const attributes = def_body.match(/([\w_\d]+)\s*:\s*(\d+)/g);

const def = {};

let offset = 0;
attributes.forEach(attr => {
  const parts = attr.match(/([\w_\d]+)\s*:\s*(\d+)/);
  const name = parts[1];
  const size = +parts[2];

  offset += size;

  def[name] = {
    size,
    offset,
  };
});


let idx = 0;
const result = [];

while (true) {
  const pos = data.indexOf('TRADE(', idx);
  if (pos === -1) {
    result.push(data.substr(idx));
    break;
  }

  result.push(data.substr(idx, pos - idx));

  const args = data.substr(pos).match(/TRADE\(\s*([\w_]+)\s*,\s*([\w_]+)\s*(,\s*([\w_]+)\s*)?\)/);
  const word = args[1];
  const attr = args[2];
  const shift = +args[4] || 0;

  const attr_def = def[attr];

  const Z = '000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000';

  const shift_div_pow = 256 - attr_def.offset + shift;
  const shift_div_hex = ['1', '2', '4', '8'][shift_div_pow % 4] + Z.substr(Z.length - Math.floor(shift_div_pow / 4));

  let mask = (Math.pow(2, attr_def.size + shift) - 1).toString('16').split('');

  if (shift) {
    const hex_count = Math.floor(shift / 4);
    for (let i = 0; i < hex_count; ++i) {
      mask[mask.length - 1 - i] = '0';
    }

    // 1111 = 15 = F
    // 1110 = 14 = E
    // 1100 = 12 = C
    // 1000 = 08 = 8
    
    if (shift != hex_count * 4) {
      const value = [15, 14, 12, 8][shift % 4]
      const pos = Math.ceil(shift / 4);
      const current = parseInt(mask[pos]);
      const new_value = value & current;
      mask[pos] = new_value.toString(16);
    }
  }

  result.push(`and(div(${word}, 0x${shift_div_hex}), 0x${mask.join('')})`);

  idx = pos + args[0].length;
}

console.log(result.join(''));
