const fs = require('fs');
const GCCProcessor = require('./gcc_processor');
const Parser = require('solidity-parser-antlr');
const keccak256 = require('js-sha3').keccak256;

module.exports = function(raw) {
  if (raw.indexOf('#define TRANSPILE') === -1) {
    return Promise.resolve(raw);
  }

  return GCCProcessor(raw).then(source => {
    const source_first_line = raw.substr(0, raw.indexOf('\n') + 1);
    source = source.substr(source.indexOf(source_first_line));

    let ast;
    try {
      ast = Parser.parse(source, { loc: true, range: true });
    } catch(e) {
      const lines = source.split('\n');

      console.log(e)

      e.errors.forEach(error => {
        console.log(error);
        console.log(lines[error.line - 1]);
      });

      process.exit(1);
      return;
    }

    const structs = {};
    const events = {};
    const TAB = '  ';

    function evaluate(node) {
      if (node.type === 'NumberLiteral') {
        return BigInt(node.number);
      }

      if (node.type === 'BinaryOperation') {
        if (node.operator === '**') {
          return evaluate(node.left) ** evaluate(node.right);
        }
      }

      throw new Error('Not implemented ' + node.type);
    }

    function typeSize(type) {
      if (!type) {
        throw new Error('invalid type');
      }
      if (type.startsWith('uint') || type.startsWith('int')) {
        return BigInt(type.substr(type.indexOf('int') + 3)) / 8n;
      }

      if (structs[type]) {
        return structs[type].bytes;
      }

      if (type === 'u256') {
        return 32n;
      }

      if (type === 'address') {
        return 20n;
      }

      throw new Error('unknown type ' + type);
    }

    function print(node, tab) {
      const raw_input = source.substr(node.range[0], node.range[1] - node.range[0] + 1);
      tab = tab || '';

      if (node.type === 'SourceUnit') {
        return node.children.map(node => print(node, tab)).join('\n' + tab);
      }

      if (node.type === 'PragmaDirective') {
        return `pragma ${node.name} ${node.value};`;
      }

      if (node.type === 'ContractDefinition') {
        const pre = '\n' + tab + TAB;
        return `contract ${node.name} {${pre}${ node.subNodes.map(node => print(node, tab + TAB)).join(pre) }\n${tab}}`;
      }

      if (node.type === 'WhileStatement') {
        return `while (${print(node.condition)})\n${tab}${print(node.body, tab + TAB)}`;
      }

      if (node.type === 'BooleanLiteral') {
        return node.value ? 'true' : 'false';
      }

      if (node.type === 'StructDefinition') {
        const struct = []
        const pre = `\n${tab}${TAB}`;
        const data = `struct ${node.name} {${pre}${node.members.map(mem => {
          if (mem.typeName.type === 'ArrayTypeName') {
            const array_length = mem.typeName.length;

            const type = mem.typeName.baseTypeName.name || mem.typeName.baseTypeName.namePath;
            let length;

            const { components } = array_length;
            if (components) {
              if (components.length > 1) {
                throw new Error('Only supports one dimentional arrays');
              }

              if (!type) {
                throw new Error('type missing');
              }

              length = evaluate(components[0]);
            }
            else if (array_length.type === 'NumberLiteral') {
              length = BigInt(array_length.number);
            }
            else {
              throw new Error('Unable to parse array length');
            }


            struct.push({ name: mem.name, type, length });
            return `${type}[${length}] ${mem.name};`;
          }
          else {
            const type = mem.typeName.name || mem.typeName.namePath;
            if (!type) {
              throw new Error('type missing');
            }
            struct.push({ name: mem.name, type });
            return `${mem.typeName.name} ${mem.name};`;
          }
        }).join(pre)}\n${tab}}`;

        const reducer = (value, item) => {
          return value + typeSize(item.type) * (item.length || 1n);
        };

        structs[node.name] = {
          bytes: struct.reduce(reducer, 0n),
          members: struct,
        };

        return data;
      }

      if (node.type === 'EventDefinition') {
        const name = node.name;
        const params = node.parameters.parameters.map(param => {
          const type = param.typeName.name;
          const name = param.name;
          return { type, name };
        });

        const hash_hex = '0x' + keccak256(`${name}(${params.map(p => p.type).join(',')})`);

        events[name] = {
          name,
          hash_hex,
          parameters: params,
        };

        return raw_input;
      }

      if (node.type === 'StateVariableDeclaration'
        || node.type === 'ElementaryTypeName'
        || node.type === 'Identifier'
        || node.type === 'NumberLiteral'
        || node.type === 'ReturnStatement'
        || node.type === 'StructDefinition') {
        return raw_input;
      }

      if (node.type === 'VariableDeclarationStatement') {
        if (node.variables.length != 1) {
          throw new Error('only support one var per var declaration statement');
        }

        const v = node.variables[0];
        if (!v.typeName.length) {
          return raw_input;
        }

        return `\n${tab}${print(v.typeName.baseTypeName)}[${print(v.typeName.length)}] ${v.storageLocation} ${v.name};`;
      }

      if (node.type === 'FunctionCall') {
        if (node.expression.name === 'sizeof') {
          if (node.arguments.length !== 1) {
            throw new Error('sizeof expects one argument');
          }

          const arg = print(node.arguments[0], tab);
          return typeSize(arg);
        }
      }

      if (node.type === 'FunctionDefinition') {
        const body = print(node.body, tab);

        const name = node.isConstructor ? (node.name || 'constructor') : 'function ' + node.name;
        const params = source.substr(node.parameters.range[0], node.parameters.range[1]-node.parameters.range[0]+1);
        const modifiers = node.visibility + (node.stateMutability ? ' ' + node.stateMutability : '');
        const returnValues = node.returnParameters
          ? '\n' + tab + source.substr(node.returnParameters.range[0], node.returnParameters.range[1]-node.returnParameters.range[0]+1)
          : '';

        return `\n${tab}${name}${params} ${modifiers} ${returnValues} ${print(node.body, tab)}`
      }

      if (node.type === 'Block' || node.type === 'AssemblyBlock') {
        const pre = '\n' + tab + TAB;
        const items = node.statements || node.operations;
        if (items.length === 0) {
          return '{}';
        }
        return `{${pre}${items.map(node => print(node, tab + TAB)).join(pre)}\n${tab}}`;
      }

      if (node.type === 'InlineAssemblyStatement') {
        return `assembly ${print(node.body, tab)}`;
      }

      if (node.type === 'AssemblyCall') {
        if (node.functionName === 'stop') {
          return 'stop()';
        }

        if (!node.arguments || !node.arguments.length) {
          return node.functionName;
        }

        if (node.functionName === 'pointer') {
          const [ struct_type, offset, index, ...other ] = node.arguments.map(print);
          const bytes = typeSize(struct_type);
          const words = bytes / 32n;

          if (other.length > 0) {
            throw new Error('Too many arguments: ' + raw_input);
          }

          if (bytes % 32n !== 0n) {
            throw new Error('Type must be word mulitple');
          }

          if (words === 1n) {
            return `add(${offset}, ${index})`;
          }

          return `add(${offset}, mul(${words.toString()}, ${index}))`;
        }

        if (node.functionName === 'pointer_attr') {
          const [ struct_type, pointer, attr_name ] = node.arguments.map(print);

          const struct = structs[struct_type];
          if (!struct) {
            throw new Error('Cannot build struct for ' + struct_type);
          }

          let total_size = 0n;
          let found = false;

          let i;
          for (i = 0; i < struct.members.length; ++i) {
            const member = struct.members[i];

            if (member.name === attr_name) {
              found = true;
              break;
            }

            total_size += typeSize(member.type) * (member.length || 1n);
          }

          if (!found) {
            throw new Error('failed to find ' + attr_name + ' in ' + struct_type);
          }

          if ((total_size % 32n) !== 0n) {
            throw new Error('not on a word multiple');
          }

          return `add(${pointer}, ${total_size / 32n})`;
        }

        if (node.functionName === 'byte_offset') {
          const [ struct_type, attr_name ] = node.arguments.map(print);

          const struct = structs[struct_type];
          if (!struct) {
            throw new Error('Cannot build struct for ' + struct_type);
          }

          let total_size = 0n;
          let found = false;

          let i;
          for (i = 0; i < struct.members.length; ++i) {
            const member = struct.members[i];

            if (member.name === attr_name) {
              found = true;
              break;
            }

            total_size += typeSize(member.type) * (member.length || 1n);
          }

          if (!found) {
            throw new Error('failed to find ' + attr_name + ' in ' + struct_type);
          }

          return total_size;
        }

        if (node.functionName === 'build' || node.functionName === 'build_with_mask') {
          const mask = node.functionName === 'build_with_mask';
          const args = node.arguments.map(print);

          const struct_type = args[0];
          const word_bytes = BigInt(args[1]) * 32n;

          const struct = structs[struct_type];
          if (!struct) {
            throw new Error('Cannot build struct for ' + struct_type);
          }

          let total_size = 0n;

          let i;
          for (i = 0; i < struct.members.length; ++i) {
            const member = struct.members[i];

            if (total_size >= word_bytes) {
              break;
            }

            total_size += typeSize(member.type) * (member.length || 1n);
          }

          if (total_size !== word_bytes) {
            throw new Error('struct members to do lie on a word boundary');
          }

          const parts = [];
          let arg_index = 2;

          let bits_remaining = 256n;
          for (; i < struct.members.length && arg_index < args.length; ++i, ++arg_index) {
            const member = struct.members[i]
            const bits = (typeSize(member.type) * (member.length || 1n)) * 8n;
            bits_remaining = bits_remaining - bits;

            if (bits < 0) {
              throw new Error('member ' + members.name + ' breaks word boundary');
            }

            const arg = args[arg_index];

            if (+arg === 0) {
              continue;
            }

            if (mask) {
              const arg_mask = '0x' + ((1n<<bits) - 1n).toString(16);
              if (bits_remaining === 0n) {
                parts.push(`/* ${member.name} */ and(${arg}, ${arg_mask})`);
              }
              else {
                parts.push(`/* ${member.name} */ mul(and(${arg}, ${arg_mask}), 0x${(1n<<bits_remaining).toString(16)})`);
              }
            }
            else {
              if (bits_remaining === 0n) {
                parts.push(`/* ${member.name} */ ${arg}`);
              }
              else {
                parts.push(`/* ${member.name} */ mul(${arg}, 0x${(1n<<bits_remaining).toString(16)})`);
              }
            }
          }

          return parts.reduce((current, part) => {
            const pre = '\n' + tab + TAB;

            if (!current) {
              return pre + part;
            }

            return `or(${current}, ${pre}${part})`;
          }, null);
        }

        if (node.functionName === 'attr') {
          const [ struct_type, word, data, member_name ] = node.arguments.map(print);

          const struct = structs[struct_type];
          if (!struct) {
            throw new Error('Cannot find struct ' + struct_type);
          }

          const word_bytes = BigInt(word) * 32n;

          let total_size = 0n;

          let i;
          for (i = 0; i < struct.members.length; ++i) {
            const member = struct.members[i];

            if (total_size >= word_bytes) {
              break;
            }

            total_size += typeSize(member.type) * (member.length || 1n);
          }

          if (total_size !== word_bytes) {
            throw new Error('struct members to do lie on a word boundary');
          }


          let bits_remaining = 256n;
          for (; i < struct.members.length; ++i) {
            const member = struct.members[i]
            const bits = (typeSize(member.type) * (member.length || 1n)) * 8n;
            bits_remaining = bits_remaining - bits;

            if (bits < 0) {
              throw new Error('member ' + members.name + ' breaks word boundary');
            }

            if (member.name === member_name) {
              const length = typeSize(member.type);

              if (length === 32n) {
                if (bits_remaining !== 0n) {
                  throw new Error(`full width member "${member_name}" should span word, remain: ${bits_remaining}`);
                }

                return data;
              }

              const mask = '0x' + ((1n << (length * 8n)) - 1n).toString(16);
              if (bits_remaining === 0n) {
                return `and(${data}, ${mask})`;
              }
              else {
                return `and(div(${data}, 0x${(1n << bits_remaining).toString(16)}), ${mask})`;
              }
            }
          }

          throw new Error('could not find ' + member_name + ' in word ' + word + ' of ' + struct_type);
        }

        if (node.functionName === 'fn_hash') {
          if (node.arguments.length !== 1 || node.arguments[0].type !== 'StringLiteral') {
            throw new Error('hash4 expects 1 string arguments');
          }
          const str = node.arguments[0].value;
          const end = '00000000000000000000000000000000000000000000000000000000';
          return `/* fn_hash("${str}") */ 0x${keccak256(str).substr(0, 8)}${end}`;
        }

        if (node.functionName === 'log_event') {
          const [ name, memory, ...args ] = node.arguments.map(print);

          const event = events[name];
          if (!event) {
            throw new Error('fould not find event ' + name);
          }

          if (event.parameters.length !== args.length) {
            throw new Error('event ' + name + ' expected ' + event.parameters.length + ' parameters but got ' + args.length);
          }

          const parts = ['', `/* Log event: ${name} */`];

          for (let i = 0; i < args.length; ++i) {
            const offset = BigInt(i) * 32n;
            const ptr = offset === 0n ? memory : `add(${memory}, ${offset})`;
            parts.push(`mstore(${ptr}, ${args[i]})`);
          }

          parts.push(`log1(${memory}, ${args.length * 32}, /* ${name} */ ${event.hash_hex})`);

          return parts.join('\n' + tab);
        }

        if (node.functionName === 'mask_out') {
          const [ struct_name, word, ...args ] = node.arguments.map(print);

          const struct = structs[struct_name];
          if (!struct) {
            throw new Error('Could not find struct ' + struct_name);
          }

          const word_bytes = BigInt(word) * 32n;
          let total_size = 0n;

          let i;
          for (i = 0; i < struct.members.length; ++i) {
            const member = struct.members[i];

            if (total_size >= word_bytes) {
              break;
            }

            total_size += typeSize(member.type) * (member.length || 1n);
          }

          if (total_size !== word_bytes) {
            throw new Error('struct members to do lie on a word boundary');
          }

          let mask = (1n << 256n) - 1n;

          const visited = {};
          let visited_count = 0;

          let bits_remaining = 256n;
          for (; i < struct.members.length; ++i) {
            const member = struct.members[i];

            const bits = typeSize(member.type) * (member.length || 1n) * 8n;
            bits_remaining -= bits;

            if (bits_remaining < 0) {
              break;
            }

            const name = member.name;
            if (args.indexOf(name) !== -1) {
              if (visited[name]) {
                continue;
              }
              visited[name] = true;
              visited_count += 1;
            }
            else {
              continue;
            }

            const member_mask = ((1n << bits) - 1n) << bits_remaining;
            mask = mask ^ member_mask;
          }

          if (visited_count !== args.length) {
            throw new Error('could not find all mask elements in struct');
          }

          return '0x' + mask.toString(16);
        }

        if (node.functionName === 'sizeof') {
          if (node.arguments.length !== 1) {
            throw new Error('sizeof expects one argument');
          }

          const arg = print(node.arguments[0], tab);
          return typeSize(arg);
        }

        if (node.functionName === 'const_add') {
          const args = node.arguments.map(print);

          return args.reduce((total, item) => {
            return total + BigInt(item);
          }, 0n);
        }

        if (node.functionName === 'const_sub') {
          const args = node.arguments.map(print);
          if (args.length != 2) {
            throw new Error('const_sub requires 2 args');
          }

          return BigInt(args[0]) - BigInt(args[1]);
        }

        return node.functionName + '(' + node.arguments.map(arg => print(arg, tab)).join(', ') + ')';
      }

      if (node.type === 'AssemblyLocalDefinition') {
        return `let ${node.names[0].name} := ${print(node.expression, tab)}`;
      }

      if (node.type === 'AssemblyAssignment') {
        return `${node.names[0].name} := ${print(node.expression, tab)}`;
      }

      if (node.type === 'DecimalNumber' || node.type === 'HexNumber') {
        return node.value;
      }

      if (node.type === 'AssemblyIf') {
        return `if ${print(node.condition)} ${print(node.body, tab)}`;
      }

      if (node.type === 'AssemblyFor') {
        return `for ${print(node.pre, tab)} ${print(node.condition)} ${print(node.post, tab)} ${print(node.body, tab)}`;
      }

      if (node.type === 'AssemblySwitch') {
        return `switch ${print(node.expression)}\n${tab}${TAB}${node.cases.map(c => {
          if (c.default) {
            return `default ${print(c.block, tab + TAB)}`;
          }
          return `case ${print(c.value)} ${print(c.block, tab + TAB)}`;
        }).join(`\n${tab}${TAB}`)}`;
      }

      console.error('Error', node.type, node);
      return '<error '+node.type+'>';
    }

    const result = print(ast);

    Object.keys(structs).forEach(struct => {
      console.error('Struct size:', struct, typeSize(struct));
    });

    return result;
  });
};
