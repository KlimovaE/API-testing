package api.steps;

import api.models.requsts.DepositAccountRequest;
import api.models.response.DepositAccountResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

public class DepositSteps {
    public static DepositAccountResponse depositAccount(long accountId, double depositAmount, String userToken) {
        DepositAccountRequest depositUserAccount = DepositAccountRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositUserAccount)
                .extract()
                .as(DepositAccountResponse.class);
    }
}
