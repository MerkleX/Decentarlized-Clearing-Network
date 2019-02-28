package io.merklex.web3.gen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * @author plorio
 */
public class Utils {
    public static String ReadFully(File stream) throws IOException {
        return ReadFully(new FileInputStream(stream));
    }

    public static String ReadFully(InputStream stream) throws IOException {
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

    public static void Write(String content, File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes());
        }
    }

    public static String Offset(ArrayList<String> offset) {
        if (offset.size() == 0) {
            return "0";
        }
        ArrayList<String> groupped = new ArrayList<>();

        int total = 0;

        for (String s : offset) {
            try {
                total += Integer.parseInt(s);
            } catch (Exception ignore) {
                groupped.add(s);
            }
        }

        if (total != 0) {
            groupped.add(String.valueOf(total));
        }

        return String.join(" + ", groupped);
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
