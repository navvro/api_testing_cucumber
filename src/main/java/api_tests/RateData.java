package api_tests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class RateData {
    @Getter
    private boolean success;
    @Getter
    private boolean timeseries;
    @Getter
    private String startDate;
    @Getter
    private String endDate;
    @Getter
    private String base;
    @Getter
    private Map<String, Map<String, Double>> rates;
}
