package io.merklex.dcn.utils;

import io.merklex.dcn.network.Utils;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateContractCode {

    public static void CompileContract(File contractSource, File outputDir) throws IOException, InterruptedException {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory");
        }

        ProcessBuilder transpiler = new ProcessBuilder("node", "transpiler/run.js", contractSource.getAbsolutePath());
        Process transpilerProcess = transpiler.start();

        String contractSourceData = Utils.ReadAll(transpilerProcess.getInputStream());
        if (transpilerProcess.waitFor() != 0) {
            throw new RuntimeException("Failed to transpile: " + Utils.ReadAll(transpilerProcess.getErrorStream()));
        }

        File solidityContractFile = new File(outputDir, contractSource.getName());
        try (FileWriter writer = new FileWriter(solidityContractFile)) {
            writer.write(contractSourceData);
        }

        ProcessBuilder compile = new ProcessBuilder("solc",
                solidityContractFile.getAbsolutePath(), "--bin", "--abi", "--optimize", "--overwrite",
                "-o", outputDir.getAbsolutePath());

        Process compileProcess = compile.start();
        String errorData = Utils.ReadAll(compileProcess.getErrorStream());

        if (compileProcess.waitFor() != 0) {
            throw new RuntimeException(errorData);
        }
    }

    public static void GenerateJavaCode(File compileOut, File javaFile, String packageName) throws Exception {
        File[] binFiles = compileOut.listFiles(pathname -> pathname.toString().endsWith(".bin"));
        File[] abiFiles = compileOut.listFiles(pathname -> pathname.toString().endsWith(".abi"));

        if (binFiles == null || abiFiles == null || binFiles.length != 1 || abiFiles.length != 1) {
            throw new IllegalArgumentException("Invalid compileOut directory");
        }

        SolidityFunctionWrapperGenerator.main(new String[]{
                binFiles[0].getAbsolutePath(),
                abiFiles[0].getAbsolutePath(),
                "-p", packageName,
                "-o", javaFile.getAbsolutePath()
        });
    }

    private static final Pattern SEND_VALUE_CALLS = Pattern.compile("executeRemoteCallTransaction\\(function(, [\\w,\\s]+)\\)");
    private static final Pattern QUERY_SIGNATURES = Pattern.compile("public RemoteCall<(Tuple\\d+<[\\w,\\s]+>)>");


    public static void FixJavaCode(File contractPath, File dir, String packageName) throws Exception {
        File javaFile = new File(dir + "/" + String.join("/", packageName.split("\\.")), contractPath.getName().replace(".sol", ".java"));
        String contents;
        try (FileInputStream stream = new FileInputStream(javaFile)) {
            contents = Utils.ReadAll(stream);
        }

        contents = contents.replaceAll("public RemoteCall<TransactionReceipt>", "public static Function");

        {
            Matcher matcher = SEND_VALUE_CALLS.matcher(contents);
            while (matcher.find()) {
                String group = matcher.group(1);
                String[] args = group.split(", ");

                StringBuilder toReplace = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    toReplace.append(", \\w+ ").append(args[i]);
                }
                toReplace.append("\\) \\{");

                contents = contents.replaceAll(matcher.group(1), "");
                contents = contents.replaceAll(toReplace.toString(), ") {");
            }
        }

        contents = contents.replaceAll("return executeRemoteCallTransaction\\(function\\);", "return function;");
        contents = contents.replaceAll("public Function.*BigInteger weiValue\\) \\{.*\\}", "");

        {
            int endingBraceIndex = contents.lastIndexOf('}');
            String beforeEnd = contents.substring(0, endingBraceIndex);
            String append = "\n" +
                    "\n" +
                    "    public TransactionReceipt executeTransaction(\n" +
                    "            Function function)\n" +
                    "            throws IOException, TransactionException {\n" +
                    "        return super.executeTransaction(function);\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final Method executeTransactionMethod;\n" +
                    "\n" +
                    "    static {\n" +
                    "        try {\n" +
                    "            executeTransactionMethod = Contract.class.getDeclaredMethod(\"executeTransaction\", Function.class, BigInteger.class);\n" +
                    "            executeTransactionMethod.setAccessible(true);\n" +
                    "        } catch (NoSuchMethodException e) {\n" +
                    "            throw new RuntimeException(e);\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    public TransactionReceipt executeTransaction(\n" +
                    "            Function function, BigInteger weiValue) throws IOException, TransactionException {\n" +
                    "        try {\n" +
                    "            return (TransactionReceipt) executeTransactionMethod.invoke(this, function, weiValue);\n" +
                    "        } catch (IllegalAccessException e) {\n" +
                    "            throw new RuntimeException(\"Failed to call internal method\", e);\n" +
                    "        } catch (InvocationTargetException e) {\n" +
                    "            Throwable cause = e.getCause();\n" +
                    "            if (cause instanceof IOException) {\n" +
                    "                throw (IOException) cause;\n" +
                    "            }\n" +
                    "            else if (cause instanceof TransactionException) {\n" +
                    "                throw (TransactionException) cause;\n" +
                    "            }\n" +
                    "            throw new RuntimeException(cause);\n" +
                    "        }\n" +
                    "    }\n";
            contents = beforeEnd + append + contents.substring(endingBraceIndex);
        }

        {
            int firstImport = contents.indexOf("import");

            contents = contents.substring(0, firstImport) +
                    "import java.io.IOException;\n" +
                    "import java.lang.reflect.InvocationTargetException;\n" +
                    "import java.lang.reflect.Method;\n" +
                    "import org.web3j.protocol.exceptions.TransactionException;\n" + contents.substring(firstImport);
        }

        while (true) {
            Matcher matcher = QUERY_SIGNATURES.matcher(contents);
            if (!matcher.find()) {
                break;
            }

            String contentBefore = contents.substring(0, matcher.start());
            String returnValue = matcher.group(1);

            String contentAfter = contents.substring(matcher.end());
            contentAfter = contentAfter.replaceFirst("\\) \\{", ") throws IOException {");

            String toReplace = "return new RemoteCall<" + returnValue + ">\\(\\s*" +
                    "new Callable<" + returnValue + ">\\(\\) \\{\\s*" +
                    "@Override\\s*public " + returnValue + " call\\(\\) throws Exception \\{\\s*" +
                    "([\\w\\d<>\\s=();,.]*)" +
                    "}\\s*}\\);";
            contentAfter = contentAfter.replaceFirst(toReplace, "$1");

            contents = contentBefore + "public " + returnValue + contentAfter;
        }

        contents = contents.replaceAll(
                "public RemoteCall<(\\w+)> ([\\w\\d_]+)\\(([\\w\\d\\s,]*)\\) \\{",
                "public $1 $2($3) throws IOException {"
        );

        contents = contents.replaceAll(
                "executeRemoteCallSingleValueReturn\\(function, (\\w+).class\\)",
                "executeCallSingleValueReturn(function, $1.class)"
        );

        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(contents);
        }
    }

    public static void ContractToJava(File contractPath, File javaOutput, String packageName) {
        File compileOut;
        try {
            compileOut = Utils.TempDir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            CompileContract(contractPath, compileOut);
            GenerateJavaCode(compileOut, javaOutput, packageName);
            FixJavaCode(contractPath, javaOutput, packageName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Utils.DeleteDir(compileOut);
        }
    }
}
