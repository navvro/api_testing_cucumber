package api_tests.exchange_rates;

import api_tests.RateData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.ScenarioContext;
import cucumber.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.DateUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AssertSteps {
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_SYMBOLS_COUNT = 170;

    public AssertSteps(TestContext testContext) {
        this.scenarioContext = testContext.getScenarioContext();
    }

    @Then("API responds with {int} status code")
    public void iGetStatusCode(int expectedStatusCode) {
        Response response = (Response) scenarioContext.getContext("RESPONSE");

        assertThat(response.statusCode(), equalTo(expectedStatusCode));
    }

    @And("Error code is {string}")
    public void errorCodeIs(String expectedErrorCode) {
        Response response = (Response) scenarioContext.getContext("RESPONSE");

        String errorCode = new JsonPath(response.asString()).get("error.code");

        assertThat(errorCode, equalTo(expectedErrorCode));
    }

    @Then("I get valid response")
    public void iGetValidResponse(List<TimeseriesParams> params) throws JsonProcessingException {
        TimeseriesParams sent = params.get(0);
        Response response = (Response) scenarioContext.getContext("RESPONSE");
        RateData rateData = objectMapper.readValue(response.getBody().asString(), RateData.class);
        assertThat(rateData, is(notNullValue()));

        /*
         Verifying if data in json is properly built
         in base of sent params.
         */
        assertThat(rateData.isSuccess(), equalTo(true));
        assertThat(rateData.isTimeseries(), equalTo(true));
        assertThat(rateData.getBase(), equalTo(sent.getBase()));
        assertThat(rateData.getStartDate(), equalTo(sent.getStartDate()));
        assertThat(rateData.getEndDate(), equalTo(sent.getEndDate()));

        long daysCountBetweenDates = DateUtils.getCountOfDaysForDates(sent.getStartDate(), sent.getEndDate());
        int responseSymbolsCount = rateData.getRates().get(rateData.getStartDate()).size();

        /*
         check if array of rates has as many items as number of days between dates
         */
        assertThat((long) rateData.getRates().size(), equalTo(daysCountBetweenDates));

        /* check if count of symbols is equal to sent list of them
        if there are not sent any, full list is received
         */
        if (sent.getSymbols().isEmpty()) {
            assertThat(responseSymbolsCount, equalTo(MAX_SYMBOLS_COUNT));
        } else {
            assertThat(responseSymbolsCount, equalTo(sent.getSymbols().size()));
        }
    }

    @Then("Responses are the same")
    public void responsesAreTheSame() {
        Response response = (Response) scenarioContext.getContext("RESPONSE");
        Response secondResponse = (Response) scenarioContext.getContext("SECOND_RESPONSE");

        assertThat(secondResponse.getBody().asString(), equalTo(response.getBody().asString()));
    }
}
