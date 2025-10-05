package iteration_2.UI;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import models.response.CreateAccountResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.chrome.ChromeOptions;
import steps.AccountCreationSteps;
import steps.GetActualBalanceSteps;
import steps.UserGetTokenSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositAccountTest {
    private final UserGetTokenSteps userGetToken = new UserGetTokenSteps();
    private final AccountCreationSteps createAccount = new AccountCreationSteps();
    private final GetActualBalanceSteps getActualBalance = new GetActualBalanceSteps();
    private final double INITIAL_BALANCE = 0.00;
    private final double DEPOSIT_AMOUNT = 1000.01;
    private final double INVALID_DEPOSIT_AMOUNT = 5000.01;


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
        String userToken = userGetToken.createRandomUserAndGetToken();
        CreateAccountResponse response = createAccount.createAccount(userToken);
        String accountName = response.getAccountNumber();
        long accountId = response.getId();
        System.out.println(accountName);

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userToken);
        Selenide.open("/deposit");
        $(".account-selector").click();
        $("select.account-selector").selectOptionContainingText(accountName);//выбираем счет по названию
        $(".deposit-input").setValue(String.valueOf(DEPOSIT_AMOUNT));
        $(".btn-primary.shadow-custom").click();

        //Проверка на UI
        Alert alert = switchTo().alert();
        assertEquals("✅ Successfully deposited $" + DEPOSIT_AMOUNT + " to account " + accountName + "!", alert.getText(),
                "Должно быть всплывающее окно с сообщением об успешном пополнении");

        //Проверка через API: проверка баланса после пополнения
        double actualBalance = getActualBalance.getActualAccountBalance(userToken, accountId);
        assertEquals(DEPOSIT_AMOUNT, actualBalance, "Баланс счета должен быть равен сумме пополнения");
    }

    @Test
    @DisplayName("Ошибка: Неуспешное пополнение счета")
    public void userCanNotDepositAccountTest() {
        String userToken = userGetToken.createRandomUserAndGetToken();
        CreateAccountResponse response = createAccount.createAccount(userToken);
        String accountName = response.getAccountNumber();
        long accountId = response.getId();
        System.out.println(accountName);

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userToken);
        Selenide.open("/deposit");
        $(".account-selector").click();
        $("select.account-selector").selectOptionContainingText(accountName);//выбираем счет по названию
        $(".deposit-input").setValue(String.valueOf(INVALID_DEPOSIT_AMOUNT));
        $(".btn-primary.shadow-custom").click();

        //Проверка на UI
        Alert alert = switchTo().alert();
        assertEquals("❌ Please deposit less or equal to 5000$.", alert.getText(),
                "Должно быть всплывающее окно с сообщением о неуспешном пополнении");

        //Проверка через API: проверка баланса после пополнения
        double actualBalance = getActualBalance.getActualAccountBalance(userToken, accountId);
        assertEquals(INITIAL_BALANCE, actualBalance, "Баланс счета не должен пополниться");
    }
}
