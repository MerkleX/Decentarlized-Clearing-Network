const ParseDefs = require('./def');
const SourceProcessor = require('./source_processor');
const CommaParser = require('./comma_parser');

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

      const word_part = shift === 0 ? word : `mul(${word}, 0x${value.toString(16)})`;
      const res = `and(${word_part}, 0x${mask.toString(16)})`;
      return `/* ${og} */ ${res}`;
    }
    else {
      const value = 1n << shift;
      const mask = ((1n << BigInt(attr.size)) - 1n) << offset;

      const word_part = shift === 0 ? word : `div(${word}, 0x${value.toString(16)})`;
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


  // Process builders
  source = SourceProcessor(source, /BUILD_([A-Z_]+)\s*{(["\/\*\._\(\)\w\d\s,]+)}(#MASK)?/, instance => {
    const [ og, name, args_source, want_mask ] = instance;

    console.error(name);

    const args = CommaParser(args_source);

    const def = defs[name];
    if (!def) {
      console.error('could not find build def', name);
      return og;
    }

    if (def.attributes.length !== args.length) {
      console.error('invalid number of args', name, def.attributes.length, args.length);
      console.error(args_source, args);
      return og;
    }

    const or_parts = [];

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
          or_parts.push(`and(/* ${attr.name} */ ${arg}, 0x${((1n << BigInt(attr.size)) - 1n).toString(16)}`);
        }
        else {
          or_parts.push(arg);
        }

        if (i < attrs.length) {
          throw new Error();
        }

        break;
      }

      const shift_mul = '0x' + (1n << (256n-BigInt(attr.end_offset))).toString(16);

      if (want_mask) {
        const mask = '0x' + (1n << BigInt(attr.size)).toString(16);
        or_parts.push(`mul(and(/* ${attr.name} */ ${arg}, ${mask}), ${shift_mul})`);
      }
      else {
        or_parts.push(`mul(/* ${attr.name} */ ${arg}, ${shift_mul})`);
      }
    }

    const expr = [];
    const expr_post = [];

    for (let i = 0; i < or_parts.length; ) {
      const part = or_parts[i++];

      if (i === or_parts.length) {
        expr.push(part);
      }
      else {
        expr.push(`or(${part}, `);
        expr_post.push(')');
      }
    }

    return expr.join('') + expr_post.join('');
  });

  // Process shift commands
  source = SourceProcessor(source, /(l|r)shift\s*\((\w+|\w+\s*\([\w\),\(]+\)),\s*(\d+)\)/, instance => {
    const [ og, type, subject, shift_amount ] = instance;

    if (type === 'r') {
      return `div(${subject}, 0x${(1n << BigInt(shift_amount)).toString(16)})`;
    }
    else if (type === 'l') {
      return `mul(${subject}, 0x${(1n << BigInt(shift_amount)).toString(16)})`;
    }

    return og;
  });

  const define_map = {};

  source = SourceProcessor(source, /#define\s+(\w+)\s+([\w\d]+)/, instance => {
    const [ og, key, value ] = instance;
    define_map[key] = value;
    return og;
  });

  Object.keys(define_map).forEach(key => {
    const query = new RegExp(`(define\\s+)?(${key})`);
    source = SourceProcessor(source, query, instance => {
      const [ og, define ] = instance;

      if (define) {
        return og;
      }

      return `/* ${key} */ ${define_map[key]}`;
    });
  });

  return source;
};
