package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
}
