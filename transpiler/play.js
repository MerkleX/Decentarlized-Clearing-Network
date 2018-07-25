const Transpiler = require('./transpiler');

const fs = require('fs');
const data = fs.readFileSync('contract/MerkleX2.sol', 'utf-8');

const res = Transpiler(data);
console.log(res);
