package iteration_2.UI;

import api.models.requests.CreateUserRequest;
import api.steps.GetActualBalanceSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
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
    CreateUserRequest sender;
    CreateUserRequest recipient;

    public void setUserData() {
        // Сначала авторизуем отправителя для UI
        sender = SessionStorage.getUser(1);
        authAsUser(sender); // Устанавливаем токен в localStorage
        // Получаем данные получателя
        recipient = SessionStorage.getUser(2);
    }

    @Test
    @UserSession(value = 2, balances = {AMOUNT, INITIAL_BALANCE}, auth = 1)
    @DisplayName("Успешный перевод")
    public void userCanTransferToAccountTest() {

        setUserData();
        // Теперь выполняем тест
        new TransferPage()
                .open()
                .transferToAccount(
                        SessionStorage.getUserAccount(1).getAccountNumber(),
                        recipient.getUsername(),
                        SessionStorage.getUserAccount(2).getAccountNumber(),
                        AMOUNT
                ).checkAlertMessageAndAccept(successfulTransfer(SessionStorage.getUserAccount(2).getAccountNumber(), AMOUNT));

        // Проверка через API
        double actualSenderAccountBalance = GetActualBalanceSteps.getActualAccountBalance(
                SessionStorage.getUserToken(),
                SessionStorage.getUserAccount().getId()
        );
        double actualRecipientAccountBalance = GetActualBalanceSteps.getActualAccountBalance(
                SessionStorage.getUserToken(2),
                SessionStorage.getUserAccount(2).getId()
        );

        softly.assertThat(actualSenderAccountBalance).isEqualTo(INITIAL_BALANCE, within(0.01));
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(AMOUNT, within(0.01));
    }

    @Test
    @UserSession(value = 2, balances = {AMOUNT, INITIAL_BALANCE}, auth = 1)
    @DisplayName("Ошибка: Неуспешный перевод")
    public void userCanNotTransferToAccountTest() {
        setUserData();
        new TransferPage().open().transferToAccount(
                SessionStorage.getUserAccount().getAccountNumber(),
                SessionStorage.getUser(2).getUsername(),
                SessionStorage.getUserAccount(2).getAccountNumber(),
                INVALID_TRANSIT_AMOUNT
        ).checkAlertMessageAndAccept(BankAlert.UNSUCCESSFULLY_TRANSFER.getMessage());

        double actualSenderAccountBalance = GetActualBalanceSteps.getActualAccountBalance(
                SessionStorage.getUserToken(),
                SessionStorage.getUserAccount().getId()
        );
        double actualRecipientAccountBalance = GetActualBalanceSteps.getActualAccountBalance(
                SessionStorage.getUserToken(2),
                SessionStorage.getUserAccount(2).getId()
        );

        softly.assertThat(actualSenderAccountBalance).isEqualTo(AMOUNT, within(0.01));
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(INITIAL_BALANCE, within(0.01));
    }
}