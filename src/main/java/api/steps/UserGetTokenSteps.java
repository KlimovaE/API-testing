package api.steps;

//Фасадный Step для удобного получения токена. Объединяет создание пользователя и логин под капотом

import api.models.requests.CreateUserRequest;

public class UserGetTokenSteps {
    public static String createRandomUserAndGetToken() {
        // 1. Создаем случайного пользователя
        CreateUserRequest createUserRequest = UserCreationSteps.createUser();

        // 2. Логинимся и возвращаем токен
        return UserAuthSteps.loginUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}