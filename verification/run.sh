set -e

rm -r out/$1 || true
mkdir -p out/$1/dapp/{out,src}
mkdir -p out/$1/src

tsol tests/$1/source.sol > out/$1/dapp/src/source.sol 2> /dev/null
cd out/$1/dapp
solc src/source.sol --overwrite --combined-json=abi,bin,bin-runtime,srcmap,srcmap-runtime,ast > out/source.sol.json
cd ../../..
cp tests/$1/spec.md out/$1/src/
cp ref/config.json out/$1/
cp ref/prelude.smt2.md out/$1/src
