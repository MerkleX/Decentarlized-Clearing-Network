package io.merklex.web3.gen;

import io.merklex.web3.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class GenerateContractCode {
    private static final String SOLC_PATH = "solc";

    public static void CompileContract(File contractSource, File outputDir) throws IOException, InterruptedException {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory");
        }

        ProcessBuilder transpiler = new ProcessBuilder("tsol", contractSource.getAbsolutePath());
        Process transpilerProcess = transpiler.start();

        String contractSourceData = FileUtils.ReadAll(transpilerProcess.getInputStream());
        if (transpilerProcess.waitFor() != 0) {
            throw new RuntimeException("Failed to transpile: " + FileUtils.ReadAll(transpilerProcess.getErrorStream()));
        }

        File solidityContractFile = new File(outputDir, contractSource.getName());
        try (FileWriter writer = new FileWriter(solidityContractFile)) {
            writer.write(contractSourceData);
        }

        ProcessBuilder compile = new ProcessBuilder(SOLC_PATH,
                solidityContractFile.getAbsolutePath(), "--bin", "--abi", "--overwrite",
                "-o", outputDir.getAbsolutePath());

        Process compileProcess = compile.start();
        String errorData = FileUtils.ReadAll(compileProcess.getErrorStream());

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
            compileOut = FileUtils.TempDir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            CompileContract(contractPath, compileOut);
            GenerateJavaCode(compileOut, javaOutput, packageName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.DeleteDir(compileOut);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ContractToJava(
                new File("src/main/resources/contracts/DCN.sol"),
                new File("src/main/generated"),
                "io.merklex.dcn.contracts"
        );

        ContractToJava(
                new File("src/main/resources/contracts/ERC20.sol"),
                new File("src/main/generated"),
                "io.merklex.dcn.contracts"
        );

        ContractToJava(
                new File("src/main/resources/contracts/WethDeposit.sol"),
                new File("src/main/generated"),
                "io.merklex.dcn.contracts"
        );

        CompileContract(
                new File("src/main/resources/contracts/DCN.sol"),
                new File("contracts-compiled/DCN")
        );

        CompileContract(
                new File("src/main/resources/contracts/ERC20.sol"),
                new File("contracts-compiled/ERC20")
        );

        CompileContract(
                new File("src/main/resources/contracts/WethDeposit.sol"),
                new File("contracts-compiled/WethDeposit")
        );
    }
}
