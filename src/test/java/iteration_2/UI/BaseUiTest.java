package iteration_2.UI;

import api.configs.Config;
import api.generators.RandomData;
import api.spec.RequestSpecs;
import api.steps.UserGetTokenSteps;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import iteration_2.API.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

    public class BaseUiTest extends BaseTest {
        @BeforeAll
        public static void setupSelenoid() {
            Configuration.remote = Config.getProperty("uiRemote");
            Configuration.baseUrl = Config.getProperty("uiBaseUrl");
            Configuration.browser = Config.getProperty("browser");
            Configuration.browserSize = Config.getProperty("browserSize");

            // Правильная конфигурация для Selenoid
            Map<String, Object> selenoidOptions = new HashMap<>();
            selenoidOptions.put("enableVNC", true);
            selenoidOptions.put("enableLog", true);

            // ChromeOptions для Selenoid
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            options.addArguments("--disable-extensions");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

            // Объединяем capabilities
            Configuration.browserCapabilities.setCapability("selenoid:options", selenoidOptions);
            Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }

        public String authAsUser() {
            String token = UserGetTokenSteps.createRandomUserAndGetToken();
            Selenide.open("/");
            executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
            return token;
        }
}
