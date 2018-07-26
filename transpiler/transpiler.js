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
    const shift = 256n - BigInt(attr.end_offset) + offset;
    const value = 1n << shift;
    const mask = ((1n << BigInt(attr.size)) - 1n) << offset;

    const word_part = shift == 0 ? word : `div(${word}, 0x${value.toString(16)})`;
    const res = `and(${word_part}, 0x${mask.toString(16)})`;
    return `/* ${og} */ ${res}`;
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

  return source;
};
