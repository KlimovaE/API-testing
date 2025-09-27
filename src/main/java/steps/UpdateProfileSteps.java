package steps;

import models.requsts.UpdateCustomerProfileRequest;
import models.response.UpdateCustomerProfileResponse;
import requests.UpdateCustomerProfile;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UpdateProfileSteps {

    public UpdateCustomerProfileResponse updateUserName(String userToken, String newName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();

        return new UpdateCustomerProfile(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .put(updateRequest)
                .extract().as(UpdateCustomerProfileResponse.class);
    }
}
