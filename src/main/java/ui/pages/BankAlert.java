package ui.pages;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum BankAlert {
    SUCCESSFULLY_CHANGE_NAME ("✅ Name updated successfully!"),
    UNSUCCESSFULLY_CHANGE_NAME ("Name must contain two words with letters only"),
    UNSUCCESSFULLY_TRANSFER("❌ Error: Transfer amount cannot exceed 10000"),
    UNSUCCESSFULLY_DEPOSIT("❌ Please deposit less or equal to 5000$.");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }

    // Статические методы для динамических сообщений
    public static String successfulTransfer(String recipientAccountName, double amount) {
        return String.format(Locale.US, "✅ Successfully transferred $%.2f to account %s!", amount, recipientAccountName);
    }

    public static String successfulDeposit(String accountName, double amount) {
        // Используем US локаль для точки как разделителя
        return String.format(Locale.US, "✅ Successfully deposited $%.2f to account %s!", amount, accountName);
    }
}
