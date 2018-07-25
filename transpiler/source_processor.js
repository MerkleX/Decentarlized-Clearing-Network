/*
 * Scans through source for regex and replace the content with the result of
 * the provided callback.
*/
module.exports = function(source, regex, callback) {
  const result = [];

  while (true) {
    const search = source.match(regex);
    if (!search) {
      result.push(source);
      return result.join('');
    }

    const find_length = search[0].length;
    const find_index = search.index;

    result.push(source.substr(0, find_index));
    source = source.substr(find_index + find_length);

    const replace_text = callback(search);
    result.push(replace_text);
  }
};
