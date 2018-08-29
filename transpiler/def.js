/*
 * Parse source and extract out all of the DEF macros
*/
module.exports = function(source) {
  const def_regex = /(\w+)_DEF\s*{([\w\s\d,:]+)}/;
  const def_attr_regex = /([\w]+)\s*:\s*(\d+)\s*,/;

  const def_regex_all = new RegExp(def_regex, 'g');
  const def_attr_regex_all = new RegExp(def_attr_regex, 'g');

  const defs = source.match(def_regex_all) || [];

  const def_res = {};

  defs.forEach(def_src => {
    const [ all, name, attrs_src ] = def_src.match(def_regex);

    const attr_res = {};
    const def_data = {
      name,
      total_size: 0,
      attributes: attr_res,
    };

    def_res[name] = def_data;

    let len = 0;
    attrs_src.match(def_attr_regex_all).forEach(attr_src => {
      const [ attr_all, attr_name, attr_size ] = attr_src.match(def_attr_regex);

      const value = {
        name: attr_name,
        size: +attr_size,
        offset: def_data.total_size,
        end_offset: def_data.total_size + (+attr_size),
      };

      attr_res[len] = value;
      attr_res[attr_name] = value;
      def_data.total_size += +attr_size;

      len += 1;
    });

    attr_res.length = len;
  });

  return def_res;
};
