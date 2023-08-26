package api_tests.exchange_rates;

import api_tests.RateData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;
import static object_mappers.RateDataJsonToObjectMapper.getRateDataFromJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TimeSeriesEndpointStepDefinitions {
    private static Response response;
    private static RequestSpecification requestSpecification;
    private static RequestSpecBuilder requestSpecBuilder;
    private static final String API_KEY = System.getenv("apikey");

    @Given("the valid endpoint to GET timeseries")
    public void theValidEndpointToGETTimeseries() {
        RestAssured.basePath = "/timeseries";
        requestSpecBuilder = new RequestSpecBuilder().log(LogDetail.ALL);
    }

    @Given("I am authenticated user")
    public void iAmAuthenticatedUser() {
        requestSpecification = requestSpecBuilder
                .setContentType("Application/json")
                .addHeader("apikey", API_KEY)
                .build();
    }

    @Given("I am not authenticated user")
    public void iAmNotAuthenticated() {
        requestSpecification = requestSpecBuilder.build();
    }

    @When("I make a GET request")
    public void iMakeAGETRequest() {
        response = given(requestSpecification).when().get();
    }

    @When("I GET timeseries between {string} and {string} dates")
    public void iGETTimeseriesBetweenAnd(String startDate, String endDate) {
        requestSpecification = requestSpecBuilder
                .addParam("start_date", startDate)
                .addParam("end_date", endDate)
                .build();

        response = given(requestSpecification).when().get();
    }

    @Then("API responds with {int} status code")
    public void iGetStatusCode(int expectedStatusCode) {
        assertThat(response.statusCode(), equalTo(expectedStatusCode));
    }

    @And("Error code is {string}")
    public void errorCodeIs(String expectedErrorCode) {
        assertThat(new JsonPath(response.asString()).get("error.code"), equalTo(expectedErrorCode));
    }

    @When("I GET timeseries with {string} as base currency")
    public void iGETTimeseriesWithAsBaseCurrency(String baseCurrencyCode) {
        requestSpecification = requestSpecBuilder
                .addParam("start_date", "2020-02-01")
                .addParam("end_date", "2020-02-03")
                .addParam("base", baseCurrencyCode)
                .build();

        response = given(requestSpecification).when().get();
    }

    @Then("I get valid response")
    public void iGetValidResponseWithData(List<TimeseriesParams> params) {
        TimeseriesParams sent = params.get(0);
        RateData rateData = getRateDataFromJson(response.getBody().asString());

        assertThat(response.statusCode(), equalTo(200));
        assertThat(rateData.isSuccess(), equalTo(true));
        assertThat(rateData.isTimeseries(), equalTo(true));
        assertThat(rateData.getBase(), equalTo(sent.getBase()));
        assertThat(rateData.getStartDate(), equalTo(sent.getStartDate()));
        assertThat(rateData.getEndDate(), equalTo(sent.getEndDate()));
        assertThat(rateData.getRates().get(rateData.getStartDate()).size(), equalTo(sent.getSymbols().size()));
    }

    @When("I GET timeseries with params")
    public void iGETTimeseriesWithParams(List<TimeseriesParams> params) {
        TimeseriesParams timeseriesParams = params.get(0);

        requestSpecification = requestSpecBuilder
                .addParam("start_date", timeseriesParams.getStartDate())
                .addParam("end_date", timeseriesParams.getEndDate())
                .addParam("base", timeseriesParams.getBase())
                .addParam("symbols", timeseriesParams.getSymbols().toString().replaceAll("^\\[|]$", ""))
                .build();

        response = given(requestSpecification).when().get();
    }
}
