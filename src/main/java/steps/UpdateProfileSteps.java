package steps;

import models.requsts.UpdateCustomerProfileRequest;
import models.response.UpdateCustomerProfileResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UpdateProfileSteps {

    public UpdateCustomerProfileResponse updateUserName(String userToken, String newName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();

        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest)
                .extract().as(UpdateCustomerProfileResponse.class);
    }
}
