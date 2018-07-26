const ParseDefs = require('./def');
const SourceProcessor = require('./source_processor');

module.exports = function(source) {
  const defs = ParseDefs(source);

  // Process attribute selection
  source = SourceProcessor(source, /([A-Z_]+)\s*\(\s*(\w+)\s*\)\s*\.\s*(\w+)(\((\d+)\))?/, instance => {
    const [ og, name, word, attr_name, _, shift_offset ] = instance;

    const def = defs[name];
    if (!def) {
      return og;
    }

    if (def.total_size > 256) {
      return og + '/* invalid len (over 256) */';
    }

    const attr = def.attributes[attr_name];
    if (!attr) {
      return og + '/* unkown attr */';
    }

    const offset = BigInt(shift_offset || 0);
    const shift = 256n - BigInt(attr.end_offset) - offset;

    if (shift < 0) {
      const value = 1n << (-shift);
      const mask = ((1n << BigInt(attr.size)) - 1n) << offset;

      const word_part = shift == 0 ? word : `mul(${word}, 0x${value.toString(16)})`;
      const res = `and(${word_part}, 0x${mask.toString(16)})`;
      return `/* ${og} */ ${res}`;
    }
    else {
      const value = 1n << shift;
      const mask = ((1n << BigInt(attr.size)) - 1n) << offset;

      const word_part = shift == 0 ? word : `div(${word}, 0x${value.toString(16)})`;
      const res = `and(${word_part}, 0x${mask.toString(16)})`;
      return `/* ${og} */ ${res}`;
    }
  });

  // Process constants
  source = SourceProcessor(source, /([A-Z_]+)\.([A-Z]+)/, instance => {
    const [ og, name, konst ] = instance;

    const def = defs[name];
    if (!def) {
      return og;
    }

    if (konst === 'BYTES') {
      return `/* ${og} */ ${ def.total_size / 8 }`;
    }

    return og;
  });


  const arg_regex = /\s*(\w+|(\w+\([\(\)\w,\s]+\)))\s*(,|$)/;

  // Process builders
  source = SourceProcessor(source, /BUILD_([A-Z_]+)\s*\(([\(\)\w\s,]+)\)(#MASK)?/, instance => {
    const [ og, name, args_source, want_mask ] = instance;

    const args = [];
    SourceProcessor(args_source, arg_regex, arg_instance => {
      args.push(arg_instance[1]);
    });

    const def = defs[name];
    if (!def) {
      return og;
    }

    if (def.attributes.length !== args.length) {
      return og;
    }

    const expr = [];
    const expr_post = [];

    const attrs = def.attributes;
    for (let i = 0; i < attrs.length;) {
      const arg = args[i];
      const attr = attrs[i];

      ++i;

      if (arg === '0') {
        continue;
      }

      if (attr.end_offset === 256) {
        if (want_mask) {
          expr.push(`and(/* ${attr.name} */ ${arg}, 0x${((1n << BigInt(attr.size)) - 1n).toString(16)}`);
        }
        else {
          expr.push(arg);
        }

        if (i < attrs.length) {
          throw new Error();
        }

        break;
      }

      const shift_mul = '0x' + (1n << (256n-BigInt(attr.end_offset))).toString(16);

      if (want_mask) {
        const mask = '0x' + (1n << BigInt(attr.size)).toString(16);
        expr.push(`or(mul(and(/* ${attr.name} */ ${arg}, ${mask}), ${shift_mul}), `);
      }
      else {
        expr.push(`or(mul(/* ${attr.name} */ ${arg}, ${shift_mul}), `);
      }

      expr_post.push(')');
    }

    return expr.join('') + expr_post.join('');
  });

  return source;
};
