import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

enum TokenType {
    IDENTIFIER, NUMBER, RESERVEDWORD, SYMBOL, STRING, ERROR
}

class Token {
    TokenType type;
    String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + " (" + value + ")";
    }
}

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    private Token getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null;
    }

    private void advanceToken() {
        currentTokenIndex++;
    }

    private void retrieveToken() {
        currentTokenIndex--;
    }

    private void error(String message) throws Exception {
        throw new Exception("Parse error: " + message);
    }

    public void parse() throws Exception {
        program();
        if (currentTokenIndex < tokens.size()) {
            error("Unexpected tokens at the end");
        }
    }

    private void program() throws Exception {
        while (currentTokenIndex < tokens.size()) {
            statement();
        }
    }

    private void statement() throws Exception {
        Token token = getCurrentToken();
        if (token == null) {
            return;
        }

        switch (token.type) {
            case IDENTIFIER:
                if (token.value.equals("return")) {
                    returnStatement();
                } else {
                    assignmentOrFunctionCall();
                }
                break;
            case RESERVEDWORD:
                reservedWordStatement();
                break;
            case SYMBOL:
                if (token.value.equals("{")) {
                    block();
                } else {
                    error("Unexpected symbol: " + token);
                }
                break;
            default:
                error("Unexpected token: " + token);
        }
    }

    private void assignmentOrFunctionCall() throws Exception {
        Token token = getCurrentToken();
        if (token.type != TokenType.IDENTIFIER) {
            error("Expected identifier");
        }
        advanceToken();
        token = getCurrentToken();
        System.out.println(token);
        if (token != null && token.type == TokenType.SYMBOL && token.value.equals("=")) {
            advanceToken();
            expression();

            if (!match(TokenType.SYMBOL, ";")) {
                error("Expected ';' after assignment");
            }
            advanceToken();
        } else if (token != null && token.type == TokenType.SYMBOL && token.value.equals("(")) {
            functionCall();
            if (!match(TokenType.SYMBOL, ";")) {
                error("Expected ';' after function call");
            }
            advanceToken();
        } else {
            error("Expected assignment or function call");
        }
    }

    private void functionCall() throws Exception {
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' in function call");
        }
        advanceToken();
        while (!match(TokenType.SYMBOL, ")")) {
            expression();
            if (!match(TokenType.SYMBOL, ")")) {
                if (!match(TokenType.SYMBOL, ",")) {
                    error("Expected ',' in function call");
                }
                advanceToken();
            }
        }
        advanceToken();
    }

    private void reservedWordStatement() throws Exception {
        Token token = getCurrentToken();
        if (token == null || token.type != TokenType.RESERVEDWORD) {
            error("Expected reserved word");
        }

        switch (token.value) {
            case "int":
                advanceToken();
                if (match(TokenType.RESERVEDWORD, "main"))
                    mainStatement();
                else {
                    retrieveToken();
                    variableDeclaration();
                }
                break;
            case "float":
            case "void":
                variableDeclaration();
                break;
            case "return":
                returnStatement();
                break;
            case "if":
                ifStatement();
                break;
            case "while":
                whileStatement();
                break;
            case "for":
                forStatement();
                break;
            case "cin":
                inputStatement();
                break;
            case "cout":
                outputStatement();
                break;
            case "continue":
                continueStatement();
                break;
            case "break":
                breakStatement();
                break;
            case "#include":
                includeStatement();
                break;
            case "using":
                usingNamespaceStatement();
                break;
            case "namespace":
                namespaceStatement();
                break;
            case "std":
                stdStatement();
                break;
            case "main":
                mainStatement();
                break;
            default:
                error("Unexpected reserved word: " + token);
        }
    }

    private void variableDeclaration() throws Exception {
        advanceToken(); // consume int/float/void
        if (!match(TokenType.IDENTIFIER)) {
            Token token = getCurrentToken();
            System.out.println(token);
            error("Expected identifier after type");
        }
        advanceToken();
        while (match(TokenType.SYMBOL, ",")) {
            advanceToken();
            if (!match(TokenType.IDENTIFIER)) {
                error("Expected identifier after ','");
            }
            advanceToken();
        }
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after variable declaration");
        }
        advanceToken();
    }

    private void returnStatement() throws Exception {
        advanceToken(); // consume return
        expression();
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after return statement");
        }
        advanceToken();
    }

    private void ifStatement() throws Exception {
        advanceToken(); // consume if
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'if'");
        }
        advanceToken();
        expression();
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after condition in 'if' statement");
        }
        advanceToken();
        statement();
    }

    private void whileStatement() throws Exception {
        advanceToken(); // consume while
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'while'");
        }
        advanceToken();
        expression();
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after condition in 'while' statement");
        }
        advanceToken();
        statement();
    }

    private void forStatement() throws Exception {
        advanceToken(); // consume for
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'for'");
        }
        advanceToken();
        statement();
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' in 'for' statement");
        }
        advanceToken();
        expression();
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' in 'for' statement");
        }
        advanceToken();
        expression();
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after 'for' conditions");
        }
        advanceToken();
        statement();
    }

    private void inputStatement() throws Exception {
        advanceToken(); // consume cin
        while (match(TokenType.RESERVEDWORD, ">>")) {
            advanceToken();
            if (!match(TokenType.IDENTIFIER)) {
                error("Expected identifier after '>>' in 'cin' statement");
            }
            advanceToken();
        }
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'cin' statement");
        }
        advanceToken();
    }

    private void outputStatement() throws Exception {
        advanceToken(); // consume cout
        while (match(TokenType.RESERVEDWORD, "<<")) {
            advanceToken();
            expression();
        }
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'cout' statement");
        }
        advanceToken();
    }

    private void continueStatement() throws Exception {
        advanceToken(); // consume continue
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'continue' statement");
        }
        advanceToken();
    }

    private void breakStatement() throws Exception {
        advanceToken(); // consume break
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'break' statement");
        }
        advanceToken();
    }

    private void includeStatement() throws Exception {
        advanceToken(); // consume #include
        if (!match(TokenType.SYMBOL, "<")) {
            error("Expected '<' after '#include'");
        }
        advanceToken();
        if (!match(TokenType.IDENTIFIER)) {
            error("Expected header file after '<'");
        }
        advanceToken();
        if (!match(TokenType.SYMBOL, ">")) {
            error("Expected '>' after header file");
        }
        advanceToken();
    }

    private void usingNamespaceStatement() throws Exception {
        advanceToken(); // consume using
        if (!match(TokenType.RESERVEDWORD, "namespace")) {
            error("Expected 'namespace' after 'using'");
        }
        advanceToken();
        if (!match(TokenType.RESERVEDWORD, "std")) {
            error("Expected 'std' after 'namespace'");
        }
        advanceToken();
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'using namespace std'");
        }
        advanceToken();
    }

    private void namespaceStatement() throws Exception {
        // Implementation for namespace statement
        advanceToken();
    }

    private void stdStatement() throws Exception {
        // Likely part of 'using namespace std', hence no separate implementation needed
        advanceToken();
    }

    private void mainStatement() throws Exception {

        advanceToken(); // consume main
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'main'");
        }
        advanceToken();
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after 'main('");
        }
        advanceToken();
        if (!match(TokenType.SYMBOL, "{")) {
            error("Expected '{' after 'main()'");
        }
        advanceToken();
        while (!match(TokenType.SYMBOL, "}")) {
            statement();
        }
        advanceToken();
    }

    private void block() throws Exception {
        if (!match(TokenType.SYMBOL, "{")) {
            error("Expected '{'");
        }
        advanceToken();
        while (!match(TokenType.SYMBOL, "}")) {
            statement();
        }
        advanceToken();
    }

    // Helper methods to match tokens and expressions
    private boolean match(TokenType type, String value) {
        Token token = getCurrentToken();
        System.out.println(token);
        return token != null && token.type == type && token.value.equals(value);
    }

    private boolean match(TokenType type) {
        Token token = getCurrentToken();
        return token != null && token.type == type;
    }

    private void expression() throws Exception {
        term();
        while (match(TokenType.SYMBOL, "+") || match(TokenType.SYMBOL, "-")) {
            advanceToken();
            term();
        }
    }

    private void term() throws Exception {
        factor();
        while (match(TokenType.SYMBOL, "*") || match(TokenType.SYMBOL, "/")) {
            advanceToken();
            factor();
        }
    }

    private void factor() throws Exception {
        Token token = getCurrentToken();
        if (token == null) {
            error("Expected factor");
        }
        if (match(TokenType.NUMBER) || match(TokenType.IDENTIFIER) || match(TokenType.STRING)) {
            advanceToken();
        } else if (match(TokenType.SYMBOL, "(")) {
            advanceToken(); // consume '('
            expression();
            if (!match(TokenType.SYMBOL, ")")) {
                error("Expected ')' after expression");
            }
            advanceToken(); // consume ')'
        } else {
            error("Unexpected token in factor: " + token);
        }
    }

    public static void test() {
        try {
//            List<Token> tokens = Arrays.asList(
//                    new Token(TokenType.RESERVEDWORD, "int"),
//                    new Token(TokenType.IDENTIFIER, "x"),
//                    new Token(TokenType.SYMBOL, ";"),
//                    new Token(TokenType.IDENTIFIER, "x"),
//                    new Token(TokenType.SYMBOL, "="),
//                    new Token(TokenType.NUMBER, "3"),
//                    new Token(TokenType.SYMBOL, "+"),
//                    new Token(TokenType.NUMBER, "4"),
//                    new Token(TokenType.SYMBOL, ";")
//            );
            List<Token> tokens = new ArrayList<>(Arrays.asList(
                    new Token(TokenType.RESERVEDWORD, "#include"),
                    new Token(TokenType.SYMBOL, "<"),
                    new Token(TokenType.IDENTIFIER, "iostream"),
                    new Token(TokenType.SYMBOL, ">"),
                    new Token(TokenType.RESERVEDWORD, "using"),
                    new Token(TokenType.RESERVEDWORD, "namespace"),
                    new Token(TokenType.RESERVEDWORD, "std"),
                    new Token(TokenType.SYMBOL, ";"),
                    new Token(TokenType.RESERVEDWORD, "int"),
                    new Token(TokenType.RESERVEDWORD, "main"),
                    new Token(TokenType.SYMBOL, "("),
                    new Token(TokenType.SYMBOL, ")"),
                    new Token(TokenType.SYMBOL, "{"),
                    new Token(TokenType.SYMBOL, "}")
            ));


            Parser parser = new Parser(tokens);
            parser.parse();
            System.out.println("Parsing completed successfully.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
