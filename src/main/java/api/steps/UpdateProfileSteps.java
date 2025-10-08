package api.steps;

import api.models.requsts.UpdateCustomerProfileRequest;
import api.models.response.UpdateCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

public class UpdateProfileSteps {

    public static UpdateCustomerProfileResponse updateUserName(String userToken, String newName) {
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
