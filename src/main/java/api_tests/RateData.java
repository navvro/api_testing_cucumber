package api_tests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
public class RateData {
    boolean success;
    boolean timeseries;
    @JsonProperty("start_date")
    String startDate;
    @JsonProperty("end_date")
    String endDate;
    String base;
    Map<String, Map<String, Double>> rates;
}
