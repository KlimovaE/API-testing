import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions softly;
    @BeforeEach
    public  void setupTest() {
        this.softly = new SoftAssertions();
    }
}
