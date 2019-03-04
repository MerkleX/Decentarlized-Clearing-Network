const Transpiler = require('./transpiler');

const fs = require('fs');
const data = fs.readFileSync(process.argv[2], 'utf-8');

Transpiler(data)
  .then(res => console.log(res))
  .catch(err => {
    console.error(err);
    process.exit(1);
  });

