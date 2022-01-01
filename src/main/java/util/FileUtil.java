package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

    public static String readLine(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readAllLines(String path) {
        Path filePath = Paths.get(path);
        Charset charset = StandardCharsets.UTF_8;
        List<String> lines = null;
        try {
            lines = Files.readAllLines(filePath, charset);
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
        return lines;
    }
}
