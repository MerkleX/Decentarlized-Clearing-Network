package io.merklex.web3.gen;

/**
 * @author plorio
 */
public class NameConverter {
    public static String CamelToSnake(String input) {
        StringBuilder snake = new StringBuilder();
        for (int i = 0; i <= input.length() - 1; i++) {
            char curr = input.charAt(i);
            if (Character.isUpperCase(curr) && i != 0) {
                snake.append("_").append(curr);
            }
            else {
                snake.append(Character.toLowerCase(curr));
            }
        }
        return snake.toString().toLowerCase();
    }

    public static String SnakeToCamel(String input) {
        StringBuilder camel = new StringBuilder();

        boolean nextCap = false;
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            if (curr == '_') {
                nextCap = true;
                continue;
            }

            if (nextCap) {
                camel.append(Character.toUpperCase(curr));
                nextCap = false;
            }
            else {
                camel.append(Character.toLowerCase(curr));
            }
        }

        return camel.toString();
    }

    public static String Capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
