import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


class ParseTreeNode {
    String value;
    List<ParseTreeNode> children;

    ParseTreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    void addChild(ParseTreeNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return value + " -> " + children;
    }
}


public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private ParseTreeNode root;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.root = new ParseTreeNode("root");
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

    public ParseTreeNode parse() throws Exception {
        program();
        if (currentTokenIndex < tokens.size()) {
            error("Unexpected tokens at the end");
        }

        return root;
    }

    private void program() throws Exception {
        while (currentTokenIndex < tokens.size()) {
            statement(root);
        }
    }

    private void statement(ParseTreeNode parent) throws Exception {
        Token token = getCurrentToken();
        if (token == null) {
            return;
        }
        ParseTreeNode node = new ParseTreeNode(token.value);
        parent.addChild(node);
        switch (token.type) {
            case IDENTIFIER:
                if (token.value.equals("return")) {
                    returnStatement(node);
                } else {
                    assignmentOrFunctionCall(node);
                }
                break;
            case RESERVEDWORD:
                reservedWordStatement(node);
                break;
            case SYMBOL:
                if (token.value.equals("{")) {
                    block(node);
                } else {
                    error("Unexpected symbol: " + token);
                }
                break;
            default:
                error("Unexpected token: " + token);
        }
    }

    private void assignmentOrFunctionCall(ParseTreeNode parent) throws Exception {
        Token token = getCurrentToken();
        if (token.type != TokenType.IDENTIFIER) {
            error("Expected identifier");
        }

        ParseTreeNode node = new ParseTreeNode(token.value);
        parent.addChild(node);

        advanceToken();
        token = getCurrentToken();

        if (token != null && token.type == TokenType.SYMBOL && token.value.equals("=")) {
            advanceToken();
            expression(node);

            if (!match(TokenType.SYMBOL, ";")) {
                error("Expected ';' after assignment");
            }
            node.addChild(new ParseTreeNode(("=")));
            advanceToken();
        } else if (token != null && token.type == TokenType.SYMBOL && token.value.equals("(")) {
            functionCall(node);
            if (!match(TokenType.SYMBOL, ";")) {
                error("Expected ';' after function call");
            }
            advanceToken();
        } else {
            System.out.println(token);
            error("Expected assignment or function call");
        }
    }

    private void functionCall(ParseTreeNode parent) throws Exception {
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' in function call");
        }

        ParseTreeNode node = new ParseTreeNode("function");
        parent.addChild(node);

        node.addChild(new ParseTreeNode("("));

        advanceToken();
        while (!match(TokenType.SYMBOL, ")")) {
            expression(node);
            if (!match(TokenType.SYMBOL, ")")) {
                if (!match(TokenType.SYMBOL, ",")) {
                    error("Expected ',' in function call");
                }
                advanceToken();
            }
        }
        node.addChild(new ParseTreeNode(")"));
        advanceToken();
    }

    private void reservedWordStatement(ParseTreeNode parent) throws Exception {
        Token token = getCurrentToken();
        if (token == null || token.type != TokenType.RESERVEDWORD) {
            System.out.println(token);
            error("Expected reserved word");
        }
        ParseTreeNode node = new ParseTreeNode(token.value);
        parent.addChild(node);
        switch (token.value) {
            case "int":
                advanceToken();
                if (match(TokenType.RESERVEDWORD, "main")) {
                    mainStatement(node);
                }
                else {
                    retrieveToken();
                    variableDeclaration(node);
                }
                break;
            case "float":
            case "void":
                variableDeclaration(node);
                break;
            case "return":
                returnStatement(node);
                break;
            case "if":
                ifStatement(node);
                break;
            case "while":
                whileStatement(node);
                break;
            case "for":
                forStatement(node);
                break;
            case "cin":
                inputStatement(node);
                break;
            case "cout":
                outputStatement(node);
                break;
            case "continue":
                continueStatement(node);
                break;
            case "break":
                breakStatement(node);
                break;
            case "#include":
                includeStatement(node);
                break;
            case "using":
                usingNamespaceStatement(node);
                break;
            case "namespace":
                namespaceStatement(node);
                break;
            case "std":
                stdStatement(node);
                break;
            case "main":
                mainStatement(node);
                break;
            default:
                error("Unexpected reserved word: " + token);
        }
    }
    private void variableDeclaration(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume int/float/void
        ParseTreeNode node = new ParseTreeNode("variableDeclaration");
        parent.addChild(node);
        Token token = getCurrentToken();
        if (!match(TokenType.IDENTIFIER)) {
            System.out.println(token);
            error("Expected identifier after type");
        }
        node.addChild(new ParseTreeNode(getCurrentToken().value));
        advanceToken();
        if (match(TokenType.SYMBOL, "=")) {
            node.addChild(new ParseTreeNode("="));
            advanceToken();
            expression(node); // Parsing the initialization expression
        }
        while (match(TokenType.SYMBOL, ",")) {
            node.addChild(new ParseTreeNode(","));
            advanceToken();
            if (!match(TokenType.IDENTIFIER)) {
                System.out.println(token);
                error("Expected identifier after ','");
            }
            node.addChild(new ParseTreeNode(getCurrentToken().value));
            advanceToken();
            if (match(TokenType.SYMBOL, "=")) {
                node.addChild(new ParseTreeNode("="));
                advanceToken();
                expression(node); // Parsing the initialization expression
            }
        }
        if (!match(TokenType.SYMBOL, ";")) {
            System.out.println(token);
            error("Expected ';' after variable declaration");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }

    private void returnStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume return
        ParseTreeNode node = new ParseTreeNode("returnStatement");
        expression(node);

        Token token = getCurrentToken();
        if (!match(TokenType.SYMBOL, ";")) {
            System.out.println(token);
            error("Expected ';' after return statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }

    private void ifStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume if
        ParseTreeNode node = new ParseTreeNode("ifStatement");
        Token token = getCurrentToken();
        if (!match(TokenType.SYMBOL, "(")) {
            System.out.println(token);
            error("Expected '(' after 'if'");
        }
        advanceToken();
        node.addChild(new ParseTreeNode("("));
        expression(node);
        if (!match(TokenType.SYMBOL, ")")) {
            System.out.println(token);
            error("Expected ')' after condition in 'if' statement");
        }
        node.addChild(new ParseTreeNode(")"));
        advanceToken();
        statement(node);
    }

    private void whileStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume while
        ParseTreeNode node = new ParseTreeNode("whileStatement");
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'while'");
        }
        node.addChild(new ParseTreeNode("("));
        advanceToken();
        expression(node);
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after condition in 'while' statement");
        }
        node.addChild(new ParseTreeNode(")"));
        advanceToken();
        statement(node);
    }

    private void forStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume for
        ParseTreeNode node = new ParseTreeNode("forStatement");
        parent.addChild(node);
        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'for'");
        }
        node.addChild(new ParseTreeNode("("));
        advanceToken();
        statement(node);
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' in 'for' statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
        expression(node);
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' in 'for' statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
        expression(node);
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after 'for' conditions");
        }
        node.addChild(new ParseTreeNode(")"));
        advanceToken();
        statement(node);
    }

    private void inputStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume cin
        ParseTreeNode node = new ParseTreeNode("inputStatement");
        parent.addChild(node);
        while (match(TokenType.RESERVEDWORD, ">>")) {
            node.addChild(new ParseTreeNode(">>"));
            advanceToken();
            if (!match(TokenType.IDENTIFIER)) {
                error("Expected identifier after '>>' in 'cin' statement");
            }
            node.addChild(new ParseTreeNode(getCurrentToken().value));
            advanceToken();
        }
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'cin' statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }

    private void outputStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume cout
        ParseTreeNode node = new ParseTreeNode("outputStatement");
        parent.addChild(node);
        while (match(TokenType.RESERVEDWORD, "<<")) {
            node.addChild(new ParseTreeNode("<<"));
            advanceToken();
            expression(node);
        }
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'cout' statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }

    private void continueStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume continue
        ParseTreeNode node = new ParseTreeNode("continueStatement");
        parent.addChild(node);
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'continue' statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }

    private void breakStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume break
        ParseTreeNode node = new ParseTreeNode("breakStatement");
        parent.addChild(node);
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'break' statement");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }
    private void includeStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume #include
        ParseTreeNode node = new ParseTreeNode("includeStatement");
        parent.addChild(node);
        if (!match(TokenType.SYMBOL, "<")) {
            error("Expected '<' after '#include'");
        }
        node.addChild(new ParseTreeNode("<"));
        advanceToken();
        if (!match(TokenType.IDENTIFIER)) {
            error("Expected header file after '<'");
        }
        node.addChild(new ParseTreeNode(getCurrentToken().value));
        advanceToken();
        if (!match(TokenType.SYMBOL, ">")) {
            error("Expected '>' after header file");
        }
        node.addChild(new ParseTreeNode(">"));
        advanceToken();
    }

    private void usingNamespaceStatement(ParseTreeNode parent) throws Exception {
        advanceToken(); // consume using
        ParseTreeNode node = new ParseTreeNode("usingNamespaceStatement");
        parent.addChild(node);
        if (!match(TokenType.RESERVEDWORD, "namespace")) {
            error("Expected 'namespace' after 'using'");
        }
        node.addChild(new ParseTreeNode("namespace"));
        advanceToken();
        if (!match(TokenType.RESERVEDWORD, "std")) {
            error("Expected 'std' after 'namespace'");
        }
        node.addChild(new ParseTreeNode("std"));
        advanceToken();
        if (!match(TokenType.SYMBOL, ";")) {
            error("Expected ';' after 'using namespace std'");
        }
        node.addChild(new ParseTreeNode(";"));
        advanceToken();
    }

    private void namespaceStatement(ParseTreeNode parent) throws Exception {
        // Implementation for namespace statement
        advanceToken();
    }

    private void stdStatement(ParseTreeNode parent) throws Exception {
        // Likely part of 'using namespace std', hence no separate implementation needed
        advanceToken();
    }

    private void mainStatement(ParseTreeNode parent) throws Exception {

        advanceToken(); // consume main
        ParseTreeNode node = new ParseTreeNode("usingNamespaceStatement");
        parent.addChild(node);

        if (!match(TokenType.SYMBOL, "(")) {
            error("Expected '(' after 'main'");
        }
        node.addChild(new ParseTreeNode("main"));
        advanceToken();
        if (!match(TokenType.SYMBOL, ")")) {
            error("Expected ')' after 'main('");
        }
        node.addChild(new ParseTreeNode("("));
        advanceToken();
        node.addChild(new ParseTreeNode(")"));
        if (!match(TokenType.SYMBOL, "{")) {
            error("Expected '{' after 'main()'");
        }
        node.addChild(new ParseTreeNode("{"));
        advanceToken();
        while (!match(TokenType.SYMBOL, "}")) {
            statement(node);
        }
        node.addChild(new ParseTreeNode("{"));
        advanceToken();
    }

    private void block(ParseTreeNode parent) throws Exception {
        ParseTreeNode node = new ParseTreeNode("usingNamespaceStatement");
        parent.addChild(node);

        if (!match(TokenType.SYMBOL, "{")) {
            error("Expected '{'");
        }
        node.addChild(new ParseTreeNode("{"));
        advanceToken();
        while (!match(TokenType.SYMBOL, "}")) {
            statement(node);
        }
        node.addChild(new ParseTreeNode("}"));
        advanceToken();
    }

    // Helper methods to match tokens and expressions
    private boolean match(TokenType type, String value) {
        Token token = getCurrentToken();
        return token != null && token.type == type && token.value.equals(value);
    }

    private boolean match(TokenType type) {
        Token token = getCurrentToken();
        return token != null && token.type == type;
    }

    private void expression(ParseTreeNode parent) throws Exception {
        ParseTreeNode exprNode = new ParseTreeNode("expression");
        parent.addChild(exprNode);

        term(exprNode);

        while (match(TokenType.SYMBOL, "+") || match(TokenType.SYMBOL, "-")) {
            Token operator = getCurrentToken();
            exprNode.addChild(new ParseTreeNode(operator.value)); // Add operator node
            advanceToken();
            term(exprNode);
        }
    }

    private void term(ParseTreeNode parent) throws Exception {
        ParseTreeNode termNode = new ParseTreeNode("term");
        parent.addChild(termNode);

        factor(termNode);

        while (match(TokenType.SYMBOL, "*") || match(TokenType.SYMBOL, "/")) {
            Token operator = getCurrentToken();
            termNode.addChild(new ParseTreeNode(operator.value)); // Add operator node
            advanceToken();
            factor(termNode);
        }
    }

    private void factor(ParseTreeNode parent) throws Exception {
        ParseTreeNode factorNode = new ParseTreeNode("factor");
        parent.addChild(factorNode);

        Token token = getCurrentToken();
        if (token == null) {
            error("Expected factor");
        }

        if (match(TokenType.NUMBER) || match(TokenType.IDENTIFIER) || match(TokenType.STRING)) {
            factorNode.addChild(new ParseTreeNode(token.value)); // Add operand node
            advanceToken();
        } else if (match(TokenType.SYMBOL, "(")) {
            factorNode.addChild(new ParseTreeNode("(")); // Add '(' node
            advanceToken(); // consume '('
            expression(factorNode);
            if (!match(TokenType.SYMBOL, ")")) {
                error("Expected ')' after expression");
            }
            factorNode.addChild(new ParseTreeNode(")")); // Add ')' node
            advanceToken(); // consume ')'
        } else {
            error("Unexpected token in factor: " + token);
        }
    }

    public static void test() {
        try {
            List<Token> tokens = Arrays.asList(
                    new Token(TokenType.RESERVEDWORD, "int"),
                    new Token(TokenType.IDENTIFIER, "x"),
                    new Token(TokenType.SYMBOL, ";"),
                    new Token(TokenType.IDENTIFIER, "x"),
                    new Token(TokenType.SYMBOL, "="),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.SYMBOL, "+"),
                    new Token(TokenType.NUMBER, "4"),
                    new Token(TokenType.SYMBOL, ";")
            );
//            List<Token> tokens = new ArrayList<>(Arrays.asList(
//                    new Token(TokenType.RESERVEDWORD, "#include"),
//                    new Token(TokenType.SYMBOL, "<"),
//                    new Token(TokenType.IDENTIFIER, "iostream"),
//                    new Token(TokenType.SYMBOL, ">"),
//                    new Token(TokenType.RESERVEDWORD, "using"),
//                    new Token(TokenType.RESERVEDWORD, "namespace"),
//                    new Token(TokenType.RESERVEDWORD, "std"),
//                    new Token(TokenType.SYMBOL, ";"),
//                    new Token(TokenType.RESERVEDWORD, "int"),
//                    new Token(TokenType.RESERVEDWORD, "main"),
//                    new Token(TokenType.SYMBOL, "("),
//                    new Token(TokenType.SYMBOL, ")"),
//                    new Token(TokenType.SYMBOL, "{"),
//                    new Token(TokenType.SYMBOL, "}")
//            ));


            Parser parser = new Parser(tokens);
            ParseTreeNode Tree = parser.parse();
            System.out.println("Parsing completed successfully.");
            System.out.println(Tree);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
