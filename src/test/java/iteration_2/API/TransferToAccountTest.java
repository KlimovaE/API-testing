package iteration_2.API;

import models.requsts.TransferAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;
import steps.AccountCreationSteps;
import steps.DepositSteps;
import steps.GetActualBalanceSteps;
import steps.UserGetTokenSteps;

import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferToAccountTest extends BaseTest {private final double TRANSFER_AMOUNT = 100.00;
    private final double INITIAL_BALANCE = 4999.99;
    private String user1Token;
    private String user2Token;
    private long firstAccountUser1;
    long secondAccountUser1;
    long firstAccountUser2;
    static long randomNonExistentId = new Random().nextInt(100000, 1000000);

    @BeforeEach
    public void setupTestData() {
        //Получение токена для пользователей
        user1Token = UserGetTokenSteps.createRandomUserAndGetToken();
        user2Token = UserGetTokenSteps.createRandomUserAndGetToken();

        //Создание счетов
        firstAccountUser1 = AccountCreationSteps.createAccount(user1Token).getId();
        secondAccountUser1 = AccountCreationSteps.createAccount(user1Token).getId();
        firstAccountUser2 = AccountCreationSteps.createAccount(user2Token).getId();

        DepositSteps.depositAccount(firstAccountUser1, INITIAL_BALANCE, user1Token);
    }

    public static Stream<Arguments> ownAccountsTransferData() {
        return Stream.of(
                Arguments.of(100),//у получателя 0 на счету
                Arguments.of( 150),//у получателя есть сумма на счету
                Arguments.of(0.01),//минимальный перевод
                Arguments.of(300)//вся сумма на счету отправителя
        );
    }

    public static Stream<Arguments> maximumAmountTransferData() {
        return Stream.of(
                Arguments.of(9999.99),//максимально гарантированная возможная сумма к перевожу
                Arguments.of(10000)//максимально возможная, граничное значение
        );
    }

    public static Stream<Arguments> dataForNegativeTransitAmountErrorCases() {
        return Stream.of(
                //Перевод ноль
                Arguments.of(0),
                //Перевод на отрицательную сумму
                Arguments.of(-5000),
                Arguments.of(-100),
                //Сумма превышающая баланс отправителя
                Arguments.of(5000)
        );
    }

    public static Stream<Arguments> dataForNegativeTransitAmountMoreThanMaximum() {
        return Stream.of(
                //Пополнение на превышающую максимальную сумму
                Arguments.of(10000.01),
                Arguments.of(10001)
        );
    }

    @ParameterizedTest
    @DisplayName("Успешный перевод между своими счетами, счет получателя не ни разу пополнялся")
    @MethodSource("ownAccountsTransferData")
    public void transferBetweenOwnAccounts(double transferAmount) {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(secondAccountUser1)
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        //Получаем баланс отправителя и получателя после перевода
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        double actualReceiverBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, secondAccountUser1);

        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderBalance).isEqualTo(INITIAL_BALANCE-transferAmount, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualReceiverBalance).isEqualTo(transferAmount, within(0.01));
        }

    @Test
        @DisplayName("Успешный перевод между своими счетами, счет получателя имеет баланс")
    public void transferBetweenOwnAccountsWithExistingBalance() {
        // 1. Установка начального баланса получателя
        DepositSteps.depositAccount(secondAccountUser1, INITIAL_BALANCE, user1Token);

        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(secondAccountUser1)
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        //Получаем баланс отправителя и получателя после перевода
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        double actualReceiverBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, secondAccountUser1);

        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderBalance).isEqualTo(INITIAL_BALANCE-TRANSFER_AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualReceiverBalance).isEqualTo(INITIAL_BALANCE+TRANSFER_AMOUNT, within(0.01));
    }

    @ParameterizedTest
    @MethodSource("maximumAmountTransferData")
    @DisplayName("Успешный перевод максимальной суммы")
    public void transferMaximumAmount(double transferAmount) {
        // 1. Дополнительное пополнение счета отправителя для суммы баланса больше 10000
        DepositSteps.depositAccount(firstAccountUser1, INITIAL_BALANCE, user1Token);
        DepositSteps.depositAccount(firstAccountUser1, INITIAL_BALANCE, user1Token);
        double senderSAccountBalanceAfterDeposit = INITIAL_BALANCE*3;// счет пополнили 3 раза до нужной суммы


        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(secondAccountUser1)
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        //Получаем баланс отправителя и получателя после перевода
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        double actualReceiverBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, secondAccountUser1);

        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderBalance).isEqualTo(senderSAccountBalanceAfterDeposit- transferAmount, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualReceiverBalance).isEqualTo(transferAmount, within(0.01));
    }

    @Test
    @DisplayName("Успешный перевод другому пользователю")
    public void transferToOtherUser() {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(firstAccountUser2)
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        //Получаем баланс отправителя и получателя после перевода
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        double actualReceiverBalance = GetActualBalanceSteps.getActualAccountBalance(user2Token, firstAccountUser2);

        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderBalance).isEqualTo(INITIAL_BALANCE-TRANSFER_AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualReceiverBalance).isEqualTo(TRANSFER_AMOUNT, within(0.01));
    }

    @ParameterizedTest
    @MethodSource("dataForNegativeTransitAmountMoreThanMaximum")
    @DisplayName("Ошибка: перевод суммы больше максимума")
    public void cannotTransferTransitAmountMoreThanMaximumError(double transferAmount) {
        DepositSteps.depositAccount(firstAccountUser1, INITIAL_BALANCE, user1Token);
        DepositSteps.depositAccount(firstAccountUser1, INITIAL_BALANCE, user1Token);
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(firstAccountUser1)
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        //Проверка, что баланс отправителя не изменился
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        assertEquals(INITIAL_BALANCE, actualSenderBalance, 0.01);
    }

    @ParameterizedTest
    @MethodSource("dataForNegativeTransitAmountErrorCases")
    @DisplayName("Ошибка: перевод невалидной суммы")
    public void cannotTransferTransitAmountError(double transferAmount) {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(firstAccountUser1)
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(request);

        //Проверка, что баланс отправителя не изменился
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        assertEquals(INITIAL_BALANCE, actualSenderBalance, 0.01); }

    @Test
    @DisplayName("Ошибка: перевод с несуществующего счета")
    public void cannotTransferFromNonExistentAccount() {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(randomNonExistentId)
                .receiverAccountId(firstAccountUser1)
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsForbidden())
                .post(request);//под капотом проверка, что пришел код ответа 403
    }

    @Test
    @DisplayName("Ошибка: перевод на несуществующий счет")
    public void cannotTransferToNonExistentAccount() {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(randomNonExistentId)
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(request);

        //Проверка, что баланс отправителя не изменился
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        assertEquals(INITIAL_BALANCE, actualSenderBalance, 0.01);
    }

    @Test
    @DisplayName("Ошибка: перевод с чужого счета")
    public void cannotTransferFromOtherUserAccount() {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser2)
                .receiverAccountId(firstAccountUser1)
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsForbidden())
                .post(request);//под капотом проверка, что пришел код ответа 403
    }

    @Test
    @DisplayName("Ошибка: счет получателя тот же, что и отправителя")
    public void cannotTransferToSameAccount() {
        TransferAccountRequest request = TransferAccountRequest.builder()
                .senderAccountId(firstAccountUser1)
                .receiverAccountId(firstAccountUser1)
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(request);

        //Проверка, что баланс отправителя не изменился
        double actualSenderBalance = GetActualBalanceSteps.getActualAccountBalance(user1Token, firstAccountUser1);
        assertEquals(INITIAL_BALANCE, actualSenderBalance, 0.01);
    }
}