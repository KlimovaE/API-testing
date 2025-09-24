package steps;

import models.UpdateCustomerProfileRequest;
import requests.UpdateCustomerProfile;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class ProfileSteps {

    public String updateUserName(String userToken, String newName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();

        return new UpdateCustomerProfile(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .put(updateRequest)
                .extract()
                .jsonPath()
                .getString("customer.name");
    }

    public int tryUpdateUserNameWithBadRequest(String userToken, String invalidName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(invalidName)
                .build();

        return new UpdateCustomerProfile(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsBadRequest())
                .put(updateRequest)
                .extract()
                .statusCode();
    }
}
