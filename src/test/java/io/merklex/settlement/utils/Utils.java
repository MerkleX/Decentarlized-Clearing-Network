package io.merklex.settlement.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Utils {
    public static String ToLargeHex(String hex) {
        if (!hex.startsWith("0x")) {
            throw new IllegalArgumentException();
        }
        return "0x" + "0000000000000000000000000000000000000000000000000000000000000000".substring(hex.length() - 2) + hex.substring(2);
    }

    public static String ReadAll(InputStream stream) throws IOException {
        StringBuilder stringBuffer = new StringBuilder();
        byte[] buffer = new byte[1024];

        while (true) {
            int read = stream.read(buffer);
            if (read < 0) {
                break;
            }
            stringBuffer.append(new String(buffer, 0, read));
        }
        return stringBuffer.toString();
    }

    public static File TempDir() throws IOException {
        File dir = File.createTempFile("out", "out");
        boolean success = dir.delete();
        assert success;

        return dir;
    }

    public static void DeleteDir(File file) {
        if (!file.exists()) {
            return;
        }
        try {
            Files.walk(file.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
