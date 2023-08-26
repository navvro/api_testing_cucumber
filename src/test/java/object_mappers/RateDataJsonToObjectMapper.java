package object_mappers;

import api_tests.RateData;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RateDataJsonToObjectMapper {

    public static RateData getRateDataFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            boolean success = jsonNode.get("success").asBoolean();
            boolean timeseries = jsonNode.get("timeseries").asBoolean();
            String startDate = jsonNode.get("start_date").asText();
            String endDate = jsonNode.get("end_date").asText();
            String base = jsonNode.get("base").asText();

            JsonNode ratesNode = jsonNode.get("rates");
            Map<String, Map<String, Double>> rates = new HashMap<>();
            ratesNode.fields().forEachRemaining(entry -> {
                String date = entry.getKey();
                JsonNode currencyNode = entry.getValue();
                Map<String, Double> currencyRates = new HashMap<>();
                currencyNode.fields().forEachRemaining(rateEntry -> {
                    String currency = rateEntry.getKey();
                    double rate = rateEntry.getValue().asDouble();
                    currencyRates.put(currency, rate);
                });
                rates.put(date, currencyRates);
            });

            return new RateData(success, timeseries, startDate, endDate, base, rates);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
