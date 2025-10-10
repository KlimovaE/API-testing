package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage>{

private SelenideElement editProfilePanelText = $(Selectors.byText("✏\uFE0F Edit Profile"));
    private SelenideElement newNameInput = $(".form-control.mt-3");
    private SelenideElement saveChangesBtn = $(".btn-primary");

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage verifyPanelVisible() {
        getEditProfilePanelText().shouldBe(Condition.visible);
        return this; // Возвращает страницу
    }

    public EditProfilePage editName(String newName) {
        newNameInput.setValue(newName);
        saveChangesBtn.click();
        return this;// Возвращает страницу
    }
}
