package iteration_2;

import models.CreateUserRequest;
import models.DepositAccountRequest;
import models.LoginUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminLoginUserRequest;
import requests.CreateAccount;
import requests.CreateUser;
import requests.UserDepositAccount;
import spec.RequestSpecs;
import spec.ResponseSpecs;

import java.util.Random;
import java.util.stream.Stream;

public class DepositAccount{
    String adminToken;
    String user1Token;
    String user2Token;
    //Генерация уникальных userName для каждого теста
    String user1Username = "A_" + System.currentTimeMillis(); // "kate001_123456"
    String user2Username = "B_" + System.currentTimeMillis(); // "kate002_123456"
    String user1Password = "Kate012!";
    String user2Password = "Kate013!";
    String userRole = "USER";
    long firstAccountUser1;
    long firstAccountUser2;
    long randomIdAccount = new Random().nextInt(10000, 1000000);

    //Метод по созданию счета
    public long createAccount(String userToken) {
        return new CreateAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.entityWasCreated())
                .post()
                .extract()
                .jsonPath()
                .getLong("id");
    }

    //Метод по созданию пользователя
    private void createUser(String userName, String userPassword, String role) {
        CreateUserRequest createUser = CreateUserRequest.builder()
                .username(userName)
                .password(userPassword)
                .role(role)
                .build();
        new CreateUser(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityWasCreated())
                .post(createUser);
    }

    //Метод по получению токена
    private String getToken(String userName, String userPassword) {
        LoginUserRequest loginUser1 = LoginUserRequest.builder()
                .username(userName)
                .password(userPassword)
                .build();
        return new AdminLoginUserRequest(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnsOK())
                .post(loginUser1)
                .extract()
                .header("Authorization");
    }

    //Метод для данных для успешного пополнения
    private float successfulDepositAccount(long accountId, double depositAmount, String userToken) {
        DepositAccountRequest depositUserAccount = DepositAccountRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();
        return new UserDepositAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .post(depositUserAccount)
                .extract()
                .path("balance");
    }


    //Метод для данных для неуспешного пополнения - ошибки суммы перевода
    private void unsuccessfulDepositAmountError(long accountId, double depositAmount, String userToken) {
        DepositAccountRequest depositUserAccount = DepositAccountRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();
        new UserDepositAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsBadRequest())
                .post(depositUserAccount);
    }

    //Метод для данных для неуспешного пополнения - ошибки счетов
    private void unsuccessfulDepositAccountError(long accountId, double depositAmount, String userToken) {
        DepositAccountRequest depositUserAccount = DepositAccountRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();
        new UserDepositAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsForbidden())
                .post(depositUserAccount);
    }

    @BeforeEach
    public void setupTestData() {
        //создание первого пользователя
        createUser(user1Username, user1Password, userRole);
        //Создание второго пользователя
        createUser(user2Username, user2Password, userRole);

        //Получение токена для пользователя1
        user1Token = getToken(user1Username, user1Password);
        //Получение токена для пользователя2
        user2Token = getToken(user2Username, user2Password);

        //Создание счета для пользователя1
        firstAccountUser1 = createAccount(user1Token);
        firstAccountUser2 = createAccount(user2Token);

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

        return Stream.of(
                Arguments.of("RANDOM", 100),    // метка для случайного ID
                Arguments.of("OTHER_USER", 100) // метка для чужого аккаунта
        );
    }

    @ParameterizedTest
    @DisplayName("Успешное пополнение счета первый и последующие разы")
    @MethodSource("transactionDataForPositiveCaseJson")
    public void userCanDepositAccountTest(double initialBalance,
                                          double depositAmount, double expectedBalance) {
        // 1. Устанавливаем начальный баланс (если нужно)
        if (initialBalance != 0) {
            successfulDepositAccount(firstAccountUser1, initialBalance, user1Token);
        }
        // 2. Делаем депозит
        successfulDepositAccount(firstAccountUser1, depositAmount, user1Token);
    }

    @ParameterizedTest
    @DisplayName("Ошибка: Пользователь не может пополнить счет на 0 и отрицательную сумму")
    @MethodSource("transactionDataForNegativeCase")
    public void userCannotDepositAccountTest(double initialBalance, double depositAmount) {
        // 1. Устанавливаем начальный баланс (если нужно)
        if (initialBalance != 0) {
            successfulDepositAccount(firstAccountUser1, initialBalance, user1Token);
        }
        // 2. Делаем депозит
        unsuccessfulDepositAmountError(firstAccountUser1, depositAmount, user1Token);
    }

    @ParameterizedTest
    @DisplayName("Ошибка: Пользователь не может пополнить чужой или несуществующий счет")
    @MethodSource("notExistOrSomebodyAccount")
    public void userCannotDepositNotExistAccountTest(String accountType, double depositAmount) {
        long accountId = "RANDOM".equals(accountType) ? randomIdAccount : firstAccountUser2;
        unsuccessfulDepositAccountError(accountId, depositAmount, user1Token);
    }

}