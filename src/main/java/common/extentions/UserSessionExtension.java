package common.extentions;

import api.models.requests.CreateUserRequest;
import api.models.response.CreateAccountResponse;
import api.steps.AccountCreationSteps;
import api.steps.DepositSteps;
import api.steps.UserAuthSteps;
import api.steps.UserCreationSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.LinkedList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserSession annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            int userCount = annotation.value();
            double[] balances = annotation.balances();
            boolean createAccount = annotation.createAccount();

            SessionStorage.clear();
            List<CreateUserRequest> users = new LinkedList<>();

            for (int i = 0; i < userCount; i++) {
                CreateUserRequest user = UserCreationSteps.createUser();
                String token = UserAuthSteps.loginUser(user.getUsername(), user.getPassword());

                if (createAccount) {
                    // Создаем пользователя СО счетом
                    CreateAccountResponse account = AccountCreationSteps.createAccount(token);

                    // Пополняем счет если указан баланс
                    if (balances.length > i && balances[i] > 0) {
                        DepositSteps.depositAccount(account.getId(), balances[i], token);
                    }

                    // Используем перегрузку со счетом
                    SessionStorage.addUser(user, token, account);
                } else {
                    // Создаем пользователя БЕЗ счета
                    // Используем перегрузку без счета
                    SessionStorage.addUser(user, token);
                }

                users.add(user);
            }

            BasePage.authAsUser(SessionStorage.getUser(annotation.auth()));
        }
    }
}