package api.steps;

import api.models.response.CreateAccountResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

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
