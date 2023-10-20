package cucumber;

import io.restassured.builder.RequestSpecBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private final Map<String, Object> scenarioContext;
    @Getter
    @Setter
    private RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

    public ScenarioContext() {
        scenarioContext = new HashMap<>();
    }

    public Object getContext(String key) {
        return scenarioContext.get(key);
    }

    public void setContext(String key, Object value) {
        scenarioContext.put(key, value);
    }

}
