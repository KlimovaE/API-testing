package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferToAccount {
    String adminToken;
    String user1Token;
    String user2Token;
    //Генерация уникальных userName для каждого теста
    String user1Username = "A_" + System.currentTimeMillis(); // "kate001_123456"
    String user2Username = "B_" + System.currentTimeMillis(); // "kate002_123456"
    int firstAccountUser1;
    int secondAccountUser1;
    int firstAccountUser2;

    //Метод по созданию счетов
    public int createAccount(String userToken) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");
    }

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    @BeforeEach
    public void setupTestData() {
        //Получение токена для админа
        adminToken = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "admin",
                          "password": "admin"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        //Создание первого пользователя
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", adminToken)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate012!",
                          "role": "USER"
                        }
                        """, user1Username))
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        //Создание второго пользователя
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", adminToken)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate013!",
                          "role": "USER"
                        }
                        """, user2Username))
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        //Получение токена для пользователя1
        user1Token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate012!"
                        }
                        """, user1Username))
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        //Получение токена для пользователя2
        user2Token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate013!"
                        }
                        """, user2Username))
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        //Создание 2 счета для пользователя1 и 1 счет для пользователя2
        firstAccountUser1 = createAccount(user1Token);
        secondAccountUser1 = createAccount(user1Token);
        firstAccountUser2 = createAccount(user2Token);
    }

    //Метод по предварительному пополнению баланса
    private void depositUserAccount(String userToken, int userAccount, double balance) {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .body(Map.of("id", userAccount, "balance", balance))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> transactionDataForPositiveCase() {
        return Stream.of(
                Arguments.of(300, 0, 100, 200, 100),
                Arguments.of(500, 100, 150, 350, 250),
                Arguments.of(300, 0, 0.01, 299.99, 0.01),
                Arguments.of(300, 0, 300, 0, 300),
                //Гарантированная максимальная сумма перевода
                Arguments.of(11000, 0, 9999.99, 1000.01, 9999.99),
                //Проверка граничного значения(10.000 включительно?)
                Arguments.of(11000, 0, 10000.00, 1000, 10000.00)
        );
    }

    public static Stream<Arguments> transactionDataAmountForNegativeCase() {
        return Stream.of(
                Arguments.of(300, 300.01),
                Arguments.of(500, -100),
                Arguments.of(300, 0),
                //Превышение максимальной суммы перевода
                Arguments.of(11000, 10000.01),
                Arguments.of(11000, 10001)
        );
    }

    public static Stream<Arguments> transactionDataAccountForNegativeCase() {
        return Stream.of(
                // testCase, transferAmount, expectedStatusCode
                Arguments.of("С несуществующего счета", 300.0, HttpStatus.SC_FORBIDDEN),
                Arguments.of("На несуществующий счет", 300.0, HttpStatus.SC_BAD_REQUEST),
                Arguments.of("Со счета другого пользователя", 300.0, HttpStatus.SC_FORBIDDEN),
                Arguments.of("На тот же счет", 300.0, HttpStatus.SC_BAD_REQUEST)
        );
    }

    private String getUserAmountsInfo(String userToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .extract()
                .asString();  // ← получаем как строку
    }

    @ParameterizedTest
    @DisplayName("Успешное пополнение счета первый и последующие разы")
    @MethodSource("transactionDataForPositiveCase")
    public void userCanTransitTest(double initialFirstAccountUser1Balance, double initialSecondAccountUser1Balance,
                                   double transferAmount, double expectedFirstAccountUser1Balance, double expectedSecondAccountUser1Balance) {

        // 1. Устанавливаем начальный баланс (если нужно)
        depositUserAccount(user1Token, firstAccountUser1, initialFirstAccountUser1Balance);
        if (initialSecondAccountUser1Balance != 0) {
            depositUserAccount(user1Token, secondAccountUser1, initialSecondAccountUser1Balance);
        }

        // 2. Делаем перевод
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(Map.of("senderAccountId", firstAccountUser1, "receiverAccountId", secondAccountUser1, "amount", transferAmount))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        //Получаем баланс отправителя и получатели и проверяем, что после перевода изменился
        double actualBalanceFirstAccountUser1 = JsonPath.from(getUserAmountsInfo(user1Token))
                .getDouble("find { it.id == " + firstAccountUser1 + " }.balance");
        double actualBalanceSecondAccountUser1 = JsonPath.from(getUserAmountsInfo(user1Token))
                .getDouble("find { it.id == " + secondAccountUser1 + " }.balance");

        assertEquals(actualBalanceFirstAccountUser1, expectedFirstAccountUser1Balance, 0.01);
        assertEquals(actualBalanceSecondAccountUser1, expectedSecondAccountUser1Balance, 0.01);
    }

    @ParameterizedTest
    @DisplayName("Неуспешный перевод с невалидной суммой")
    @MethodSource("transactionDataAmountForNegativeCase")
    public void userCannotTransitInvalidAmountTest(double initialFirstAccountUser1Balance, double transferAmount) {
        // 1. Устанавливаем начальный баланс (если нужно)
        depositUserAccount(user1Token, firstAccountUser1, initialFirstAccountUser1Balance);

        // 2. Делаем перевод
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(Map.of("senderAccountId", firstAccountUser1, "receiverAccountId", secondAccountUser1, "amount", transferAmount))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @DisplayName("Неуспешный перевод с невалидными счетами")
    @MethodSource("transactionDataAccountForNegativeCase")
    public void userCannotTransferTest(String testCase, double transferAmount, int expectedStatusCode) {
        int senderAccountId;
        int receiverAccountId;
        int randomNonExistentId = new Random().nextInt(100000, 1000000);

        switch (testCase) {
            case "На несуществующий счет":
                // счет1 -> несуществующий счет
                senderAccountId = firstAccountUser1;
                receiverAccountId = randomNonExistentId;
                break;
            case "С несуществующего счета":
                // несуществующий счет -> счет1
                senderAccountId = randomNonExistentId;
                receiverAccountId = firstAccountUser1;
                break;
            case "Со счета другого пользователя":
                // счет другого пользователя -> счет1
                senderAccountId = firstAccountUser2; // счет user2
                receiverAccountId = firstAccountUser1; // счет user1
                break;
            case "На тот же счет":
                // счет1 -> счет1 (самому себе)
                senderAccountId = firstAccountUser1;
                receiverAccountId = firstAccountUser1;
                break;
            default:
                throw new IllegalArgumentException("Unknown test case: " + testCase);
        }

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(Map.of(
                        "senderAccountId", senderAccountId,
                        "receiverAccountId", receiverAccountId,
                        "amount", transferAmount
                ))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(expectedStatusCode);
    }

}