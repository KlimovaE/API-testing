package api.requests.skelethon;

import api.models.requsts.*;
import api.models.response.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import api.models.BaseModel;
import api.models.requsts.*;
import api.models.response.*;

@AllArgsConstructor
@Getter

public enum Endpoint {

    ADMIN_CREATE_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
            ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    UPDATE_CUSTOMER(
            "/customer/profile",
            UpdateCustomerProfileRequest.class,
            UpdateCustomerProfileResponse.class
    ),

    GET_CUSTOMER_INFO(
            "/customer/profile",
            UpdateCustomerProfileRequest.class,
            UpdateCustomerProfileResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            DepositAccountRequest.class,
            DepositAccountResponse.class

    ),

    TRANSFER(
            "/accounts/transfer",
            TransferAccountRequest.class,
            TransferAccountResponse.class

    ),

    ACCOUNTS_INFO(
            "/customer/accounts",
            GetCustomerProfileRequest.class,
            GetCustomerProfileResponse.class
    );

    private final String url;
    private final Class <? extends BaseModel> requestModel;
    private final Class <? extends BaseModel> responseModel;

}
