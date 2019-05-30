package io.merklex.web3.gen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.merklex.web3.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JavaContractGenerator {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JsonNode abi;
    private final String binaryData;

    public JavaContractGenerator(File abiPath, File binaryPath) throws IOException {
        abi = MAPPER.readTree(abiPath);
        binaryData = FileUtils.ReadAll(binaryPath).trim();
    }

    private static class ConstantFunctionReturn {
        String functionName;
        JsonNode attributes;
    }

    private static final String[] IMPORTS = new String[]{
            "org.web3j.abi.*;",
            "org.web3j.abi.datatypes.*;",
            "org.web3j.abi.datatypes.generated.*;",
            "org.web3j.protocol.Web3j;",
            "org.web3j.protocol.core.DefaultBlockParameterName;",
            "org.web3j.protocol.core.methods.request.Transaction;",
            "org.web3j.protocol.core.methods.request.Transaction;",
            "org.web3j.protocol.core.methods.response.Log;",
            "org.web3j.tx.Contract;",
            "java.io.IOException;",
            "java.math.BigInteger;",
            "java.util.Arrays;",
            "java.util.Collections;",
            "java.util.List;",
            "org.web3j.utils.Numeric;"
    };

    public String generate(String className) {
        JavaCodeGen gen = new JavaCodeGen();

        for (String anImport : IMPORTS) {
            gen.importLine().append(anImport).end();
        }

        gen.space();

        ArrayList<ConstantFunctionReturn> constants = new ArrayList<>();

        JavaCodeGen.Block block = gen.cls(className);

        block.line().append("public static final String BINARY = \"").append(binaryData).append("\";").end();

        for (JsonNode item : abi) {
            String type = item.get("type").asText();


            if ("function".equals(type)) {
                function(block, item);

                if (item.get("constant").asBoolean(false)) {
                    ConstantFunctionReturn c = new ConstantFunctionReturn();
                    c.functionName = item.get("name").asText();
                    c.attributes = item.get("outputs");
                    constants.add(c);

                    constantFunction(block, item);
                }
            }
            else if ("constructor".equals(type)) {
                constructor(block, item);
            }
            else if ("event".equals(type)) {
                event(block, item);
            }
        }

        generateReturnTypes(block, constants);

        block.end();

        return gen.toString();
    }

    private void event(JavaCodeGen.Block block, JsonNode item) {
        String eventName = item.get("name").asText();
        JsonNode inputs = item.get("inputs");

        block = block.staticClass(eventName);
        {
            for (JsonNode input : inputs) {
                String name = input.get("name").asText();
                String type = input.get("type").asText();
                block.line().append("public ").append(ToJavaType(type)).append(" ").append(name).append(";").end();
            }
        }
        block = block.end();

        block.line().append("public static final Event ").append(eventName).append("_EVENT = new Event(\"").append(eventName).append("\",").end();
        JavaCodeGen.Block body = block.tabbed();
        functionTypedReferences(body, inputs).end();
        block.line().append(");").end();

        String hashName = eventName + "_EVENT_HASH";

        block.line().append("public static final String ").append(hashName).append(" = EventEncoder.encode(").append(eventName).append("_EVENT").append(");").end();

        block = block.publicStaticMethod("Extract" + eventName, eventName)
                .arg("log", "Log").end();
        {
            block.line().append("List<String> topics = log.getTopics();").end();
            block = block.line().append("if (topics.size() == 0 || !").append(hashName).append(".equals(topics.get(0))) {").end().block();
            block.line().append("return null;").end();
            block = block.end();

            block.line().append("EventValues values = Contract.staticExtractEventParameters(").append(eventName).append("_EVENT, log);").end();

            block.line().append(eventName).append(" event = new ").append(eventName).append("();").end();

            int nextIndexedArg = 0;
            int nextNonIndexedArg = 0;

            for (int i = 0; i < inputs.size(); i++) {
                JsonNode input = inputs.get(i);
                String name = input.get("name").asText();
                String type = input.get("type").asText();

                if (input.get("indexed").asBoolean()) {
                    block.line().append("event.").append(name).append(" = ")
                            .append(ConvertType(type, "values.getIndexedValues().get(" + nextIndexedArg + ").getValue()"))
                            .append(";").end();
                    nextIndexedArg++;
                }
                else {
                    block.line().append("event.").append(name).append(" = ")
                            .append(ConvertType(type, "values.getNonIndexedValues().get(" + nextNonIndexedArg + ").getValue()"))
                            .append(";").end();
                    nextNonIndexedArg++;
                }
            }
        }

        block.line().append("return event;").end();

        block.end();
    }

    private void constructor(JavaCodeGen.Block block, JsonNode item) {
        JavaCodeGen.Arguments args = block.publicStaticMethod("DeployData", "String");

        JsonNode inputs = item.get("inputs");
        arguments(args, inputs);

        block = args.end();

        block.line().append("String encodedConstructor = FunctionEncoder.encodeConstructor(").end();
        JavaCodeGen.Block body = block.tabbed();
        functionTypedArgs(body, inputs).end();
        block.line().append(");").end();

        block.line().append("return BINARY + encodedConstructor;").end();

        block.end();
    }

    private void generateReturnTypes(JavaCodeGen.Block block, ArrayList<ConstantFunctionReturn> constants) {
        for (ConstantFunctionReturn constant : constants) {
            block = block.staticClass(ReturnValueName(constant.functionName));

            for (JsonNode attribute : constant.attributes) {
                String type = attribute.get("type").asText();
                String name = attribute.get("name").asText();
                block.line().append("public ").append(ToJavaType(type)).append(" ").append(or(name, "value")).append(";").end();
            }

            block = block.end();
        }
    }

    private static int IntSize(String solidityType) {
        if (solidityType.startsWith("int")) {
            return Integer.parseInt(solidityType.substring(3));
        }
        else if (solidityType.startsWith("uint")) {
            return Integer.parseInt(solidityType.substring(4));
        }
        return 0;
    }

    private static String ArrayBuilderMethod(int itemCount) {
        if (itemCount == 1) {
            return "Collections.singletonList";
        }
        if (itemCount == 0) {
            return "Collections.emptyList";
        }
        return "Arrays.asList";
    }

    public static String ABITypeConstructor(String solidityType, String arg) {
        if (solidityType.startsWith("uint") && !solidityType.equals("uint")) {
            int bits = Integer.parseInt(solidityType.substring("uint".length()));
            return "UnsignedNumberType(" + bits + ", " + arg + ")";
        }

        return ABIGeneratedType(solidityType) + "(" + cast(arg, solidityType) + ")";
    }

    private static String ABIGeneratedType(String solidityType) {
        if (solidityType.equals("string")) {
            return "org.web3j.abi.datatypes.Utf8String";
        }

        if (solidityType.equals("uint")) {
            return "org.web3j.abi.datatypes.generated.Uint256";
        }

        if (solidityType.startsWith("uint")) {
            return "org.web3j.abi.datatypes.generated.Uint" + IntSize(solidityType);
        }

        if (solidityType.equals("int")) {
            return "org.web3j.abi.datatypes.generated.Int256";
        }

        if (solidityType.startsWith("int")) {
            return "org.web3j.abi.datatypes.generated.Int" + IntSize(solidityType);
        }

        if (solidityType.equals("bytes")) {
            return "org.web3j.abi.datatypes.DynamicBytes";
        }

        if (solidityType.startsWith("bytes")) {
            return "org.web3j.abi.datatypes.generated.Bytes" + Integer.parseInt(solidityType.substring(5));
        }

        if (solidityType.equals("address")) {
            return "org.web3j.abi.datatypes.Address";
        }

        if (solidityType.equals("bool")) {
            return "org.web3j.abi.datatypes.Bool";
        }

        throw new IllegalArgumentException("Unknown solidity type: " + solidityType);
    }

    private void function(JavaCodeGen.Block block, JsonNode item) {
        String fnName = item.get("name").asText();
        JavaCodeGen.Arguments args = block.publicStaticMethod(fnName, "Function");

        JsonNode inputs = item.get("inputs");
        arguments(args, inputs);

        JavaCodeGen.Block fn = args.end();

        JavaCodeGen.Block body = fn.line().append("return new Function(").end().tabbed();
        body.line().append("\"").append(fnName).append("\"").append(",").end();
        functionTypedArgs(body, inputs).append(",").end();
        functionTypedReferences(body, item.get("outputs")).end();


        fn.line().append(");").end();
        fn.end();

        {
            int unsignedBigInts = 0;
            int signedBigInts = 0;
            for (JsonNode input : inputs) {
                String type = input.get("type").asText();
                if (IntSize(type) > 64) {
                    if (type.startsWith("u")) {
                        unsignedBigInts++;
                    }
                    else {
                        signedBigInts++;
                    }
                }
            }

            /* support simplified caller */
            if (unsignedBigInts > 0 && signedBigInts == 0) {
                {
                    args = block.publicStaticMethod(fnName, "Function");
                    for (int i = 0; i < inputs.size(); i++) {
                        JsonNode input = inputs.get(i);
                        String type = input.get("type").asText();
                        String name = input.get("name").asText();

                        if (IntSize(type) > 64) {
                            args.arg(or(name, "arg" + i), "long");
                        }
                        else {
                            args.arg(or(name, "arg" + i), ToJavaType(type));
                        }
                    }
                    fn = args.end();
                }

                body = fn.line().append("return ").append(fnName).append("(").end().tabbed();
                for (int i = 0; i < inputs.size(); i++) {
                    JsonNode input = inputs.get(i);
                    JavaCodeGen.Line line = body.line();

                    String type = input.get("type").asText();
                    String name = input.get("name").asText();

                    if (i > 0) {
                        line.append(", ");
                    }

                    if (IntSize(type) > 64) {
                        line.append("new BigInteger(Long.toUnsignedString(").append(or(name, "arg" + i)).append("))");
                    }
                    else {
                        line.append(or(name, "arg" + i));
                    }

                    line.end();
                }

                fn.line().append(");").end();
                fn.end();
            }
        }
    }

    private void arguments(JavaCodeGen.Arguments args, JsonNode inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            JsonNode input = inputs.get(i);
            String type = input.get("type").asText();
            String name = input.get("name").asText();
            args.arg(or(name, "arg" + i), ToJavaType(type));
        }
    }

    private void constantFunction(JavaCodeGen.Block block, JsonNode item) {
        String fnName = item.get("name").asText();
        String returnType = ReturnValueName(fnName);

        JavaCodeGen.Arguments args = block.publicStaticMethod("query_" + fnName, returnType);
        args.arg("contractAddress", "String");
        args.arg("web3j", "Web3j");
        args.arg("function", "Function");

        args.Throws("IOException");

        JavaCodeGen.Block fn = args.end();

        fn.line().append("String encodedFunction = FunctionEncoder.encode(function);").end();
        fn.line().append("org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(").end();

        JavaCodeGen.Block body = fn.tabbed();
        body.line().append("Transaction.createEthCallTransaction(\"0x0000000000000000000000000000000000000000\", contractAddress, encodedFunction),").end();
        body.line().append("DefaultBlockParameterName.LATEST").end();
        fn.line().append(").send();").end();

        fn.line().append("String value = ethCall.getValue();").end();
        fn.line().append("List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());").end();

        fn.line().append(returnType).append(" returnValue = new ").append(returnType).append("();").end();

        JsonNode outputs = item.get("outputs");
        for (int i = 0; i < outputs.size(); i++) {
            JsonNode output = outputs.get(i);

            String name = output.get("name").asText();
            String type = output.get("type").asText();
            fn.line().append("returnValue.").append(or(name, "value")).append(" = ")
                    .append(ConvertType(type, "values.get(" + i + ").getValue()")).append(";").end();
        }

        fn.line().append("return returnValue;").end();

        fn.end();
    }

    private static String or(String a, String b) {
        return (a == null || a.length() == 0) ? b : a;
    }

    private static String ConvertType(String solidityType, String item) {
        String javaType = ToJavaType(solidityType);
        if (javaType.equals("int")) {
            return "((BigInteger) " + item + ").intValue()";
        }

        if (javaType.equals("long")) {
            return "((BigInteger) " + item + ").longValue()";
        }

        if (javaType.equals("BigInteger")) {
            return "(BigInteger) " + item;
        }

        if (javaType.equals("String")) {
            return "(String) " + item;
        }

        if (javaType.equals("boolean")) {
            return "(Boolean) " + item;
        }

        throw new IllegalArgumentException("Unknown type: " + solidityType);
    }

    private static String ToJavaType(String solidityType) {
        int intSize = IntSize(solidityType);
        if (intSize != 0) {
            if (intSize <= 32) {
                return "int";
            }

            if (intSize <= 64) {
                return "long";
            }

            return "BigInteger";
        }

        if (solidityType.equals("bool")) {
            return "boolean";
        }

        return "String";
    }

    private static String ReturnValueName(String fnName) {
        return NameConverter.Capitalize(NameConverter.SnakeToCamel(fnName)) + "ReturnValue";
    }

    private JavaCodeGen.Line functionTypedArgs(JavaCodeGen.Block block, JsonNode items) {
        if (items.size() == 0) {
            return block.line().append(ArrayBuilderMethod(items.size())).append("()");
        }

        block.line().append(ArrayBuilderMethod(items.size())).append("(").end();

        block = block.tabbed();

        for (int i = 0; i < items.size(); i++) {
            JsonNode input = items.get(i);
            String type = input.get("type").asText();
            String name = input.get("name").asText();

            JavaCodeGen.Line line = block.line();
            if (i != 0) {
                line.append(", ");
            }
            line.append("new ").append(ABITypeConstructor(type, or(name, "arg" + i)));
            line.end();
        }

        return block.end().line().append(")");
    }

    private JavaCodeGen.Line functionTypedReferences(JavaCodeGen.Block block, JsonNode items) {
        if (items.size() == 0) {
            return block.line().append(ArrayBuilderMethod(items.size())).append("()");
        }

        block.line().append(ArrayBuilderMethod(items.size())).append("(").end();

        block = block.tabbed();

        for (int i = 0; i < items.size(); i++) {
            JsonNode input = items.get(i);
            String type = input.get("type").asText();

            JavaCodeGen.Line line = block.line();
            if (i != 0) {
                line.append(", ");
            }

            JsonNode indexed = input.get("indexed");
            if (indexed != null && indexed.asBoolean()) {
                line.append("new TypeReference<").append(ABIGeneratedType(type)).append(">(true) {}");
            }
            else {
                line.append("new TypeReference<").append(ABIGeneratedType(type)).append(">() {}");
            }

            line.end();
        }

        return block.end().line().append(")");
    }

    private static String cast(String value, String type) {
        if ("bytes".equals(type)) {
            return "Numeric.hexStringToByteArray(" + value + ")";
        }
        return value;
    }
}
