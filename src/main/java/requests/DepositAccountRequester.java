package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.requsts.DepositAccountRequest;

import static io.restassured.RestAssured.given;

public class DepositAccountRequester extends PostRequest<DepositAccountRequest> {
    public DepositAccountRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(DepositAccountRequest model) {
    return given()
            .spec(requestSpecification)
            .body(model)
            .post("/api/v1/accounts/deposit")
            .then()
            .assertThat()
            .spec(responseSpecification);
}
}
