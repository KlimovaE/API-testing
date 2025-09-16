package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
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

public class DepositAccount {
    String adminToken;
    String user1Token;
    String user2Token;
    //Генерация уникальных userName для каждого теста
    String user1Username = "A_" + System.currentTimeMillis(); // "kate001_123456"
    String user2Username = "B_" + System.currentTimeMillis(); // "kate002_123456"
    static int firstAccountUser1;

    public int createAccount() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user1Token)
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
        firstAccountUser1 = createAccount();
    }

    public static Stream<Arguments> transactionDataForPositiveCaseJson() {
        return Stream.of(
                Arguments.of(0, 100, 100),
                Arguments.of(100, 200, 300),
        );
    }

    public static Stream<Arguments> transactionDataForNegativeCase() {
        return Stream.of(
                //Пополнение на ноль
                Arguments.of(0, 0),
                Arguments.of(100, 0),
                //Пополнение на отрицательную сумму
                Arguments.of(0, -100),
        );
    }

    public static Stream<Arguments> notExistOrSomebodyAccount() {
        int randomIdAccount = new Random().nextInt(10000, 1000000);
        return Stream.of(
        );
    }

    @ParameterizedTest
    @DisplayName("Успешное пополнение счета первый и последующие разы")
    @MethodSource("transactionDataForPositiveCaseJson")
    public void userCanDepositAccountTest(double initialBalance, double depositAmount, double expectedBalance) {
        // 1. Устанавливаем начальный баланс (если нужно)
        if (initialBalance != 0) {
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", user1Token)
                    .body(Map.of("id", firstAccountUser1, "balance", initialBalance))
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
        }

        Float actualBalance = given()
                .contentType(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(Map.of("id", firstAccountUser1, "balance", depositAmount)) // ← "amount"!
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("balance");

        // 3. Проверяем итоговый баланс
        assertEquals((float) expectedBalance, actualBalance, 0.01f);
    }

    @ParameterizedTest
    @MethodSource("transactionDataForNegativeCase")
    public void userCannotDepositAccountTest(double initialBalance, double depositAmount) {
        // 1. Устанавливаем начальный баланс (если нужно)
        if (initialBalance != 0) {
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", user1Token)
                    .body(Map.of("id", firstAccountUser1, "balance", initialBalance))
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
        }

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(Map.of("id", firstAccountUser1, "balance", depositAmount)) // ← "amount"!
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("notExistOrSomebodyAccount")
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(String.format("""
        {
          "id": %d,
          "balance": 100.0
        }
        """, accountId))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

}