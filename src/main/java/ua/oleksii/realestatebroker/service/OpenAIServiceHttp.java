package ua.oleksii.realestatebroker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OpenAIServiceHttp {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    public JsonNode buildFunctionCallingPayload(String userMessage) {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", "gpt-4-0613");

        ArrayNode messages = mapper.createArrayNode();
        messages.add(createMessage("system", "You are a real-estate assistant. Use provided functions to help the user."));
        messages.add(createMessage("user", userMessage));
        payload.set("messages", messages);

        ArrayNode functions = mapper.createArrayNode();
        functions.add(createGetPropertiesNearPOIFunction());
        functions.add(createCheckPropertyPOIFunction());
        payload.set("functions", functions);

        return payload;
    }

    private ObjectNode createMessage(String role, String content) {
        ObjectNode msg = mapper.createObjectNode();
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }

    private ObjectNode createGetPropertiesNearPOIFunction() {
        ObjectNode func = mapper.createObjectNode();
        func.put("name", "getPropertiesNearPOI");
        func.put("description", "Return list of properties near a given POI category");

        ObjectNode params = mapper.createObjectNode();
        params.put("type", "object");

        ObjectNode properties = mapper.createObjectNode();
        properties.set("category", createProperty("string", "POI category in English"));
        properties.set("radius", createProperty("integer", "Search radius in meters"));

        params.set("properties", properties);
        params.set("required", mapper.createArrayNode().add("category"));
        func.set("parameters", params);

        return func;
    }

    private ObjectNode createCheckPropertyPOIFunction() {
        ObjectNode func = mapper.createObjectNode();
        func.put("name", "checkPropertyPOI");
        func.put("description", "Return distance from a property to the nearest POI of given category");

        ObjectNode params = mapper.createObjectNode();
        params.put("type", "object");

        ObjectNode properties = mapper.createObjectNode();
        properties.set("propertyTitle", createProperty("string", "Title of the property"));
        properties.set("category", createProperty("string", "POI category in English"));

        params.set("properties", properties);
        params.set("required", mapper.createArrayNode().add("propertyTitle").add("category"));
        func.set("parameters", params);

        return func;
    }

    private ObjectNode createProperty(String type, String description) {
        ObjectNode prop = mapper.createObjectNode();
        prop.put("type", type);
        prop.put("description", description);
        return prop;
    }

    public JsonNode chatWithFunctionCalling(JsonNode payload) {
        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload.toString())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
