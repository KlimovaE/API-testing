package steps;

import generators.RandomData;
import models.requsts.CreateUserRequest;
import models.Role;
import requests.CreateUser;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UserCreationSteps {
    public CreateUserRequest createUser(String username, String password, Role role) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUserName())
                .password(RandomData.getPassword())
                .role(role.toString()) // Берем из enum
                .build();

        new CreateUser(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityWasCreated())
                .post(createUserRequest);

        return createUserRequest;
    }
}
