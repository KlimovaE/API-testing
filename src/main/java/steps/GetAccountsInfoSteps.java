package steps;

import models.Account;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class GetAccountsInfoSteps {
    public static Account[]  getAccountsInfo(String userToken) {
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.ACCOUNTS_INFO,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(Account[].class);
    }
}
