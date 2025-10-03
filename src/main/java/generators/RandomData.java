package generators;


import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
    private RandomData(){};

    public static String getUserName() {
        return RandomStringUtils.randomAlphabetic(10);

    }

    public static String getPassword() {
        String upperCase = RandomStringUtils.randomAlphabetic(2).toUpperCase();      // 2 большие буквы
        String lowerCase = RandomStringUtils.randomAlphabetic(3).toLowerCase();      // 3 маленькие буквы
        String numbers = RandomStringUtils.randomNumeric(2);                         // 2 цифры
        String special = RandomStringUtils.random(2, "!@#$%^&");                    // 2 спецсимвола

        return upperCase + lowerCase + numbers + special;
    }

}