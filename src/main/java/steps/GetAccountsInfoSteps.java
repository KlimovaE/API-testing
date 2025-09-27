package steps;

import models.Account;
import requests.GetAccountInfoRequest;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class GetAccountsInfoSteps {
    public Account[]  getAccountsInfo(String userToken) {
        return new GetAccountInfoRequest(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(Account[].class);
    }
}
