package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.given;

public class CreateAccount {
    private final RequestSpecification requestSpecification;
    private final ResponseSpecification responseSpecification;

    public CreateAccount(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        this.requestSpecification = requestSpec;
        this.responseSpecification = responseSpec;
    }

    public ValidatableResponse post() {
        return given()
                .spec(requestSpecification)
                .post("/api/v1/accounts") // ← Без .body()
                .then()
                .spec(responseSpecification);
    }
}