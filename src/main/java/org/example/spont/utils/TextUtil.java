package org.example.spont.utils;

public class TextUtil {

    public static String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(capitalizeComplexWord(word)).append(" ");
        }

        return result.toString().trim();
    }

    private static String capitalizeComplexWord(String word) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : word.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }

            // After special characters, capitalize next letter
            if (c == '\'' || c == '-') {
                capitalizeNext = true;
            }
        }

        return result.toString();
    }
}
