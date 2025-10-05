package iteration_2.API;

import models.requsts.DepositAccountRequest;
import models.response.DepositAccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.ValidatedCrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;
import steps.AccountCreationSteps;
import steps.DepositSteps;
import steps.GetActualBalanceSteps;
import steps.UserGetTokenSteps;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositAccountTest {
    private final double DEPOSIT_AMOUNT = 100.00;
    private final double INITIAL_BALANCE = 0.00;
    private String user1Token;
    private long firstAccountUser1;


    @BeforeEach
    public void setupTestData() {
        //Получение токена для пользователя1
        user1Token = UserGetTokenSteps.createRandomUserAndGetToken();
        //Создание счета для пользователя1
        firstAccountUser1 = AccountCreationSteps.createAccount(user1Token).getId();
    }

    public static Stream<Arguments> depositAccountPositiveCases() {
        return Stream.of(
                Arguments.of(100),
                Arguments.of(0.01),
                //Гарантированное максимальное пополнение
                Arguments.of(4999.99),
                //Проверка границы максимальной суммы пополнения(включительно ли 5000)
                Arguments.of(5000)
        );
    }

    public static Stream<Arguments> depositAccountNegativeCases() {
        return Stream.of(
                //Пополнение на ноль
                Arguments.of(0),
                //Пополнение на отрицательную сумму
                Arguments.of(-100),
                //Превышение максимальной суммы пополнения
                Arguments.of(5000.01),
                Arguments.of(5001)
        );
    }

    @ParameterizedTest
    @DisplayName("Успешное пополнение счета первый раз(инициализация счета)")
    @MethodSource("depositAccountPositiveCases")
    public void userCanFirstDepositAccountTest(double depositAmount) {
        //Пополняем счет и получаем актуальный баланс
        DepositAccountRequest depositAccount = DepositAccountRequest.builder()
                .id(firstAccountUser1)
                .balance(depositAmount)
                .build();

        DepositAccountResponse response = (DepositAccountResponse) new ValidatedCrudRequester<DepositAccountResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositAccount);

        double actualBalance = response.getBalance();

        //Проверяем баланс после пополнения
        assertEquals(depositAmount, actualBalance, 0.001, "Баланс должен быть равен сумме пополнения");
    }

    @ParameterizedTest
    @DisplayName("Успешное пополнение счета последующие разы")
    @MethodSource("depositAccountPositiveCases")
    public void userCanDepositAccountTest(double depositAmount) {
        // 1. Устанавливаем начальный баланс
        DepositSteps.depositAccount(firstAccountUser1, DEPOSIT_AMOUNT, user1Token);
        // 2. Делаем депозит и получаем актуальный баланс
        DepositAccountRequest depositAccount = DepositAccountRequest.builder()
                .id(firstAccountUser1)
                .balance(depositAmount)
                .build();

        DepositAccountResponse response = (DepositAccountResponse) new ValidatedCrudRequester<DepositAccountResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositAccount);

        double actualBalance = response.getBalance();

        //Проверяем баланс после пополнения
        assertEquals(DEPOSIT_AMOUNT + depositAmount, actualBalance, 0.001, "Баланс должен увеличиться на сумму пополнения");
    }

    @ParameterizedTest
    @DisplayName("Ошибка: Пользователь не может пополнить счет на невалидную сумму")
    @MethodSource("depositAccountNegativeCases")
    public void userCannotDepositAccountTest(double depositAmount) {
        // 1. Делаем депозит
        DepositAccountRequest depositAccount = DepositAccountRequest.builder()
                .id(firstAccountUser1)
                .balance(depositAmount)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest())
                .post(depositAccount);

        //Получаем баланс и проверяем, что он не изменился
        double actualBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        assertEquals(INITIAL_BALANCE, actualBalance, 0.01, "Баланс не должен был измениться");
    }

    @Test
    @DisplayName("Ошибка: Пользователь не может пополнить чужой счет")
    public void userCannotDepositSomebodyAccountTest() {

        //1. Создаем счет другого пользователя
        long firstAccountUser2 = AccountCreationSteps.createAccount(UserGetTokenSteps.createRandomUserAndGetToken()).getId();
        // 2. Пополняем чужой счет
        DepositAccountRequest depositAccount = DepositAccountRequest.builder()
                .id(firstAccountUser2)
                .balance(DEPOSIT_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositAccount);//под капотом проверка, что пришел код ответа 403
    }

    @Test
    @DisplayName("Ошибка: Пользователь не может пополнить несуществующий счет")
    public void userCannotDepositNotExistAccountTest() {
        //Создаем случайный счет
        long randomIdAccount = new Random().nextInt(10000, 1000000);
        // 2. Пополняем чужой счет
        DepositAccountRequest depositAccount = DepositAccountRequest.builder()
                .id(randomIdAccount)
                .balance(DEPOSIT_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositAccount);//под капотом проверка, что пришел код ответа 403
    }
}