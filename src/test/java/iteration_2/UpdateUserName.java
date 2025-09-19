package iteration_2;

import models.CreateUserRequest;
import models.LoginUserRequest;
import models.UpdateCustomerProfileRequest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminLoginUserRequest;
import requests.CreateAccount;
import requests.CreateUser;
import requests.UpdateCustomerProfile;
import spec.RequestSpecs;
import spec.ResponseSpecs;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class UpdateUserName {
    String adminToken = "Basic YWRtaW46YWRtaW4=";
    String user1Token;
    String user2Token;
    String user1Password = "Kate012!";
    String user2Password = "Kate013!";
    //Генерация уникальных userName для каждого теста
    String user1Username = "A_" + System.currentTimeMillis(); // "kate001_123456"
    String user2Username = "B_" + System.currentTimeMillis(); // "kate002_123456"
    String userRole = "USER";

    //Метод по созданию аккаунта
    public long createAccount(String userToken) {
        return new CreateAccount(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.entityWasCreated())
                .post()
                .extract()
                .jsonPath()
                .getLong("id");
    }

    //Метод по созданию пользователя
    private void createUser(String userName, String userPassword, String role) {
        CreateUserRequest createUser = CreateUserRequest.builder()
                .username(userName)
                .password(userPassword)
                .role(role)
                .build();
        new CreateUser(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityWasCreated())
                .post(createUser);
    }

    //Метод по получению токена
    private String getToken(String userName, String userPassword) {
        LoginUserRequest loginUser1 = LoginUserRequest.builder()
                .username(userName)
                .password(userPassword)
                .build();
        return new AdminLoginUserRequest(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnsOK())
                .post(loginUser1)
                .extract()
                .header("Authorization");
    }

    //Метод для успешного изменения имени
    private String successfulUpdateName(String newName, String userToken) {
        UpdateCustomerProfileRequest updateUser1 = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();
        return new UpdateCustomerProfile(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsOK())
                .put(updateUser1)
                .extract()
                .jsonPath()
                .getString("customer.name");
    }

    //Метод для неуспешного изменения имени
    private int unsuccessfulUpdateName(String newName, String userToken) {
        UpdateCustomerProfileRequest updateUser1 = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();
        return new UpdateCustomerProfile(RequestSpecs.userAuthSpec(userToken), ResponseSpecs.requestReturnsBadRequest())
                .put(updateUser1)
                .extract()
                .statusCode();
    }


    @BeforeEach
    public void setupTestData() {
        //создание первого пользователя
        createUser(user1Username, user1Password, userRole);
        //Создание второго пользователя
        createUser(user2Username, user2Password, userRole);

        //Получение токена для пользователя1
        user1Token = getToken(user1Username, user1Password);
        //Получение токена для пользователя2
        user2Token = getToken(user2Username, user2Password);
    }

    public static Stream<Arguments> validNameData() {
        return Stream.of(
                //Update name from null (null->kate)
                Arguments.of(null, "Kate"),
                //Update user's name to another valid name (kate->kat
                Arguments.of("Kate", "Kat"),
                //Update name to duplicate name(kat->kat)
                Arguments.of("Kat", "Kat"),
                //Update name - use all type symbols(kat->Kate 1234567890:%;№"!?*()+=,/\'<>.-_)
                Arguments.of("Kat", "Kate 1234567890:%;№!?*()+=,/'<>.-_")
        );
    }

    public static Stream<Arguments> nameDataForCornerCases() {
        return Stream.of(
                //only special symbols
                Arguments.of("-:%;№!?*()+=,/\"'<>.-_"),
                //only numbers
                Arguments.of("1234567890")
        );
    }

    public static Stream<Arguments> nameDataForNegativeCases() {
        return Stream.of(
                //only special symbols
                Arguments.of(""),
                //only numbers
                Arguments.of("   ")
        );
    }

    @ParameterizedTest
    @MethodSource("validNameData")
    @DisplayName("Пользователь может изменить имя с null и с другого значения")
    public void userCanUpdateNameTest(String initialName, String newName) {
        // Если initialName не null, сначала устанавливаем его
        if (initialName != null) {
            successfulUpdateName(initialName, user1Token);
        }

        // Теперь меняем имя на новое
        String actualName = successfulUpdateName(newName, user1Token);
        assertEquals(newName, actualName);
    }

    @Test
    @DisplayName("Пользователь может изменить себе имя на имя у другого пользователя")
    public void userCanUpdateNameToNameAnotherUserTest() {
        String duplicateName = "UserKate";
        // Задаем имя первому пользователю
        successfulUpdateName(duplicateName, user1Token);
        // Задаем имя первого пользователя второму
        successfulUpdateName(duplicateName, user2Token);

        String actualName = successfulUpdateName(duplicateName, user2Token);
        assertEquals(duplicateName, actualName);
    }

    @ParameterizedTest
    @MethodSource("nameDataForCornerCases")
    @DisplayName("Пользователь может изменить имя на значение только из символов или чисел")
    public void useOnlySpecialSymbolsOrNumbersForNameTest(String newName) {
        String actualName = successfulUpdateName(newName, user1Token);
        assertEquals(newName, actualName);
    }

    @ParameterizedTest
    @MethodSource("nameDataForNegativeCases")
    @DisplayName("Пользователь не может изменить имя на невалидное значение(пустое, только пробелы)")
    public void userCannotUpdateNameWithInvalidValue(String newName) {
        int statusCodeResponse = unsuccessfulUpdateName(newName, user1Token);
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCodeResponse);
    }
}