package steps;

import models.Account;

import java.util.Arrays;

public class GetActualBalanceSteps {
    GetAccountsInfoSteps getAccountsInfo = new GetAccountsInfoSteps();

    public double getActualAccountBalance(String userToken, long accountId) {
        Account[] accounts = getAccountsInfo.getAccountsInfo(userToken);

        return Arrays.stream(accounts)
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }
}
