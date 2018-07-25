const count = 127n;

number_of_commits = count + (count % 2n);

const setup_tx_cost = 21000n + (number_of_commits * 128n) / 8n * 68n;
const setup_insert_cost = (number_of_commits * 128n) / 256n * 5000n;
const setup_cost = setup_tx_cost + setup_insert_cost;

const apply_tx_cost = 21000n;
const apply_run_cost = number_of_commits * 5000n * 2n;
const apply_cost = apply_tx_cost + apply_run_cost;

const total_cost = setup_cost + apply_cost;

console.log(total_cost, total_cost / count);
