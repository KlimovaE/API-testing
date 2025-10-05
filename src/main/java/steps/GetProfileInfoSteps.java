package steps;

import models.response.GetCustomerProfileResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class GetProfileInfoSteps {
    public static GetCustomerProfileResponse getProfileInfo(String userToken){
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.GET_CUSTOMER_INFO,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract().as(GetCustomerProfileResponse.class);
    }
}
