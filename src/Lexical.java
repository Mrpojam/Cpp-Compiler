import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;

public class Lexical {
    // Function to check if a character is a letter
    public static boolean isLetter(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    // Function to check if a character is a digit
    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    // Function to check if a string is an identifier
    public static boolean isIdentifier(String str) {
        return str.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    // Function to check if a string is a number
    public static boolean isNumber(String str) {
        return str.matches("[0-9]+");
    }

    // Function to check if a string is a reserved word
    public static boolean isReservedWord(String str) {
        String[] reservedWords = {"int", "float", "return", "if", "while", "cin", "cout",
                "continue", "break", "#include", "using", "namespace",
                "std", "main", "=", "+", "&&", "||", "<<", ">>"};
        for (String word : reservedWords) {
            if (str.equals(word)) {
                return true;
            }
        }
        return false;
    }

    // Function to check if a string is a valid string (enclosed in quotation marks)
    public static boolean isString(String str) {
        return str.startsWith("\"") && str.endsWith("\"");
    }

    public static List<String> tokenizeCode(String code) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (Character.isWhitespace(ch) || isSpecialCharacter(ch)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                if (!Character.isWhitespace(ch)) {
                    if (isPartOfMultiCharOperator(ch) && i < code.length() - 1 && isPartOfMultiCharOperator(code.charAt(i + 1))) {
                        currentToken.append(ch);
                        currentToken.append(code.charAt(i + 1));
                        i++; // Skip the next character
                    } else {
                        tokens.add(String.valueOf(ch));
                    }
                }
            } else {
                currentToken.append(ch);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    // Function to check if a character is a special character
    private static boolean isSpecialCharacter(char ch) {
        char[] specialCharacters = {'=', '+', '&', '|', '<', '>', '(', ')', '{', '}', '[', ']', ';', ',', '.'};
        for (char c : specialCharacters) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    // Function to check if a character is part of a multi-character operator
    private static boolean isPartOfMultiCharOperator(char ch) {
        char[] multiCharOperators = {'=', '+', '&', '|', '<', '>'};
        for (char c : multiCharOperators) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }
    public static List<String> analyzeTokens(List<String> tokens) {
        List<String> analyzedTokens = new ArrayList<>();
        for (String token : tokens) {
            if (isIdentifier(token)) {
                analyzedTokens.add("<Identifier, " + token + ">");
            } else if (isReservedWord(token)) {
                analyzedTokens.add("<ReservedWord, " + token + ">");
            } else if (isNumber(token)) {
                analyzedTokens.add("<Number, " + token + ">");
            } else if (isString(token)) {
                analyzedTokens.add("<String, " + token + ">");
            } else {
                analyzedTokens.add("<Operator, " + token + ">");
            }
        }
        return analyzedTokens;
    }

    public static void RunAnalyzerTest() {
        // Test the tokenizeCode method
//        String code = "int main() {\n    return 0;\n}";
        String code = "#include<iostream>\n using namespace std;\n int main() {\nint x = 0;\n x++;\n if(x == 1)\n\t cout << \"yes\";   return 0;\n}";

        List<String> tokens = tokenizeCode(code);
        System.out.println("Tokenized code:");
        for (String token : tokens) {
            System.out.println(token);
        }

        // Test the analyzeTokens method
        System.out.println("\nAnalyzed tokens:");
        List<String> analyzedTokens = analyzeTokens(tokens);
        for (String token : analyzedTokens) {
            System.out.println(token);
        }
    }
    public static void RunTokenizerTest() {
        String code = "#include<iostream>\n using namespace std;\n int main() {\nint x = 0;\n x++;\n if(x == 1)\n\t cout << \"yes\";   return 0;\n}";
        List<String> tokens = tokenizeCode(code);
        String[] expectedTokens = {"#include", "<", "iostream", ">", "using", "namespace", "std", ";", "int", "main", "(", ")", "{","int", "x", "=", "0", ";", "x", "++", ";", "if", "(", "x", "==", "1", ")", "cout", "<<", "\"yes\"", ";", "return", "0", ";", "}"};

        System.out.println("\u001B[33m ###Testing the Tokenizer###");
        System.out.println("Plain code:\n " + code);
        for (int i = 0; i < expectedTokens.length; i++) {
            String expectedToken = expectedTokens[i];
            if (i < tokens.size()) {
                String token = tokens.get(i);
                if (expectedToken.equals(token)) {
                    System.out.println("\u001B[32m+ " + token + "/" + expectedToken); // Print green '+'
                } else {
                    System.out.println("\u001B[31m- " + token + "/" + expectedToken); // Print red '-'
                }
            } else {
                System.out.println("\u001B[31m- Missing token: " + expectedToken); // Print missing token in red '-'
            }
        }
//        System.out.println("Tokenized code:");
//        for (String token : tokens) {
//            System.out.println(token);
//        }
    }

    public static void RunTokenTypeTest() {
        // Test the functions
        // Test the Lexical class functions
        System.out.println("Testing Lexical class functions:");
        System.out.println("----------------------------------");

        // Test isLetter
        System.out.println("isLetter('a'): " + Lexical.isLetter('a')); // true
        System.out.println("isLetter('3'): " + Lexical.isLetter('3')); // false

        // Test isDigit
        System.out.println("isDigit('5'): " + Lexical.isDigit('5')); // true
        System.out.println("isDigit('x'): " + Lexical.isDigit('x')); // false

        // Test isIdentifier
        System.out.println("isIdentifier(\"var_123\"): " + Lexical.isIdentifier("var_123")); // true
        System.out.println("isIdentifier(\"123var\"): " + Lexical.isIdentifier("123var")); // false

        // Test isNumber
        System.out.println("isNumber(\"12345\"): " + Lexical.isNumber("12345")); // true
        System.out.println("isNumber(\"abc123\"): " + Lexical.isNumber("abc123")); // false

        // Test isReservedWord
        System.out.println("isReservedWord(\"int\"): " + Lexical.isReservedWord("int")); // true
        System.out.println("isReservedWord(\"while\"): " + Lexical.isReservedWord("while")); // true
        System.out.println("isReservedWord(\"foo\"): " + Lexical.isReservedWord("foo")); // false

        // Test isString
        System.out.println("isString(\"\\\"Hello\\\"\"): " + Lexical.isString("\"Hello\"")); // true
        System.out.println("isString(\"Hello\\\"\"): " + Lexical.isString("Hello\"")); // false
    }
}

