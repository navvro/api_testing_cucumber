package api_tests.exchange_rates;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TimeSeriesEndpointStepDefinitions {
    private static Response response;
    private static RequestSpecification requestSpecification;
    private static final String API_KEY = System.getenv("apikey");

    @Given("the valid endpoint to GET timeseries")
    public void theValidEndpointToGETTimeseries() {
        RestAssured.basePath = "/timeseries";
    }

    @When("I make a GET request")
    public void iMakeAGETRequest() {
        response = given(requestSpecification).when().get();
    }

    @Given("I am authenticated user")
    public void iAmAuthenticatedUser() {
        requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "Application/json")
                .addHeader("apikey", API_KEY)
                .build();
    }

    @Given("I am not authenticated user")
    public void iAmNotAuthenticated() {
        requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "Application/json")
                .build();
    }

    @When("I GET timeseries between {string} and {string} dates")
    public void iGETTimeseriesBetweenAnd(String startDate, String endDate) {
        requestSpecification = new RequestSpecBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Content-type", "Application/json")
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
        requestSpecification = new RequestSpecBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Content-type", "Application/json")
                .addParam("start_date", "2020-02-01")
                .addParam("end_date", "2020-02-03")
                .addParam("base", baseCurrencyCode)
                .build();

        response = given(requestSpecification).when().get();
    }

    @Then("I get valid response")
    public void iGetValidResponseWithData() {
        assertThat(response.statusCode(),equalTo(200));
    }
}
