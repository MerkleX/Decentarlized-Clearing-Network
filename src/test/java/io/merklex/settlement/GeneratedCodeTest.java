package io.merklex.settlement;

import io.merklex.settlement.utils.GenerateContractCode;
import io.merklex.settlement.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GeneratedCodeTest {
    @Test
    public void generatedCodeShouldBeLatest() throws IOException {
        File compareOut = Utils.TempDir();

        try {
            GenerateContractCode.ContractToJava(
                    new File("src/main/resources/contract/MerkleX2.sol"),
                    compareOut,
                    "out"
            );

            File[] files = new File(compareOut, "out").listFiles();
            assert files != null;
            File contractPath = files[0];

            String expected = Utils.ReadAll(new FileInputStream(contractPath));
            expected = expected.replaceFirst("package [\\w_.]+;", "");
            expected = expected.replaceFirst("(private static final String BINARY = \"[0-9abcdef]+)[0-9abcdef]{68}(\";)", "$1$2");

            String actual = Utils.ReadAll(new FileInputStream("src/main/generated/io/merklex/settlement/contracts/MerkleX.java"));
            actual = actual.replaceFirst("package [\\w_.]+;", "");
            actual = actual.replaceFirst("(private static final String BINARY = \"[0-9abcdef]+)[0-9abcdef]{68}(\";)", "$1$2");

            System.out.println(actual);

            Assert.assertEquals(expected, actual);
        } finally {
            Utils.DeleteDir(compareOut);
        }
    }

    public static void main(String[] args) {
        GenerateContractCode.ContractToJava(
                new File("src/main/resources/contract/MerkleX2.sol"),
                new File("src/main/generated"),
                "io.merklex.settlement.contracts"
        );
    }
}
