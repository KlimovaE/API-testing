package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class UpdateUserName {
    String adminToken;
    String user1Token;
    String user2Token;
    //Генерация уникальных userName для каждого теста
    String user1Username = "A_" + System.currentTimeMillis(); // "kate001_123456"
    String user2Username = "B_" + System.currentTimeMillis(); // "kate002_123456"

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));

    }
    @BeforeEach
    public void setupTestData() {
        //Получение токена для админа
        adminToken = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "admin",
                          "password": "admin"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");
        //Создание первого пользователя
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", adminToken)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate012!",
                          "role": "USER"
                        }
                        """, user1Username))
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
        //Создание второго пользователя
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", adminToken)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate013!",
                          "role": "USER"
                        }
                        """, user2Username))
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
        //Получение токена для пользователя1
        user1Token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate012!"
                        }
                        """, user1Username))
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");
        //Получение токена для пользователя2
        user2Token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "Kate013!"
                        }
                        """, user2Username))
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");
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
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", user1Token)
                    .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, initialName))
                    .put("http://localhost:4111/api/v1/customer/profile")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("customer.name", equalTo(initialName));

        }

        // Теперь меняем имя на новое
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, newName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", equalTo(newName));
    }
    @Test
    @DisplayName("Пользователь может изменить себе имя на имя у другого пользователя")
    public void userCanUpdateNameToNameAnotherUserTest() {
    String duplicateName = "UserKate";
        // Задаем имя первому пользователю
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, duplicateName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", equalTo(duplicateName));

        // Задаем имя первого пользователя второму
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user2Token)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, duplicateName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", equalTo(duplicateName));
    }

    @ParameterizedTest
    @MethodSource("nameDataForCornerCases")
    @DisplayName("Пользователь может изменить имя на значение только из символов или чисел")
    public void useOnlySpecialSymbolsOrNumbersForNameTest(String newName) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user1Token)
        //Вручную: JSON валиден, кавычки правильно экранированы
        //В тесте: String.format() может некорректно обработать \" и ' в строке
                .body(String.format("""
                        {
                        "name":"%s"
                        }
                        """, newName.replace("\"", "\\\"")))// ← Экранируем кавычки!
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", equalTo(newName));

    }
    @ParameterizedTest
    @MethodSource("nameDataForNegativeCases")
    @DisplayName("Пользователь не может изменить имя на невалидное значение(пустое, только пробелы)")
    public void userCannotUpdateNameWithInvalidValue(String newName) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user1Token)
                .body(String.format("""
                        {
                        "name":"%s"
                        }
                        """, newName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

}
