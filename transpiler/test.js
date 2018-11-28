const fs = require('fs');
const GCCProcessor = require('./gcc_processor');
const Parser = require('solidity-parser-antlr');

let raw = fs.readFileSync('/home/plorio/merklex/trading/dcn/src/main/resources/contracts/DCN-2.sol', 'utf-8');
GCCProcessor(raw).then(source => {
  const source_first_line = raw.substr(0, raw.indexOf('\n') + 1);
  source = source.substr(source.indexOf(source_first_line));

  let ast;
  try {
    ast = Parser.parse(source, { loc: true, range: true });
  } catch(e) {
    const lines = source.split('\n');

    e.errors.forEach(error => {
      console.log(error);
      console.log(lines[error.line + 1]);
    });

    process.exit(1);
    return;
  }

  const structs = {};
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

    if (node.type === 'StructDefinition') {
      const struct = []
      const pre = `\n${tab}${TAB}`;
      const data = `struct ${node.name} {${pre}${node.members.map(mem => {
        if (mem.typeName.type === 'ArrayTypeName') {
          const { components } = mem.typeName.length;
          if (components.length > 1) {
            throw new Error('Only supports one dimentional arrays');
          }

          const type = mem.typeName.baseTypeName.name || mem.typeName.baseTypeName.namePath;
          if (!type) {
            throw new Error('type missing');
          }

          const length = evaluate(components[0]);

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

    if (node.type === 'EventDefinition'
      || node.type === 'StateVariableDeclaration'
      || node.type === 'ReturnStatement'
      || node.type === 'VariableDeclarationStatement'
      || node.type === 'StructDefinition') {
      return source.substr(node.range[0], node.range[1] - node.range[0] + 1);
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
      return `{${pre}${items.map(node => print(node, tab + TAB)).join(pre)}\n${tab}}`;
    }

    if (node.type === 'InlineAssemblyStatement') {
      return `assembly ${print(node.body, tab)}`;
    }

    if (node.type === 'AssemblyCall') {
      if (!node.arguments || !node.arguments.length) {
        return node.functionName;
      }

      if (node.functionName === 'pointer') {
        const [ struct_type, offset, index ] = node.arguments.map(print);
        const bytes = typeSize(struct_type);
        return `add(${offset}, mul(${(bytes / 32n).toString()}, ${index}))`;
      }

      if (node.functionName === 'build') {
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

          if (bits === 0) {
            parts.push(arg);
          }
          else {
            parts.push(`/* ${member.name} */ mul(${arg}, 0x${(1n<<bits_remaining).toString(16)})`);
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
              if (bits === 0) {
                throw new Error('something went wrong');
              }

              return data;
            }

            const mask = '0x' + ((1n << (length * 8n)) - 1n).toString(16);
            if (bits === 0) {
              return `and(${data}, ${mask})`;
            }
            else {
              return `and(div(${data}, 0x${(1n << bits).toString(16)}), ${mask})`;
            }
          }
        }

        throw new Error('could not find ' + member_name + ' in word ' + word + ' of ' + struct_type);
      }

      return node.functionName + '(' + node.arguments.map(arg => print(arg, tab)).join(', ') + ')';
    }

    if (node.type === 'AssemblyLocalDefinition') {
      return `let ${node.names[0].name} := ${print(node.expression, tab)}`;
    }

    if (node.type === 'AssemblyAssignment') {
      return `${node.names[0].name} := ${print(node.expression, tab)}`;
    }

    if (node.type === 'DecimalNumber') {
      return node.value;
    }

    if (node.type === 'AssemblyIf') {
      return `if ${print(node.condition)} ${print(node.body, tab)}`;
    }

    return '<error '+node.type+'>';
  }

  console.log(print(ast));
});
