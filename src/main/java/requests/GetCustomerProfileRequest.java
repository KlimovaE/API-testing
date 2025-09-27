package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.response.GetCustomerProfileResponse;

import static io.restassured.RestAssured.given;

public class GetCustomerProfileRequest extends GetRequest<GetCustomerProfileResponse> {
    public GetCustomerProfileRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return
                given()
                        .spec(requestSpecification)
                        .get("/api/v1/customer/accounts")
                        .then()
                        .spec(responseSpecification);
    }
}
