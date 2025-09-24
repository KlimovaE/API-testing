package iteration_2;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.ProfileSteps;
import steps.UserSteps;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateUserNameTest {

    private final UserSteps userSteps = new UserSteps();
    private final ProfileSteps profileSteps = new ProfileSteps();

    public static Stream<Arguments> validNameData() {
        return Stream.of(
                Arguments.of(null, "Kate"),
                Arguments.of("Kate", "Kat"),
                Arguments.of("Kat", "Kat"),
                Arguments.of("Kat", "Kate 1234567890:%;№!?*()+=,/'<>.-_")
        );
    }

    public static Stream<Arguments> nameDataForCornerCases() {
        return Stream.of(
                Arguments.of("-:%;№!?*()+=,/\"'<>.-_"),
                Arguments.of("1234567890")
        );
    }

    public static Stream<Arguments> nameDataForNegativeCases() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("   ")
        );
    }

    @ParameterizedTest
    @MethodSource("validNameData")
    @DisplayName("Пользователь может изменить имя с null и с другого значения")
    public void userCanUpdateNameTest(String initialName, String newName) {
        // Создаем пользователя и получаем токен
        String userToken = userSteps.createRandomUserAndGetToken();

        // Если initialName не null, сначала устанавливаем его
        if (initialName != null) {
            profileSteps.updateUserName(userToken, initialName);
        }

        // Меняем имя на новое
        String actualName = profileSteps.updateUserName(userToken, newName);
        assertEquals(newName, actualName);
    }

    @ParameterizedTest
    @DisplayName("Пользователь может изменить себе имя на имя у другого пользователя")
    @MethodSource("validNameData")
    public void userCanUpdateNameToNameAnotherUserTest(String initialName, String duplicateName) {
        // Создаем первого пользователя и устанавливаем ему имя
        String user1Token = userSteps.createRandomUserAndGetToken();
        if (initialName != null) {
            profileSteps.updateUserName(user1Token, initialName);
        }
        profileSteps.updateUserName(user1Token, duplicateName);

        // Создаем второго пользователя и даем ему такое же имя
        String user2Token = userSteps.createRandomUserAndGetToken();
        String actualName = profileSteps.updateUserName(user2Token, duplicateName);

        assertEquals(duplicateName, actualName);
    }

    @ParameterizedTest
    @MethodSource("nameDataForCornerCases")
    @DisplayName("Пользователь может изменить имя на значение только из символов или чисел")
    public void useOnlySpecialSymbolsOrNumbersForNameTest(String newName) {
        String userToken = userSteps.createRandomUserAndGetToken();
        String actualName = profileSteps.updateUserName(userToken, newName);
        assertEquals(newName, actualName);
    }

    @ParameterizedTest
    @MethodSource("nameDataForNegativeCases")
    @DisplayName("Пользователь не может изменить имя на невалидное значение(пустое, только пробелы)")
    public void userCannotUpdateNameWithInvalidValue(String invalidName) {
        String userToken = userSteps.createRandomUserAndGetToken();
        int statusCode = profileSteps.tryUpdateUserNameWithBadRequest(userToken, invalidName);
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
    }
}