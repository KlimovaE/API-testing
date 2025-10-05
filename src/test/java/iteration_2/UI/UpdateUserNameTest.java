package iteration_2.UI;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import generators.RandomData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.chrome.ChromeOptions;
import steps.GetProfileInfoSteps;
import steps.UserGetTokenSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UpdateUserNameTest {
    private final UserGetTokenSteps userGetToken = new UserGetTokenSteps();
    GetProfileInfoSteps getUserInfo = new GetProfileInfoSteps();
    private final String NEW_NAME = RandomData.getRandomValidName();
    private final String INVALID_NAME = NEW_NAME + NEW_NAME;

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
    @DisplayName("Успешное изменение имени пользователя")
    public void userCanUpdateNameTest() {
        String userToken = userGetToken.createRandomUserAndGetToken();

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userToken);
        Selenide.open("/dashboard");
        $(".user-name").click();
        $(".form-control.mt-3").setValue(NEW_NAME);
        $(".btn-primary").click();

        Alert alert = switchTo().alert();
        assertEquals("✅ Name updated successfully!", alert.getText(), "Должно быть всплывающее окно с сообщением об успешном изменении имени");

        String actualName = getUserInfo.getProfileInfo(userToken).getName();
        assertEquals(NEW_NAME, actualName, "Значение имени пользователя должно было измениться на новое");
    }

    @Test
    @DisplayName("Неспешное изменение имени пользователя")
    public void userCanNotUpdateNameTest() {
        String userToken = userGetToken.createRandomUserAndGetToken();

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userToken);
        Selenide.open("/dashboard");
        $(".user-name").click();
        $(".form-control.mt-3").setValue(NEW_NAME + NEW_NAME);
        $(".btn-primary").click();

        Alert alert = switchTo().alert();
        assertEquals("Name must contain two words with letters only", alert.getText(), "Должно быть всплывающее окно с сообщением об успешном изменении имени");

        String actualName = getUserInfo.getProfileInfo(userToken).getName();
        assertNotEquals(INVALID_NAME, actualName, "Значение имени пользователя не должно было измениться на новое");
    }
}
