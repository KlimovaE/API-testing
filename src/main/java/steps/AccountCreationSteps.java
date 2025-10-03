package steps;

import models.response.CreateAccountResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class AccountCreationSteps {
    public static CreateAccountResponse createAccount(String userToken) {
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);
    }
}
