package api_tests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class RateData {
    @Getter
    private boolean success;
    @Getter
    private boolean timeseries;
    @Getter
    private String start_date;
    @Getter
    private String end_date;
    @Getter
    private String base;
    @Getter
    private Map<String, Map<String, Double>> rates;
}
