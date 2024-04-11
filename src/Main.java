import java.util.List;

public class Main {
    public static void main(String[] args) {
//        Lexical.RunTokenTypeTest();
//        Lexical.RunTokenizerTest();
//        Lexical.RunAnalyzerTest();

        String PlainCode = CodeReader.read("Codes/main.cpp");
        String CleanedCode = CodeCleaner.cleanCode((PlainCode));

        System.out.println("####Cleaned Code####");
        System.out.println(CleanedCode);
        List<String> tokens = Lexical.tokenizeCode(CleanedCode);
        List<String> analyzedTokens = Lexical.analyzeTokens(tokens);
        for (String token : analyzedTokens) {
            System.out.println(token);
        }
    }
}
