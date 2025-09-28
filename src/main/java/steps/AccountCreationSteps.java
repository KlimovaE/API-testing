package steps;

import models.response.CreateAccountResponse;
import requests.CreateAccount;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class AccountCreationSteps {
    public CreateAccountResponse createAccount(String userToken) {
        return new CreateAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.entityWasCreated())
                .post()
                .extract().as(CreateAccountResponse.class);
    }
}
