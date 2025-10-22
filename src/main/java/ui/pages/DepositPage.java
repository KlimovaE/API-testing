package ui.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositPage extends BasePage<DepositPage> {
    SelenideElement selectAccountDropdown = $(".account-selector");
    SelenideElement selectedAccount = $("select.account-selector");//выбираем счет по названию
    SelenideElement amountInput = $(".deposit-input");
    SelenideElement depositBtn = $(".btn-primary.shadow-custom");

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage depositAccount(String accountName, double depositAmount) {
        selectAccountDropdown.click();
        selectedAccount.selectOptionContainingText(accountName);
        amountInput.setValue(String.valueOf(depositAmount));
        depositBtn.click();
        return this;
    }
}
