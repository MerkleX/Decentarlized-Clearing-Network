const Transpiler = require('./transpiler');

const fs = require('fs');
const data = fs.readFileSync(process.argv[2], 'utf-8');

const res = Transpiler(data);
console.log(res);
