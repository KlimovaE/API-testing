package steps;

//Фасадный Step для удобного получения токена. Объединяет создание пользователя и логин под капотом

import generators.RandomData;
import models.Role;
import models.requsts.CreateUserRequest;

public class UserGetTokenSteps {
    public static String createRandomUserAndGetToken() {
        // 1. Создаем случайного пользователя
        CreateUserRequest createUserRequest = UserCreationSteps.createUser(RandomData.getUserName(), RandomData.getPassword(), Role.USER);

        // 2. Логинимся и возвращаем токен
        return UserAuthSteps.loginUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}