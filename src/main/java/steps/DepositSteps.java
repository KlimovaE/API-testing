package steps;

import io.restassured.response.Response;
import models.requsts.DepositAccountRequest;
import requests.DepositAccountRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class DepositSteps {
    public Response depositAccount(long accountId, double depositAmount, String userToken) {
        DepositAccountRequest depositUserAccount = DepositAccountRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();
        return new DepositAccountRequester(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .post(depositUserAccount)
                .extract()
                .response();
    }
}
