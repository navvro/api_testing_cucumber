package cucumber;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Getter
public class TestContext {
    private static Dotenv dotenv;
    private final ScenarioContext scenarioContext;

    public TestContext() {
        scenarioContext = new ScenarioContext();
        dotenv = Dotenv.load();
        scenarioContext.setContext("API_KEY", dotenv.get("API_KEY"));
    }
}
