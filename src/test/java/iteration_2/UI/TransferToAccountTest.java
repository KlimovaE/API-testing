package iteration_2.UI;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import generators.RandomData;
import models.Role;
import models.requsts.CreateUserRequest;
import models.response.CreateAccountResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.chrome.ChromeOptions;
import steps.*;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferToAccountTest extends BaseTest{
    private final UserGetTokenSteps userGetToken = new UserGetTokenSteps();
    private final AccountCreationSteps createAccount = new AccountCreationSteps();
    private final GetActualBalanceSteps getActualBalance = new GetActualBalanceSteps();
    private final DepositSteps deposit = new DepositSteps();
    private final double INITIAL_BALANCE = 0.00;
    private final double AMOUNT = 1000.01;
    private final double INVALID_TRANSIT_AMOUNT = 10000.01;
    private final UserCreationSteps newUser = new UserCreationSteps();
    private final UserAuthSteps userAuth = new UserAuthSteps();

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.107:3001";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));

        //дополнительная настройка браузера для снятия блокировки работы с LocalStorage
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-extensions");
        Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
    }

    @Test
    @DisplayName("Успешное пополнение счета")
    public void userCanDepositAccountTest() {
        //данные отправителя
        String senderToken = userGetToken.createRandomUserAndGetToken();
        CreateAccountResponse responseSenderAccount = createAccount.createAccount(senderToken);
        String senderAccountName = responseSenderAccount.getAccountNumber();
        long senderAccountId = responseSenderAccount.getId();
        deposit.depositAccount(senderAccountId, AMOUNT, senderToken);

        //Данные получателя
        CreateUserRequest userRequest = newUser.createUser(RandomData.getUserName(), RandomData.getPassword(), Role.USER);
        String recipientToken = userAuth.loginUser(userRequest.getUsername(), userRequest.getPassword());
        CreateAccountResponse responseRecipientAccount = createAccount.createAccount(recipientToken);
        String recipientAccountName = responseRecipientAccount.getAccountNumber();
        long RecipientAccountId = responseRecipientAccount.getId();

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", senderToken);
        Selenide.open("/transfer");

        $(".account-selector").click();
        $("select.account-selector").selectOptionContainingText(senderAccountName);
        $("[placeholder='Enter recipient name']").setValue(userRequest.getUsername());
        $("[placeholder='Enter recipient account number']").setValue(recipientAccountName);
        $("[placeholder='Enter amount']").setValue(String.valueOf(AMOUNT));
        $("#confirmCheck").setSelected(true);
        $(".btn-primary.shadow-custom").click();

        //Проверка на UI
        Alert alert = switchTo().alert();

        assertEquals("✅ Successfully transferred $" + AMOUNT + " to account " + recipientAccountName + "!", alert.getText(),
                "Должно быть всплывающее окно с сообщением об успешном переводе");

        //Проверка через API: проверка баланса после пополнения
        double actualSenderAccountBalance = getActualBalance.getActualAccountBalance(senderToken, senderAccountId);
        double actualRecipientAccountBalance = getActualBalance.getActualAccountBalance(recipientToken, RecipientAccountId);
        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderAccountBalance).isEqualTo(AMOUNT-AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(INITIAL_BALANCE+AMOUNT, within(0.01));
    }

    @Test
    @DisplayName("Ошибка: Неуспешное пополнение счета")
    public void userCanNotDepositAccountTest() {
        //данные отправителя
        String senderToken = userGetToken.createRandomUserAndGetToken();
        CreateAccountResponse responseSenderAccount = createAccount.createAccount(senderToken);
        String senderAccountName = responseSenderAccount.getAccountNumber();
        long senderAccountId = responseSenderAccount.getId();
        deposit.depositAccount(senderAccountId, AMOUNT, senderToken);

        //Данные получателя
        CreateUserRequest userRequest = newUser.createUser(RandomData.getUserName(), RandomData.getPassword(), Role.USER);
        String recipientToken = userAuth.loginUser(userRequest.getUsername(), userRequest.getPassword());
        CreateAccountResponse responseRecipientAccount = createAccount.createAccount(recipientToken);
        String recipientAccountName = responseRecipientAccount.getAccountNumber();
        long RecipientAccountId = responseRecipientAccount.getId();

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", senderToken);
        Selenide.open("/transfer");

        $(".account-selector").click();
        $("select.account-selector").selectOptionContainingText(senderAccountName);
        $("[placeholder='Enter recipient name']").setValue(userRequest.getUsername());
        $("[placeholder='Enter recipient account number']").setValue(recipientAccountName);
        $("[placeholder='Enter amount']").setValue(String.valueOf(INVALID_TRANSIT_AMOUNT));
        $("#confirmCheck").setSelected(true);
        $(".btn-primary.shadow-custom").click();

        //Проверка на UI
        Alert alert = switchTo().alert();

        assertEquals("❌ Error: Transfer amount cannot exceed 10000", alert.getText(),
                "Должно быть всплывающее окно с сообщением о неуспешном переводе");

        //Проверка через API: проверка баланса после пополнения
        double actualSenderAccountBalance = getActualBalance.getActualAccountBalance(senderToken, senderAccountId);
        double actualRecipientAccountBalance = getActualBalance.getActualAccountBalance(recipientToken, RecipientAccountId);
        //Проверка, что баланс отправителя уменьшился
        softly.assertThat(actualSenderAccountBalance).isEqualTo(AMOUNT, within(0.01));
        //Проверка, что баланс получателя пополнился
        softly.assertThat(actualRecipientAccountBalance).isEqualTo(INITIAL_BALANCE, within(0.01));
    }
}
