package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CreateAccountRequest;

import static io.restassured.RestAssured.given;

public class CreateAccount extends PostRequest<CreateAccountRequest>{
    public CreateAccount(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(CreateAccountRequest model) {
        return
                given()
                        .spec(requestSpecification)
                        .body(model)
                        .post("/api/v1/accounts")
                        .then()
                        .spec(responseSpecification);

    }
    public ValidatableResponse post() {
        return
                given()
                        .spec(requestSpecification)
                        .body("{}")
                        .post("/api/v1/accounts")
                        .then()
                        .spec(responseSpecification);

    }
}
