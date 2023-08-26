package api_tests.exchange_rates;

import io.cucumber.java.DataTableType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class TimeseriesParams {
    @Getter
    private String startDate;
    @Getter
    private String endDate;
    @Getter
    private String base;
    @Getter
    private List<String> symbols;

    @DataTableType
    public static TimeseriesParams makeObjectFromStep(Map<String, String> row) {
        return new TimeseriesParams(
                row.get("startDate"),
                row.get("endDate"),
                row.get("base"),
                new ArrayList<>(Arrays.asList(row.get("symbols").split(",")))
        );
    }
}
