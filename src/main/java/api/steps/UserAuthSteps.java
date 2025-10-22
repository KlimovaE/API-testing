package api.steps;

import api.models.requests.LoginUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

public class UserAuthSteps {
    public static String loginUser(String username, String password) {
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
