package steps;

import models.response.GetCustomerProfileResponse;
import requests.GetCustomerProfileRequest;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class GetProfileInfoSteps {
    public GetCustomerProfileResponse getProfileInfo(String userToken){
        return new GetCustomerProfileRequest(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .get()
                .extract().as(GetCustomerProfileResponse.class);
    }
}
