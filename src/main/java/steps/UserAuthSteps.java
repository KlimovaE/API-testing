package steps;

import models.requsts.LoginUserRequest;
import requests.AdminLoginUserRequest;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UserAuthSteps {
    public String loginUser(String username, String password) {
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        return new AdminLoginUserRequest(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnsOK())
                .post(loginRequest)
                .extract()
                .header("Authorization");
    }
}
