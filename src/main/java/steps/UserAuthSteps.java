package steps;

import models.requsts.LoginUserRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UserAuthSteps {
    public String loginUser(String username, String password) {
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        return new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(loginRequest)
                .extract()
                .header("Authorization");
    }
}
