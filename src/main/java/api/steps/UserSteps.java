package api.steps;

import api.generators.RandomModelGenerator;
import api.models.Account;
import api.models.Role;
import api.models.requests.CreateUserRequest;
import api.models.requests.DepositAccountRequest;
import api.models.requests.LoginUserRequest;
import api.models.requests.UpdateCustomerProfileRequest;
import api.models.response.CreateAccountResponse;
import api.models.response.DepositAccountResponse;
import api.models.response.GetCustomerProfileResponse;
import api.models.response.UpdateCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.spec.RequestSpecs;
import api.spec.ResponseSpecs;

import java.util.Arrays;
import java.util.List;

public class UserSteps {   private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public  List<CreateAccountResponse> getAllAccounts() {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.ACCOUNTS_INFO,
                ResponseSpecs.requestReturnsOK()).getAll(CreateAccountResponse[].class);
    }


    public static CreateAccountResponse createAccount(String userToken) {
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);
    }

    public static DepositAccountResponse depositAccount(long accountId, double depositAmount, String userToken) {
        DepositAccountRequest depositUserAccount = DepositAccountRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositUserAccount)
                .extract()
                .as(DepositAccountResponse.class);
    }

    public static Account[]  getAccountsInfo(String userToken) {
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.ACCOUNTS_INFO,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(Account[].class);
    }

    public static double getActualAccountBalance(String userToken, long accountId) {
        Account[] accounts = GetAccountsInfoSteps.getAccountsInfo(userToken);

        return Arrays.stream(accounts)
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    public static GetCustomerProfileResponse getProfileInfo(String userToken){
        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.GET_CUSTOMER_INFO,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract().as(GetCustomerProfileResponse.class);
    }

    public static UpdateCustomerProfileResponse updateUserName(String userToken, String newName) {
        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(newName)
                .build();

        return new CrudRequester(
                RequestSpecs.userAuthSpec(userToken),
                Endpoint.UPDATE_CUSTOMER,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest)
                .extract().as(UpdateCustomerProfileResponse.class);
    }

    public static String loginUser(String username, String password) {
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        return new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(loginRequest)
                .extract()
                .header("Authorization");
    }

    public static CreateUserRequest createUser(String username, String password, Role role) {
        CreateUserRequest createUserRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);

        new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.ADMIN_CREATE_USER,
                ResponseSpecs.entityWasCreated())
                .post(createUserRequest);

        return createUserRequest;
    }

    public static String createRandomUserAndGetToken() {
        // 1. Создаем случайного пользователя
        CreateUserRequest createUserRequest = UserCreationSteps.createUser();

        // 2. Логинимся и возвращаем токен
        return UserAuthSteps.loginUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}
