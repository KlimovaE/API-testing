package api.steps;

import api.models.Account;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

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
