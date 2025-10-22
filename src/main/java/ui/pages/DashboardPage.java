package ui.pages;


import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter

public class DashboardPage extends BasePage<DashboardPage> {
    SelenideElement userNameInfo = $(".user-name");

    @Override
    public String url() {
        return "/dashboard";
    }

    // Действие, выполняемое на ЭТОЙ странице
    public EditProfilePage goToChangeName() {
        userNameInfo.click();  // ← Действие на DashboardPage
        return new EditProfilePage();  // ← Результат действия - НОВАЯ страница
    }
}
