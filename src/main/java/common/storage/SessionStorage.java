package common.storage;

import api.models.requests.CreateUserRequest;
import api.models.response.CreateAccountResponse;
import api.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Хранилище сессионных данных пользователей для тестирования
 */
public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();

    private final Map<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final Map<CreateUserRequest, String> userTokensMap = new LinkedHashMap<>();
    private final Map<CreateUserRequest, CreateAccountResponse> userAccountsMap = new LinkedHashMap<>();

    private SessionStorage() {
    }

    /**
     * Добавляет пользователя БЕЗ счета (только шаги и токен)
     */
    public static void addUser(CreateUserRequest user, String token) {
        INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        INSTANCE.userTokensMap.put(user, token);
        // Не добавляем в userAccountsMap - пользователь без счета
    }

    /**
     * Добавляет пользователя СО счетом (полные данные: шаги, токен, счет)
     */
    public static void addUser(CreateUserRequest user, String token, CreateAccountResponse account) {
        INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        INSTANCE.userTokensMap.put(user, token);
        INSTANCE.userAccountsMap.put(user, account); // Счет всегда должен быть не null
    }

    /**
     * Получает пользователя по номеру (начиная с 1)
     */
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet()).get(number - 1);
    }

    /**
     * Получает первого пользователя
     */
    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    /**
     * Получает токен пользователя по номеру
     */
    public static String getUserToken(int number) {
        CreateUserRequest user = getUser(number);
        return INSTANCE.userTokensMap.get(user);
    }

    /**
     * Получает токен первого пользователя
     */
    public static String getUserToken() {
        return getUserToken(1);
    }

    /**
     * Получает счет пользователя по номеру (может вернуть null если счета нет)
     */
    public static CreateAccountResponse getUserAccount(int number) {
        CreateUserRequest user = getUser(number);
        return INSTANCE.userAccountsMap.get(user);
    }

    /**
     * Получает счет первого пользователя
     */
    public static CreateAccountResponse getUserAccount() {
        return getUserAccount(1);
    }

    /**
     * Получает UserSteps по номеру пользователя
     */
    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.values()).get(number - 1);
    }

    /**
     * Получает UserSteps первого пользователя
     */
    public static UserSteps getSteps() {
        return getSteps(1);
    }

    /**
     * Очищает все хранилища
     */
    public static void clear() {
        INSTANCE.userStepsMap.clear();
        INSTANCE.userTokensMap.clear();
        INSTANCE.userAccountsMap.clear();
    }
}