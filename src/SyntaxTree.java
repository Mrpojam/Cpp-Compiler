import java.util.*;

class SyntaxTreeeNode {
    String value;
    List<SyntaxTreeeNode> children;

    SyntaxTreeeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    void addChild(SyntaxTreeeNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        SyntaxTreeeNode root = this;

        Queue<SyntaxTreeeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            SyntaxTreeeNode node = queue.poll();
            for (SyntaxTreeeNode u : node.children) {
                System.out.println(node.value + " -> " + u.value);
                queue.add(u);
            }
        }

        return " ";
    }
}

class SyntaxTree {
    SyntaxTreeeNode root;

    SyntaxTree(String equation) {
        this.root = parseEquation(equation);
    }

    private SyntaxTreeeNode parseEquation(String equation) {
        List<String> tokens = tokenize(equation);
        return parseTokens(tokens);
    }

    private List<String> tokenize(String equation) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        for (char c : equation.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '.') {
                token.append(c);
            } else {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=') {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    private SyntaxTreeeNode parseTokens(List<String> tokens) {
        Stack<SyntaxTreeeNode> nodes = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    nodes.push(buildSubTree(operators.pop(), nodes.pop(), nodes.pop()));
                }
                operators.push(token);
            } else if (token.equals("=")) {
                operators.push(token);
            } else {
                nodes.push(new SyntaxTreeeNode(token));
            }
        }

        while (!operators.isEmpty()) {
            nodes.push(buildSubTree(operators.pop(), nodes.pop(), nodes.pop()));
        }

        return nodes.pop();
    }

    private SyntaxTreeeNode buildSubTree(String operator, SyntaxTreeeNode right, SyntaxTreeeNode left) {
        SyntaxTreeeNode node = new SyntaxTreeeNode(operator);
        node.addChild(left);
        node.addChild(right);
        return node;
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private int precedence(String operator) {
        switch (operator) {
            case "=":
                return 0;
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
        }
        return -1;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static void testAST() {
        SyntaxTree syntaxTree = new SyntaxTree("x=a*b+3");
        System.out.println(syntaxTree);
    }
}
