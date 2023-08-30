package object_mappers;

import api_tests.RateData;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RateDataJsonToObjectMapper {

    public static RateData getRateDataFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, RateData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
