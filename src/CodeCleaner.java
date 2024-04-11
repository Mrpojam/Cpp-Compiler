public class CodeCleaner {
    public static String cleanCode(String code) {

        StringBuilder result = new StringBuilder();
        boolean insideComment = false;

        for (int i = 0; i < code.length(); i++) {
            if (!insideComment && i + 1 < code.length() && code.charAt(i) == '/' && code.charAt(i + 1) == '/') {
                // Skip characters until end of line
                while (i < code.length() && code.charAt(i) != '\n') {
                    i++;
                }
            } else if (!insideComment && i + 1 < code.length() && code.charAt(i) == '/' && code.charAt(i + 1) == '*') {
                insideComment = true;
                i++;
            } else if (insideComment && i + 1 < code.length() && code.charAt(i) == '*' && code.charAt(i + 1) == '/') {
                insideComment = false;
                i++;
            } else if (!insideComment) {
                result.append(code.charAt(i));
            }
        }

        return result.toString();

    }
}