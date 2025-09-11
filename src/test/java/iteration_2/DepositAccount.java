package iteration_2;

import io.restassured.http.ContentType;
import models.CreateUserRequest;
import models.LoginUserRequest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminLoginUserRequest;
import requests.CreateUser;
import spec.RequestSpecs;
import spec.ResponseSpecs;

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
    String user1Password = "Kate012!";
    String user2Password = "Kate013!";
    String userRole = "USER";
    static int firstAccountUser1;
    static int secondAccountUser1;
    static Random random = new Random();

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
    @BeforeEach
    public void setupTestData() {
        //Создание первого пользователя
        CreateUserRequest createUser1Request = CreateUserRequest.builder()
                .username(user1Username)
                .password(user1Password)
                .role(userRole)
                .build();
        new CreateUser(RequestSpecs.userAuthSpec(user1Token), ResponseSpecs.entityWasCreated())
                .post(createUser1Request);
        //Получение токена для пользователя1
        LoginUserRequest loginUser1 = LoginUserRequest.builder()
                .username(user1Username)
                .password(user1Password)
                .build();

        user1Token = new AdminLoginUserRequest(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnsOK())
                .post(loginUser1)
                .extract()
                .header("Authorization");

        //Создание счета первого пользователя
        firstAccountUser1 = createAccount();
    }

    public static Stream<Arguments> transactionDataForPositiveCaseJson() {

        return Stream.of(
                Arguments.of(0, 100, 100),
                Arguments.of(100, 200, 300),
                Arguments.of(300, 0.01, 300.01)
        );
    }

    public static Stream<Arguments> transactionDataForNegativeCase() {
        return Stream.of(
                //Пополнение на ноль
                Arguments.of(0, 0),
                Arguments.of(100, 0),
                //Пополнение на отрицательную сумму
                Arguments.of(0, -100),
                Arguments.of(200, -100)
        );
    }

    public static Stream<Arguments> notExistOrSomebodyAccount() {
        int randomIdAccount = new Random().nextInt(10000, 1000000);

        return Stream.of(
                Arguments.of(randomIdAccount)  // ← передаем только ID
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

        // 2. Делаем депозит
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
    @DisplayName("Пользователь не может пополнить счет на 0 и отрицательную сумму")
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

        // 2. Делаем депозит
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
    @DisplayName("Пользователь не может пополнить чужой или несуществующий счет")
    @MethodSource("notExistOrSomebodyAccount")
    public void userCannotDepositNotExistAccountTest(int accountId) {
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