/*
 * Returns an array of arguments
*/
module.exports = function(source) {
  const args = [];
  let current = [];

  for (let i = 0; i < source.length; ++i) {
    const c = source[i];

    if (c === ',') {
      args.push(current.join('').trim());
      current = [];
      continue;
    }

    current.push(c);

    if (c === '(') {
      let count = 1;
      i += 1;

      for (; i < source.length; ++i) {
        const c2 = source[i];
        current.push(c2);

        if (c2 === '(') {
          count += 1;
        }
        else if (c2 === ')') {
          count -= 1;

          if (!count) {
            break;
          }
        }
      }
    }
  }

  if (current.length) {
    args.push(current.join('').trim());
  }

  return args;
};
