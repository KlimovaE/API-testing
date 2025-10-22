package api.steps;

import api.generators.RandomModelGenerator;
import api.models.requests.CreateUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

public class UserCreationSteps {
    public static CreateUserRequest createUser() {
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
