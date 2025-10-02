package iteration_2;

import models.requsts.UpdateCustomerProfileRequest;
import models.response.UpdateCustomerProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import spec.RequestSpecs;
import spec.ResponseSpecs;
import steps.UpdateProfileSteps;
import steps.UserGetTokenSteps;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class UpdateUserName {
    private final UserGetTokenSteps userGetTokenSteps = new UserGetTokenSteps();
    private final UpdateProfileSteps updateProfileSteps = new UpdateProfileSteps();
    private String user1Token;
    private final String NEW_NAME = "Kate";

    @BeforeEach
    public void setUp() {
        // Создаем пользователя перед каждым тестом
        user1Token = userGetTokenSteps.createRandomUserAndGetToken();
    }

    public static Stream<Arguments> validNameData() {
        return Stream.of(
                //Изменение Имени с null со всеми цифрами и символами (null->Kate 1234567890:%;№!?*()+=,/'<>.-_)
                Arguments.of("Kat", "Kate 1234567890:%;№!?*()+=,/'<>.-_"),
                //Изменения имени на другое валидное имя(kate->kat)
                Arguments.of("Kate", "Kat"),
                //Изменение на тоже самое имя(Kat->Kat)
                Arguments.of("Kat", "Kat")
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

    @Test
    public void userCanUpdateNameFromNullTest() {
        // Меняем имя на новое
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(NEW_NAME)
                .build();

         UpdateCustomerProfileResponse response = (UpdateCustomerProfileResponse) new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest);

        String actualName = response.getCustomer().getName();

        //Проверяем что у имени теперь новое значение
        assertEquals(NEW_NAME, actualName);
    }

    @ParameterizedTest
    @MethodSource("validNameData")
    @DisplayName("Пользователь может изменить имя с null и с другого значения")
    public void userCanUpdateNameToAnotherValidNameTest(String initialName, String newName) {
        //Устанавливаем изначальное имя
        updateProfileSteps.updateUserName(user1Token, initialName);

        // Меняем имя на новое
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();

        UpdateCustomerProfileResponse response = (UpdateCustomerProfileResponse) new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest);

        String actualName = response.getCustomer().getName();
        //Проверяем что у имени теперь новое значение
        assertEquals(newName, actualName);
    }

    @Test
    @DisplayName("Пользователь может изменить имя на имя другого пользователя")
    public void userCanUpdateNameToNameAnotherUserTest() {
        //Устанавливаем имя первому пользователю
        updateProfileSteps.updateUserName(user1Token, NEW_NAME);

        // Создаем второго пользователя и даем ему такое же имя
        String user2Token = userGetTokenSteps.createRandomUserAndGetToken();

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(NEW_NAME)
                .build();

        UpdateCustomerProfileResponse response = (UpdateCustomerProfileResponse) new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest);

        String actualName = response.getCustomer().getName();

        //Проверяем что у имени теперь новое значение
        assertEquals(NEW_NAME, actualName);
    }


    @ParameterizedTest
    @MethodSource("nameDataForCornerCases")
    @DisplayName("Пользователь может изменить имя на значение только из символов или чисел")
    public void useOnlySpecialSymbolsOrNumbersForNameTest(String newName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();

        UpdateCustomerProfileResponse response = (UpdateCustomerProfileResponse) new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest);

        String actualName = response.getCustomer().getName();

        //Проверяем что у имени теперь новое значение
        assertEquals(newName, actualName);
    }

    @ParameterizedTest
    @MethodSource("nameDataForNegativeCases")
    @DisplayName("Пользователь не может изменить имя на невалидное значение(пустое, только пробелы)")
    public void userCannotUpdateNameWithInvalidValue(String invalidName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(invalidName)
                .build();

        UpdateCustomerProfileResponse response = (UpdateCustomerProfileResponse) new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsBadRequest())
                .put(updateRequest);

        String actualName = response.getCustomer().getName();
        //Проверяем, что имя не изменилось
        assertNull(actualName, "Дефолтное значение поля name не изменилось и осталось null");
    }
}