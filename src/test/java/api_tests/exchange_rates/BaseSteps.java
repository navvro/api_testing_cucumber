package api_tests.exchange_rates;

import cucumber.ScenarioContext;
import cucumber.TestContext;
import lombok.Getter;

@Getter
public class BaseSteps {
    private final ScenarioContext scenarioContext;

    public BaseSteps(TestContext testContext) {
        this.scenarioContext = testContext.getScenarioContext();
    }
}
