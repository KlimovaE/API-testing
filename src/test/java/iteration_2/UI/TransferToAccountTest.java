package iteration_2.UI;

import api.steps.*;
import api.generators.RandomData;
import api.models.Role;
import api.models.requsts.CreateUserRequest;
import api.models.response.CreateAccountResponse;
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
    public void setupLocalStorage() {
        senderToken = authAsUser();
        CreateAccountResponse responseSenderAccount = AccountCreationSteps.createAccount(senderToken);
        senderAccountName = responseSenderAccount.getAccountNumber();
        senderAccountId = responseSenderAccount.getId();
        DepositSteps.depositAccount(senderAccountId, AMOUNT, senderToken);

        //Данные получателя
        CreateUserRequest userRequest = UserCreationSteps.createUser(RandomData.getUserName(), RandomData.getPassword(), Role.USER);
        recipientToken = UserAuthSteps.loginUser(userRequest.getUsername(), userRequest.getPassword());
        recipientName = userRequest.getUsername();
        CreateAccountResponse responseRecipientAccount = AccountCreationSteps.createAccount(recipientToken);
        recipientAccountName = responseRecipientAccount.getAccountNumber();
        recipientAccountId = responseRecipientAccount.getId();
    }

    @Test
    @DisplayName("Успешный перевод")
    public void userCanTransferToAccountTest() {
        new TransferPage().open().transferToAccount(senderAccountName, recipientName, recipientAccountName, AMOUNT)
                .checkAlertMessageAndAccept(successfulTransfer(recipientAccountName, AMOUNT));

        //Проверка через API: проверка баланса после пополнения
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

        //Проверка через API: проверка баланса после пополнения
        double actualSenderAccountBalance = GetActualBalanceSteps.getActualAccountBalance(senderToken, senderAccountId);
        double actualRecipientAccountBalance = GetActualBalanceSteps.getActualAccountBalance(recipientToken, recipientAccountId);
        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderAccountBalance).isEqualTo(AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(INITIAL_BALANCE, within(0.01));
    }
}
