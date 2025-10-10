package iteration_2.UI;

import api.steps.*;
import api.models.requests.CreateUserRequest;
import api.models.response.CreateAccountResponse;
import common.annotations.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransferPage;

import static org.assertj.core.api.AssertionsForClassTypes.within;
import static ui.pages.BankAlert.successfulTransfer;

public class TransferToAccountTest extends BaseUiTest {
    private final double INITIAL_BALANCE = 0.00;
    private final double AMOUNT = 1000.01;
    private final double INVALID_TRANSIT_AMOUNT = 10000.01;
    String senderToken;
    String senderAccountName;
    long senderAccountId;
    String recipientToken;
    String recipientName;
    String recipientAccountName;
    long recipientAccountId;

    @BeforeEach
    @UserSession(value = 2, auth = 1) // Создаем 2 пользователей, авторизуем как первый
    public void setupUsersAndAccounts() {
        System.out.println("=== DEBUG: setupUsersAndAccounts started ===");

        // Проверяем, что пользователи созданы
        try {
            CreateUserRequest senderUser = common.storage.SessionStorage.getUser(1);
            CreateUserRequest recipientUser = common.storage.SessionStorage.getUser(2);
            System.out.println("DEBUG: Users found - Sender: " + senderUser.getUsername() +
                    ", Recipient: " + recipientUser.getUsername());
        } catch (Exception e) {
            System.out.println("DEBUG: Error getting users: " + e.getMessage());
            throw e;
        }

        // Получаем пользователей из SessionStorage
        CreateUserRequest senderUser = common.storage.SessionStorage.getUser(1);
        senderToken = UserAuthSteps.loginUser(senderUser.getUsername(), senderUser.getPassword());

        CreateAccountResponse senderAccount = AccountCreationSteps.createAccount(senderToken);
        senderAccountName = senderAccount.getAccountNumber();
        senderAccountId = senderAccount.getId();
        DepositSteps.depositAccount(senderAccountId, AMOUNT, senderToken);

        CreateUserRequest recipientUser = common.storage.SessionStorage.getUser(2);
        recipientToken = UserAuthSteps.loginUser(recipientUser.getUsername(), recipientUser.getPassword());
        recipientName = recipientUser.getUsername();

        CreateAccountResponse recipientAccount = AccountCreationSteps.createAccount(recipientToken);
        recipientAccountName = recipientAccount.getAccountNumber();
        recipientAccountId = recipientAccount.getId();
    }



    @Test
    @DisplayName("Успешный перевод")
    public void userCanTransferToAccountTest() {
        new TransferPage().open().transferToAccount(senderAccountName, recipientName, recipientAccountName, AMOUNT)
                .checkAlertMessageAndAccept(successfulTransfer(recipientAccountName, AMOUNT));

        //Проверка через API: проверка баланса после перевода
        double actualSenderAccountBalance = GetActualBalanceSteps.getActualAccountBalance(senderToken, senderAccountId);
        double actualRecipientAccountBalance = GetActualBalanceSteps.getActualAccountBalance(recipientToken, recipientAccountId);
        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderAccountBalance).isEqualTo(AMOUNT - AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(INITIAL_BALANCE + AMOUNT, within(0.01));
    }

    @Test
    @DisplayName("Ошибка: Неуспешный перевод")
    public void userCanNotTransferToAccountTest() {
        new TransferPage().open().transferToAccount(senderAccountName, recipientName, recipientAccountName, INVALID_TRANSIT_AMOUNT)
                .checkAlertMessageAndAccept(BankAlert.UNSUCCESSFULLY_TRANSFER.getMessage());

        //Проверка через API: проверка баланса после перевода
        double actualSenderAccountBalance = GetActualBalanceSteps.getActualAccountBalance(senderToken, senderAccountId);
        double actualRecipientAccountBalance = GetActualBalanceSteps.getActualAccountBalance(recipientToken, recipientAccountId);
        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderAccountBalance).isEqualTo(AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(INITIAL_BALANCE, within(0.01));
    }
}
