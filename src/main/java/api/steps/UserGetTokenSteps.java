package api.steps;

//Фасадный Step для удобного получения токена. Объединяет создание пользователя и логин под капотом

import api.generators.RandomData;
import api.models.Role;
import api.models.requsts.CreateUserRequest;

public class UserGetTokenSteps {
    public static String createRandomUserAndGetToken() {
        // 1. Создаем случайного пользователя
        CreateUserRequest createUserRequest = UserCreationSteps.createUser(RandomData.getUserName(), RandomData.getPassword(), Role.USER);

        // 2. Логинимся и возвращаем токен
        return UserAuthSteps.loginUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}