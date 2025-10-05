package generators;


import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
    private RandomData(){};

    public static String getUserName() {
        String firstWord = RandomStringUtils.randomAlphabetic(3, 8);  // первое слово 3-8 букв
        String secondWord = RandomStringUtils.randomAlphabetic(3, 8); // второе слово 3-8 букв

        return firstWord + " " + secondWord;
    }

    public static String getPassword() {
        String upperCase = RandomStringUtils.randomAlphabetic(2).toUpperCase();      // 2 большие буквы
        String lowerCase = RandomStringUtils.randomAlphabetic(3).toLowerCase();      // 3 маленькие буквы
        String numbers = RandomStringUtils.randomNumeric(2);                         // 2 цифры
        String special = RandomStringUtils.random(2, "!@#$%^&");                    // 2 спецсимвола

        return upperCase + lowerCase + numbers + special;
    }

    public static String getRandomValidName() {
        String firstWord = RandomStringUtils.randomAlphabetic(3, 8);  // первое слово 3-8 букв
        String secondWord = RandomStringUtils.randomAlphabetic(3, 8); // второе слово 3-8 букв

        return firstWord + " " + secondWord;
    }
}