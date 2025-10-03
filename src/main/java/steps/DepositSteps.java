package steps;

import models.requsts.DepositAccountRequest;
import models.response.DepositAccountResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class DepositSteps {
    public DepositAccountResponse depositAccount(long accountId, double depositAmount, String userToken) {
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
