package api_tests.exchange_rates;

import api_tests.RateData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.DateUtils;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TimeSeriesEndpointStepDefinitions {
    private static final Dotenv dotenv = Dotenv.load();
    private static final int MAX_SYMBOLS_COUNT = Integer.parseInt(dotenv.get("MAX_SYMBOLS_COUNT"));
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String ENDPOINT_PATH = dotenv.get("TIMESERIES_ENDPOINT_PATH");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Response response;
    private static Response secondResponse;
    private static RequestSpecBuilder requestSpecBuilder;

    private static RequestSpecification getRequestSpecification(TimeseriesParams params) {

        return requestSpecBuilder
                .addParam("start_date", params.getStartDate())
                .addParam("end_date", params.getEndDate())
                .addParam("base", params.getBase())
                .addParam("symbols", getSymbolsParam(params))
                .build();
    }

    private static String getSymbolsParam(TimeseriesParams timeseriesParams) {
        return timeseriesParams.getSymbols().toString().replaceAll("^\\[|]$", "");
    }

    @Given("the valid endpoint to GET timeseries")
    public void theValidEndpointToGETTimeseries() {
        RestAssured.basePath = ENDPOINT_PATH;
        requestSpecBuilder = new RequestSpecBuilder();
    }

    @Given("I am authenticated user")
    public void iAmAuthenticatedUser() {
        requestSpecBuilder
                .addHeader("apikey", API_KEY);
    }

    @Given("I am not authenticated user")
    public void iAmNotAuthenticated() {
    }

    @Given("I use {string} to authenticate")
    public void iUseToAuthenticate(String tokenValue) {
        requestSpecBuilder
                .addHeader("apikey", tokenValue);
    }

    @Then("API responds with {int} status code")
    public void iGetStatusCode(int expectedStatusCode) {
        assertThat(response.statusCode(), equalTo(expectedStatusCode));
    }

    @And("Error code is {string}")
    public void errorCodeIs(String expectedErrorCode) {
        String errorCode = new JsonPath(response.asString()).get("error.code");

        assertThat(errorCode, equalTo(expectedErrorCode));
    }

    @When("I make GET request to timeseries with params")
    public void iGETTimeseriesWithParams(List<TimeseriesParams> paramsList) {
        RequestSpecification requestSpecification = getRequestSpecification(paramsList.get(0));

        response = given(requestSpecification).when().get();
    }

    @When("I make GET request two times with params")
    public void iMakeGETRequestTwoTimesWithParams(List<TimeseriesParams> paramsList) {
        RequestSpecification requestSpecification = getRequestSpecification(paramsList.get(0));

        response = given(requestSpecification).when().get();
        secondResponse = given(requestSpecification).when().get();
    }

    @Then("Responses are the same")
    public void responsesAreTheSame() {
        assertThat(secondResponse.getBody().asString(), equalTo(response.getBody().asString()));
    }

    @Then("I get valid response")
    public void iGetValidResponse(List<TimeseriesParams> params) throws JsonProcessingException {
        TimeseriesParams sent = params.get(0);
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
        if (sent.getSymbols().size() == 0) {
            assertThat(responseSymbolsCount, equalTo(MAX_SYMBOLS_COUNT));
        } else {
            assertThat(responseSymbolsCount, equalTo(sent.getSymbols().size()));
        }
    }
}
