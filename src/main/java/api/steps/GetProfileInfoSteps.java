package api.steps;

import api.models.response.GetCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

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
