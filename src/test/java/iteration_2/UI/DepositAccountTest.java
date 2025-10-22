package iteration_2.UI;

import api.models.requests.CreateUserRequest;
import api.steps.GetActualBalanceSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.pages.BankAlert.successfulDeposit;

public class DepositAccountTest extends BaseUiTest {
    private final double INITIAL_BALANCE = 0.00;
    private final double DEPOSIT_AMOUNT = 1000.01;
    private final double INVALID_DEPOSIT_AMOUNT = 5000.01;
    CreateUserRequest user;

    public void setUserData() {
        // Сначала авторизуем пользователя для UI
        user = SessionStorage.getUser();
        authAsUser(user);
    }

    @Test
    @UserSession
    @DisplayName("Успешное пополнение счета")
    public void userCanDepositAccountTest() {
        setUserData();
        new DepositPage().open().depositAccount(SessionStorage.getUserAccount().getAccountNumber(), DEPOSIT_AMOUNT)
                .checkAlertMessageAndAccept(successfulDeposit(SessionStorage.getUserAccount().getAccountNumber(), DEPOSIT_AMOUNT));

        //Проверка через API: проверка баланса после пополнения
        double actualBalance = GetActualBalanceSteps.getActualAccountBalance(SessionStorage.getUserToken(), SessionStorage.getUserAccount().getId());
        assertEquals(DEPOSIT_AMOUNT, actualBalance, "Баланс счета должен быть равен сумме пополнения");
    }

    @Test
    @UserSession
    @DisplayName("Ошибка: Неуспешное пополнение счета")
    public void userCanNotDepositAccountTest() {
        setUserData();
        new DepositPage().open().depositAccount(SessionStorage.getUserAccount(1).getAccountNumber(), INVALID_DEPOSIT_AMOUNT)
                .checkAlertMessageAndAccept(BankAlert.UNSUCCESSFULLY_DEPOSIT.getMessage());

        //Проверка через API: проверка баланса после пополнения
        double actualBalance = GetActualBalanceSteps.getActualAccountBalance(SessionStorage.getUserToken(), SessionStorage.getUserAccount().getId());
        assertEquals(INITIAL_BALANCE, actualBalance, "Баланс счета не должен пополниться");
    }
}
