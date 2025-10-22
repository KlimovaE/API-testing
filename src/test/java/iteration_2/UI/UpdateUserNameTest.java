package iteration_2.UI;

import api.generators.RandomData;
import api.models.requests.CreateUserRequest;
import api.steps.GetProfileInfoSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DashboardPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UpdateUserNameTest extends BaseUiTest {
    private final String NEW_NAME = RandomData.getRandomValidName();
    private final String INVALID_NAME = NEW_NAME + NEW_NAME;
    CreateUserRequest user;

    public void setUserData() {
        // Сначала авторизуем пользователя для UI
        user = SessionStorage.getUser();
        authAsUser(user);
    }
    @Test
    @UserSession(createAccount = false)
    @DisplayName("Успешное изменение имени пользователя")
    public void userCanUpdateNameTest() {
        setUserData();
        // Пользовательский сценарий: цепочка последовательных вызовов(переходов)
        new DashboardPage()
                .open()                  // Пользователь открывает дашборд
                .goToChangeName()        // Кликает на свое имя
                .verifyPanelVisible()    // Видит страницу редактирования
                .editName(NEW_NAME)     // Меняет имя
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_CHANGE_NAME.getMessage());//проверка сообщения алерта

        String actualName = GetProfileInfoSteps.getProfileInfo(getCurrentUserToken()).getName();
        assertEquals(NEW_NAME, actualName, "Значение имени пользователя должно было измениться на новое");
    }

    @Test
    @UserSession(createAccount = false)
    @DisplayName("Неспешное изменение имени пользователя")
    public void userCanNotUpdateNameTest() {
        setUserData();
        new DashboardPage().open().goToChangeName().verifyPanelVisible().editName(NEW_NAME + NEW_NAME)
                .checkAlertMessageAndAccept(BankAlert.UNSUCCESSFULLY_CHANGE_NAME.getMessage());

        String actualName = GetProfileInfoSteps.getProfileInfo(getCurrentUserToken()).getName();
        assertNotEquals(INVALID_NAME, actualName, "Значение имени пользователя не должно было измениться на новое");
    }
}
