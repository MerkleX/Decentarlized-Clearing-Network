const spawn = require('child_process').spawn;

module.exports = function(source) {
  return new Promise((resolve, reject) => {
    const ps = spawn('gcc', ['-E', '-P', '-CC', '-']);

    const data = [];

    ps.stdin.setEncoding('utf-8');
    ps.stdout.setEncoding('utf-8');

    ps.stdout.on('data', buf => {
      data.push(buf);
    });

    ps.stdout.on('end', () => {
      resolve(data.join(''));
    });

    ps.on('error', err => {
      reject(err);
    });

    ps.stdin.write(source);
    ps.stdin.end();
  });
};
