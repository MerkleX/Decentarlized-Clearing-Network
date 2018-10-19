package io.merklex.dcn.utils;

import io.merklex.ether.JavaContractGenerator;
import io.merklex.ether_net.Utils;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;

import java.io.*;
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

        File abiFile = abiFiles[0];
        String className = abiFile.getName().substring(0, abiFile.getName().lastIndexOf('.'));


        String generated = "package " + packageName + ";\n\n" +
                new JavaContractGenerator(abiFile, binFiles[0]).generate(className);

        File file = new File(javaFile, packageName.replace('.', '/') + "/" + className + ".java");
        file.getParentFile().mkdirs();
        try (Writer w = new FileWriter(file)) {
            w.write(generated);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Utils.DeleteDir(compileOut);
        }
    }
}
