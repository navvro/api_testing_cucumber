package api_tests.exchange_rates;

import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TimeSeriesEndpointStepDefinitions extends BaseSteps{

    public TimeSeriesEndpointStepDefinitions(TestContext testContext) {
        super(testContext);
    }

    private static String getSymbolsParam(TimeseriesParams timeseriesParams) {
        return timeseriesParams.getSymbols().toString().replaceAll("^\\[|]$", "");
    }

    @Given("the valid endpoint to GET timeseries")
    public void theValidEndpointToGETTimeseries() {
        getScenarioContext().getRequestSpecBuilder().setBasePath("/timeseries");
    }

    @When("I make GET request to timeseries with params")
    public void iGETTimeseriesWithParams(List<TimeseriesParams> paramsList) {
        TimeseriesParams params = paramsList.get(0);

        Response response = given(getScenarioContext().getRequestSpecBuilder()
                .addParam("start_date", params.getStartDate())
                .addParam("end_date", params.getEndDate())
                .addParam("base", params.getBase())
                .addParam("symbols", getSymbolsParam(params))
                .build())
                .when()
                .get();

        getScenarioContext().setContext("RESPONSE", response);
    }

    @When("I make GET request two times with params")
    public void iMakeGETRequestTwoTimesWithParams(List<TimeseriesParams> paramsList) {
        TimeseriesParams params = paramsList.get(0);

        RequestSpecification requestSpecification = getScenarioContext().getRequestSpecBuilder()
                .addParam("start_date", params.getStartDate())
                .addParam("end_date", params.getEndDate())
                .addParam("base", params.getBase())
                .addParam("symbols", getSymbolsParam(params))
                .build();

        Response response = given(requestSpecification).when().get();
        Response secondResponse = given(requestSpecification).when().get();

        getScenarioContext().setContext("RESPONSE", response);
        getScenarioContext().setContext("SECOND_RESPONSE", secondResponse);
    }
}
