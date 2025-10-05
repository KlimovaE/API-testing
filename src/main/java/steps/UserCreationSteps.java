package steps;

import generators.RandomModelGenerator;
import models.Role;
import models.requsts.CreateUserRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UserCreationSteps {
    public static CreateUserRequest createUser(String username, String password, Role role) {
        CreateUserRequest createUserRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);

        new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.entityWasCreated())
                .post(createUserRequest);

        return createUserRequest;
    }
}
