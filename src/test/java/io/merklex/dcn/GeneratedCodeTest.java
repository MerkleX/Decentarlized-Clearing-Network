package io.merklex.dcn;

import io.merklex.dcn.network.Utils;
import io.merklex.dcn.utils.GenerateContractCode;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class GeneratedCodeTest {
    @Test
    public void generatedCodeShouldBeLatest() throws IOException {
        File compareOut = Utils.TempDir();

        try {
            GenerateContractCode.ContractToJava(
                    new File("src/main/resources/contracts/DCN.sol"),
                    compareOut,
                    "out"
            );

            File[] files = new File(compareOut, "out").listFiles();
            assert files != null;
            File contractPath = files[0];

            String expected = Utils.ReadAll(new FileInputStream(contractPath));
            expected = expected.replaceFirst("package [\\w_.]+;", "");
            expected = expected.replaceFirst("(static final String BINARY = \"[0-9abcdef]+)[0-9abcdef]{68}(\";)", "$1$2");

            String actual = Utils.ReadAll(new FileInputStream("src/main/generated/io/merklex/dcn/contracts/DCN.java"));
            actual = actual.replaceFirst("package [\\w_.]+;", "");
            actual = actual.replaceFirst("(static final String BINARY = \"[0-9abcdef]+)[0-9abcdef]{68}(\";)", "$1$2");

            Assert.assertEquals(expected, actual);
        } finally {
            Utils.DeleteDir(compareOut);
        }
    }

    @Test
    public void compiledOutputShouldBeLatest() throws IOException, InterruptedException {
        File compareOut = Utils.TempDir();

        try {
            GenerateContractCode.CompileContract(
                    new File("src/main/resources/contracts/DCN.sol"),
                    compareOut
            );

            File[] expectedFiles = compareOut.listFiles();
            Assert.assertNotNull(expectedFiles);

            File[] actualFiles = new File("contracts-compiled/DCN").listFiles();
            Assert.assertNotNull(actualFiles);

            Arrays.sort(expectedFiles);
            Arrays.sort(actualFiles);

            Assert.assertEquals(expectedFiles.length, actualFiles.length);

            for (int i = 0; i < expectedFiles.length; i++) {
                try (FileInputStream expectedStream = new FileInputStream(expectedFiles[i]);
                     FileInputStream actualStream = new FileInputStream(actualFiles[i])) {
                    String expectedData = Utils.ReadAll(expectedStream);
                    String actualData = Utils.ReadAll(actualStream);

                    if (expectedFiles[i].getName().endsWith(".bin")) {
                        expectedData = expectedData.substring(0, expectedData.length() - 68);
                        actualData = actualData.substring(0, actualData.length() - 68);
                    }

                    Assert.assertEquals(expectedData, actualData);
                }
            }
        } finally {
            Utils.DeleteDir(compareOut);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        GenerateContractCode.ContractToJava(
                new File("src/main/resources/contracts/DCN.sol"),
                new File("src/main/generated"),
                "io.merklex.dcn.contracts"
        );

        GenerateContractCode.ContractToJava(
                new File("src/main/resources/contracts/ERC20.sol"),
                new File("src/main/generated"),
                "io.merklex.dcn.contracts"
        );

        GenerateContractCode.CompileContract(
                new File("src/main/resources/contracts/DCN.sol"),
                new File("contracts-compiled/DCN")
        );
    }
}
