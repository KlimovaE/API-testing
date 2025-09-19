package iteration_2;

import io.restassured.path.json.JsonPath;
import models.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.*;
import spec.RequestSpecs;
import spec.ResponseSpecs;
import java.util.Random;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferToAccount {
    String user1Token;
    String user2Token;
    //Генерация уникальных userName для каждого теста
    String user1Username = "A_" + System.currentTimeMillis();
    String user2Username = "B_" + System.currentTimeMillis();
    String user1Password = "Kate012!";
    String user2Password = "Kate013!";
    long firstAccountUser1;
    long secondAccountUser1;
    long firstAccountUser2;
    String userRole = "USER";
    Random random = new Random();
    static long randomNonExistentId = new Random().nextInt(100000, 1000000);

    @BeforeEach
    public void setupTestData() {
        //Создание первого пользователя
        createUser(user1Username, user1Password, userRole);
        //Создание второго пользователя
        createUser(user2Username, user2Password, userRole);

        //Получение токена для пользователя1
        user1Token = getToken(user1Username, user1Password);
        //Получение токена для пользователя2
        user2Token = getToken(user2Username, user2Password);

        //Создание 2х счетов для пользователя 1 и 1 счета для пользователя 2
        firstAccountUser1 = createAccount(user1Token);
        secondAccountUser1 = createAccount(user1Token);
        firstAccountUser2 = createAccount(user2Token);
    }

    //Метод по созданию аккаунта
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
    //Метод по предварительному пополнению баланса
    private void depositUserAccount(String userToken, long userAccount, double balance) {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(userAccount)
                .balance(balance)
                .build();
        new UserDepositAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .post(depositAccountRequest);
    }

    private String getUserAmountsInfo(String userToken) {
        return new GetCustomerProfileRequest(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .asString();// ← получаем как строку
    }

    //Метод по проверке баланса
    private double actualAccountBalance(String userToken, long userAccount) {
        return JsonPath.from(getUserAmountsInfo(userToken))
                .getDouble("find { it.id == " + userAccount + " }.balance");
    }

    //Общий метод для успешных переводов (позитивные кейсы)
    private void runPositiveTest(long senderAccount, long receiverAccount,
                                 double initialSenderBalance, double initialReceiverBalance,
                                 double transferAmount, String userSenderToken, String userReceiverToken) {

        // Установка начального баланса
        depositUserAccount(userSenderToken, senderAccount, initialSenderBalance);
        if (initialReceiverBalance != 0) {
            depositUserAccount(userReceiverToken, receiverAccount, initialReceiverBalance);
        }

        // Перевод
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .amount(transferAmount)
                .build();

        new TransferToAccountRequest(RequestSpecs.userAuthSpec(userSenderToken), ResponseSpecs.requestReturnsOK())
                .post(request);
        double actualSenderBalance = actualAccountBalance(userSenderToken, senderAccount);
        double actualReceiverBalance = actualAccountBalance(userReceiverToken, receiverAccount);

        //Проверка, что баланс отправителя уменьшился
        assertEquals(initialSenderBalance-transferAmount, actualSenderBalance, 0.01);
        //Проверка, что баланс получателя пополнился
        assertEquals(initialReceiverBalance+transferAmount, actualReceiverBalance, 0.01);
    }

    //Общий метод пополнения для негативных запросов
    private void runNegativeTest(long senderAccount, long receiverAccount,
                                 double transferAmount, int expectedStatusCode) {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .amount(transferAmount)
                .build();

        int actualStatusCode = new TransferToAccountRequest(RequestSpecs.userAuthSpec(user1Token),
                ResponseSpecs.requestCanReturnAnyStatus())
                .post(request)
                .extract()
                .statusCode();

        assertEquals(expectedStatusCode, actualStatusCode);
    }

    public static Stream<Arguments> ownAccountsTransferData() {
        return Stream.of(
                Arguments.of(300, 0, 100),
                Arguments.of(500, 100, 150),
                Arguments.of(300, 0, 0.01),
                Arguments.of(300, 0, 300)
        );
    }

    @ParameterizedTest
    @DisplayName("Успешный перевод между своими счетами")
    @MethodSource("ownAccountsTransferData")
    public void transferBetweenOwnAccounts(double initialSenderBalance, double initialReceiverBalance,
                                           double transferAmount) {

        runPositiveTest(firstAccountUser1, secondAccountUser1,
                initialSenderBalance, initialReceiverBalance,
                transferAmount, user1Token, user1Token);

    }

    @Test
    @DisplayName("Успешный перевод другому пользователю")
    public void transferToOtherUser() {
        runPositiveTest(firstAccountUser1, firstAccountUser2, 300, 0, 0.01, user1Token, user2Token);
    }

    @Test
    @DisplayName("Ошибка: перевод с несуществующего счета")
    public void cannotTransferFromNonExistentAccount() {
        runNegativeTest(randomNonExistentId, firstAccountUser1, 300.0, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Ошибка: перевод на несуществующий счет")
    public void cannotTransferToNonExistentAccount() {
        runNegativeTest(firstAccountUser1, randomNonExistentId, 300.0, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Ошибка: перевод с чужого счета")
    public void cannotTransferFromOtherUserAccount() {
        runNegativeTest(firstAccountUser2, firstAccountUser1, 300.0, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Ошибка: счет получателя тот же, что и отправителя")
    public void cannotTransferToSameAccount() {
        depositUserAccount(user1Token, firstAccountUser1, 500);
        runNegativeTest(firstAccountUser1, firstAccountUser1, 300.0, HttpStatus.SC_BAD_REQUEST);
    }

}