package generators;

import java.util.Random;

public class UsernameGenerator {
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-";
    private static final Random random = new Random();

    public static String generateValidUsername() {
        int length = random.nextInt(13) + 3; // от 3 до 15 символов

        StringBuilder username = new StringBuilder();
        for (int i = 0; i < length; i++) {
            username.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }

        if (Character.isDigit(username.charAt(0))) {
            username.setCharAt(0, (char) ('a' + random.nextInt(26)));
        }

        return username.toString();
    }

    public static String generateValidPassword() {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        StringBuilder password = new StringBuilder();
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        int totalLength = random.nextInt(5) + 8; // 8-12 символов
        String allChars = lowercase + uppercase + digits + special;
        while (password.length() < totalLength) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}