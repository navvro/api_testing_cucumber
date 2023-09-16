package api_tests.exchange_rates;

import io.cucumber.java.DataTableType;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Value
public class TimeseriesParams {
    String startDate;
    String endDate;
    String base;
    List<String> symbols;

    @DataTableType
    public static TimeseriesParams makeObjectFromDataTable(Map<String, String> row) {
        ArrayList<String> symbolList = row.get("symbols") == null ?
                new ArrayList<>() : new ArrayList<>(Arrays.asList(row.get("symbols").split(",")));

        return new TimeseriesParams(
                row.get("startDate"),
                row.get("endDate"),
                row.get("base"),
                symbolList
        );
    }
}
