package iteration_2.API;

import api.generators.RandomData;
import api.models.requsts.UpdateCustomerProfileRequest;
import api.models.response.UpdateCustomerProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;
import api.steps.GetProfileInfoSteps;
import api.steps.UpdateProfileSteps;
import api.steps.UserGetTokenSteps;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class UpdateUserNameTest {
    private String user1Token;
    private final static String NEW_NAME = RandomData.getUserName();

    @BeforeEach
    public void setUp() {
        // Создаем пользователя перед каждым тестом
        user1Token = UserGetTokenSteps.createRandomUserAndGetToken();
    }

    public static Stream<Arguments> validNameData() {
        return Stream.of(
                //Изменения имени на другое валидное имя
                Arguments.of(RandomData.getUserName(), RandomData.getUserName()),
                //Изменение на то же самое имя
                Arguments.of(NEW_NAME, NEW_NAME)
        );
    }

    public static Stream<Arguments> nameDataForNegativeCases() {
        return Stream.of(
                //only special symbols
                Arguments.of(""),
                //only numbers
                Arguments.of("   "),
                //more than 2 words
                Arguments.of(RandomData.getUserName() + RandomData.getUserName()),
                Arguments.of(RandomData.getUserName() + 1)
        );
    }
    @DisplayName("Пользователь может изменить имя с null")
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
    @DisplayName("Пользователь может изменить имя с другого значения")
    public void userCanUpdateNameToAnotherValidNameTest(String initialName, String newName) {
        //Устанавливаем изначальное имя
        UpdateProfileSteps.updateUserName(user1Token, initialName);

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
        UpdateProfileSteps.updateUserName(user1Token, NEW_NAME);

        // Создаем второго пользователя и даем ему такое же имя
        String user2Token = UserGetTokenSteps.createRandomUserAndGetToken();

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
    @MethodSource("nameDataForNegativeCases")
    @DisplayName("Пользователь не может изменить имя на невалидное значение")
    public void userCannotUpdateNameWithInvalidValue(String invalidName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(invalidName)
                .build();

        new CrudRequester(
                RequestSpecs.userAuthSpec(user1Token),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsBadRequest())
                .put(updateRequest);

        //Проверяем, что имя не изменилось
        assertNull(GetProfileInfoSteps.getProfileInfo(user1Token).getName(), "Дефолтное значение поля name не изменилось и осталось null");
    }
}