package steps;

//Фасадный Step для удобного получения токена. Объединяет создание пользователя и логин под капотом

import generators.RandomData;
import models.requsts.CreateUserRequest;
import models.Role;

public class UserGetTokenSteps {
    private final UserCreationSteps creationSteps = new UserCreationSteps();
    private final UserAuthSteps authSteps = new UserAuthSteps();

    public String createRandomUserAndGetToken() {
        // 1. Создаем случайного пользователя
        CreateUserRequest createUserRequest = creationSteps.createUser(RandomData.getUserName(), RandomData.getPassword(), Role.USER);

        // 2. Логинимся и возвращаем токен
        return authSteps.loginUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}