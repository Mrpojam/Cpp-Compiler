import java.util.List;


public class Main {
    public static void main(String[] args) {
            try {
                String PlainCode = CodeReader.read("Codes/main.cpp");
                String CleanedCode = CodeCleaner.cleanCode((PlainCode));

                System.out.println("####Cleaned Code####");
                System.out.println(CleanedCode);
                List<String> tokens = Lexical.tokenizeCode(CleanedCode);
                List<Token> analyzedTokens = Lexical.analyzeTokens(tokens);

                for (Token token : analyzedTokens) {
                    System.out.println(token.toString());
                }
                System.out.println("End of list");
                Parser parser = new Parser(analyzedTokens);
                ParseTreeNode Tree = parser.parse();

                System.out.println("Parsing completed successfully.");

                SyntaxTree syntaxTree = new SyntaxTree(CleanedCode);
                System.out.println(syntaxTree);

                ThreeAddressCodeGenerator tacGenerator = new ThreeAddressCodeGenerator();
                List<String> code = tacGenerator.generateTAC(syntaxTree.root);

                // Print the generated TAC
                for (String instr : code) {
                    System.out.println(instr);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

