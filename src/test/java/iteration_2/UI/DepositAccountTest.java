package iteration_2.UI;

import api.models.response.CreateAccountResponse;
import api.steps.AccountCreationSteps;
import api.steps.GetActualBalanceSteps;
import org.junit.jupiter.api.BeforeEach;
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
    private String userToken;
    private String accountName;
    private long accountId;

    @BeforeEach
    public void setupLocalStorage() {
        userToken = authAsUser();
        CreateAccountResponse response = AccountCreationSteps.createAccount(userToken);
        accountName = response.getAccountNumber();
        accountId = response.getId();
    }

    @Test
    @DisplayName("Успешное пополнение счета")
    public void userCanDepositAccountTest() {
new DepositPage().open().depositAccount(accountName, DEPOSIT_AMOUNT).checkAlertMessageAndAccept(successfulDeposit(accountName, DEPOSIT_AMOUNT));

        //Проверка через API: проверка баланса после пополнения
        double actualBalance = GetActualBalanceSteps.getActualAccountBalance(userToken, accountId);
        assertEquals(DEPOSIT_AMOUNT, actualBalance, "Баланс счета должен быть равен сумме пополнения");
    }

    @Test
    @DisplayName("Ошибка: Неуспешное пополнение счета")
    public void userCanNotDepositAccountTest() {
        new DepositPage().open().depositAccount(accountName, INVALID_DEPOSIT_AMOUNT).checkAlertMessageAndAccept(BankAlert.UNSUCCESSFULLY_DEPOSIT.getMessage());

        //Проверка через API: проверка баланса после пополнения
        double actualBalance = GetActualBalanceSteps.getActualAccountBalance(userToken, accountId);
        assertEquals(INITIAL_BALANCE, actualBalance, "Баланс счета не должен пополниться");
    }
}
