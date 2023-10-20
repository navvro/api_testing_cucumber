package api_tests.exchange_rates;

import cucumber.ScenarioContext;
import cucumber.TestContext;
import io.cucumber.java.en.Given;

public class CommonStepDefinitions {
    private final ScenarioContext scenarioContext;

    public CommonStepDefinitions(TestContext testContext) {
        this.scenarioContext = testContext.getScenarioContext();
    }

    @Given("I am authenticated user")
    public void iAmAuthenticatedUser() {
        String apiKey = (String) scenarioContext.getContext("API_KEY");

        scenarioContext.getRequestSpecBuilder()
                .addHeader("apikey", apiKey);
    }


    @Given("I am not authenticated user")
    public void iAmNotAuthenticated() {
    }

    @Given("I use {string} to authenticate")
    public void iUseToAuthenticate(String tokenValue) {
        scenarioContext.getRequestSpecBuilder()
                .addHeader("apikey", tokenValue);
    }
}
