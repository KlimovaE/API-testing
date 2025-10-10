package ui.pages;

import api.models.requests.CreateUserRequest;
import api.steps.UserGetTokenSteps;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;
import api.spec.RequestSpecs;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;
import api.configs.Config;



public abstract class BasePage <T extends BasePage> {
    protected SelenideElement selectAccountDropdown = $(".account-selector");
    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String bankAlert) {
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();
        return (T) this;
    }

    public static String authAsUser() {
        String token = UserGetTokenSteps.createRandomUserAndGetToken();
        String uiBaseUrl = Config.getProperty("uiBaseUrl");
        Selenide.open(uiBaseUrl);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
        return token;
    }

    public static void authAsUser(String username, String password) {
        String uiBaseUrl = Config.getProperty("uiBaseUrl");
        Selenide.open(uiBaseUrl);
        String userAuthHeader = RequestSpecs.getAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public static void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

}