package api_tests.exchange_rates;

import api_tests.RateData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.restassured.RestAssured.given;
import static object_mappers.RateDataJsonToObjectMapper.getRateDataFromJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TimeSeriesEndpointStepDefinitions {
    private static Response response;
    private static RequestSpecBuilder requestSpecBuilder;
    private static final int MAX_SYMBOLS_COUNT = 170;
    private static final String API_KEY = System.getenv("apikey");

    @Given("the valid endpoint to GET timeseries")
    public void theValidEndpointToGETTimeseries() {
        RestAssured.basePath = "/timeseries";
        requestSpecBuilder = new RequestSpecBuilder();
    }

    @Given("I am authenticated user")
    public void iAmAuthenticatedUser() {
        requestSpecBuilder
                .addHeader("apikey", API_KEY);
    }

    @Given("I am not authenticated user")
    public void iAmNotAuthenticated() {}

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
        assertThat(new JsonPath(response.asString()).get("error.code"), equalTo(expectedErrorCode));
    }

    @When("I GET timeseries with params")
    public void iGETTimeseriesWithParams(List<TimeseriesParams> params) {
        TimeseriesParams timeseriesParams = params.get(0);

        RequestSpecification requestSpecification = requestSpecBuilder
                .addParam("start_date", timeseriesParams.getStartDate())
                .addParam("end_date", timeseriesParams.getEndDate())
                .addParam("base", timeseriesParams.getBase())
                .addParam("symbols", getSymbolsParam(timeseriesParams))
                .build();

        response = given(requestSpecification).when().get();
    }

    @Then("I get valid response")
    public void iGetValidResponse(List<TimeseriesParams> params) {
        TimeseriesParams sent = params.get(0);
        RateData rateData = getRateDataFromJson(response.getBody().asString());

        assertThat(response.statusCode(), equalTo(200));
        assertThat(rateData, is(notNullValue()));
        assertThat(rateData.isSuccess(), equalTo(true));
        assertThat(rateData.isTimeseries(), equalTo(true));
        assertThat(rateData.getBase(), equalTo(sent.getBase()));
        assertThat(rateData.getStartDate(), equalTo(sent.getStartDate()));
        assertThat(rateData.getEndDate(), equalTo(sent.getEndDate()));

        assertThat((long) rateData.getRates().size(),
                equalTo(getCountOfDaysForDates(rateData.getStartDate(), rateData.getEndDate())));

        if (sent.getSymbols().size() == 0) {
            assertThat(rateData.getRates().get(rateData.getStartDate()).size(),
                    equalTo(MAX_SYMBOLS_COUNT));
        } else {
            assertThat(rateData.getRates().get(rateData.getStartDate()).size(),
                    equalTo(sent.getSymbols().size()));
        }
    }

    private static String getSymbolsParam(TimeseriesParams timeseriesParams) {
        return timeseriesParams.getSymbols().toString().replaceAll("^\\[|]$", "");
    }

    private static long getCountOfDaysForDates(String startDate, String endDate) {
        LocalDate startDay = LocalDate.parse(startDate);
        LocalDate endDay = LocalDate.parse(endDate);

        return ChronoUnit.DAYS.between(startDay, endDay) + 1;
    }
}
