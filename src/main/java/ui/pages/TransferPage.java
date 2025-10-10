package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage<TransferPage>{

    private SelenideElement selectAccountDropdown = $("select.form-control.account-selector");//аналогичен депозиту
    private SelenideElement selectedSenderAccountName = $("select.account-selector");
    private SelenideElement recipientAccountNameInput = $("[placeholder='Enter recipient name']");
    private SelenideElement recipientAccountNumberImput = $("[placeholder='Enter recipient account number']");
    private SelenideElement amountInput = $("[placeholder='Enter amount']");
    private SelenideElement confirmCheckbox = $("#confirmCheck");
    private SelenideElement sendTransferBtn = $(".btn-primary.shadow-custom");



    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage transferToAccount(String senderAccountName, String recipientName, String recipientAccountNumber, double amount) {
        selectAccountDropdown.click();
        selectedSenderAccountName.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        selectAccountDropdown.selectOptionContainingText(senderAccountName);
        recipientAccountNameInput.setValue(recipientName);
        recipientAccountNumberImput.setValue(String.valueOf(recipientAccountNumber));
        amountInput.setValue(String.valueOf(amount));
        confirmCheckbox.setSelected(true);
        sendTransferBtn.click();
        return this;
    }
}
