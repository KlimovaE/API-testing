package steps;

import generators.UsernameGenerator;
import models.CreateUserRequest;
import models.LoginUserRequest;
import requests.AdminLoginUserRequest;
import requests.CreateUser;
import spec.RequestSpecs;
import spec.ResponseSpecs;

public class UserSteps {
        public String createRandomUserAndGetToken() {
            String username = UsernameGenerator.generateValidUsername();
            String password = UsernameGenerator.generateValidPassword();

            // Создаем пользователя
            CreateUserRequest createRequest = CreateUserRequest.builder()
                    .username(username)
                    .password(password)
                    .role("USER")
                    .build();

            new CreateUser(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityWasCreated())
                    .post(createRequest);

            // Логинимся и получаем токен
            LoginUserRequest loginRequest = LoginUserRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            return new AdminLoginUserRequest(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnsOK())
                    .post(loginRequest)
                    .extract()
                    .header("Authorization");
        }

        public String createRandomUserWithName(String name, String userToken) {
            models.UpdateCustomerProfileRequest updateRequest = models.UpdateCustomerProfileRequest.builder()
                    .name(name)
                    .build();

            return new requests.UpdateCustomerProfile(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                    .put(updateRequest)
                    .extract()
                    .jsonPath()
                    .getString("customer.name");
        }
    }


