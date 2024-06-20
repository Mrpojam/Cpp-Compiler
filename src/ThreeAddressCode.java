import java.util.ArrayList;
import java.util.List;

class ThreeAddressCodeGenerator {
    private List<String> instructions;
    private int tempVarCounter;

    public ThreeAddressCodeGenerator() {
        instructions = new ArrayList<>();
        tempVarCounter = 0;
    }

    public List<String> generateTAC(ParseTreeNode root) {
        traverse(root);
        return instructions;
    }

    private String traverse(ParseTreeNode node) {
        if (node == null) {
            return "";
        }

        if (node.children.isEmpty()) {
            return node.value;
        }

        // Handle assignment
        if (node.value.equals("=")) {
            String left = traverse(node.children.get(0));
            String right = traverse(node.children.get(1));
            instructions.add(left + " = " + right);
            return left;
        }

        // Handle binary operations
        if (node.value.equals("+") || node.value.equals("-") || node.value.equals("*") || node.value.equals("/")) {
            String left = traverse(node.children.get(0));
            String right = traverse(node.children.get(1));
            String tempVar = getNextTempVar();
            instructions.add(tempVar + " = " + left + " " + node.value + " " + right);
            return tempVar;
        }

        // Default case for handling single child nodes like factors
        return traverse(node.children.get(0));
    }

    private String getNextTempVar() {
        return "t" + (tempVarCounter++);
    }

    public static void testTAC() {
        // Construct the parse tree for the expression: x = a + b * c;
        ParseTreeNode root = new ParseTreeNode("=");
        ParseTreeNode x = new ParseTreeNode("x");
        ParseTreeNode plus = new ParseTreeNode("+");
        ParseTreeNode a = new ParseTreeNode("a");
        ParseTreeNode multiply = new ParseTreeNode("*");
        ParseTreeNode b = new ParseTreeNode("b");
        ParseTreeNode c = new ParseTreeNode("c");

        root.addChild(x);
        root.addChild(plus);
        plus.addChild(a);
        plus.addChild(multiply);
        multiply.addChild(b);
        multiply.addChild(c);

        System.out.println(root);
        // Generate TAC
        ThreeAddressCodeGenerator tacGenerator = new ThreeAddressCodeGenerator();
        List<String> code = tacGenerator.generateTAC(root);

        // Print the generated TAC
        for (String instr : code) {
            System.out.println(instr);
        }
    }
}
