package api.steps;

import api.models.Account;

import java.util.Arrays;

public class GetActualBalanceSteps {
    public static double getActualAccountBalance(String userToken, long accountId) {
        Account[] accounts = GetAccountsInfoSteps.getAccountsInfo(userToken);

        return Arrays.stream(accounts)
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }
}
