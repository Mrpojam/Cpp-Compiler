import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CodeReader {
    private static String HLL;

    public static String read(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file = new File(filePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HLL = stringBuilder.toString();
        return HLL;
    }

    public static void main() {
        CodeReader codeReader = new CodeReader();
        String filePath = "Codes/main.cpp"; // Specify the path to your C++ file
        String code = codeReader.read(filePath);
        System.out.println("Contents of the file:");
        System.out.println(code);
    }
}