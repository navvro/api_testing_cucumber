package api_tests.exchange_rates;

import cucumber.TestContext;
import io.cucumber.java.en.Given;

public class CommonStepDefinitions extends BaseSteps{

    public CommonStepDefinitions(TestContext testContext) {
        super(testContext);
    }

    @Given("I am authenticated user")
    public void iAmAuthenticatedUser() {
        String apiKey = (String) getScenarioContext().getContext("API_KEY");

        getScenarioContext().getRequestSpecBuilder()
                .addHeader("apikey", apiKey);
    }


    @Given("I am not authenticated user")
    public void iAmNotAuthenticated() {
    }

    @Given("I use {string} to authenticate")
    public void iUseToAuthenticate(String tokenValue) {
        getScenarioContext().getRequestSpecBuilder()
                .addHeader("apikey", tokenValue);
    }
}
