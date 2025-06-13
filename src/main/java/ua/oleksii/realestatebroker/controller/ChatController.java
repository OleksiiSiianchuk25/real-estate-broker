package ua.oleksii.realestatebroker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.ChatRequest;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.OpenAIServiceHttp;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class ChatController {

    private final OpenAIServiceHttp openAiServiceHttp;

    @PostMapping("/chat")
    public ResponseEntity<JsonNode> chat(
            @AuthenticationPrincipal User user,
            @RequestBody ChatRequest req
    ) {
        if (user == null) {
            return ResponseEntity.status(403).build();
        }

        JsonNode payload = openAiServiceHttp.buildFunctionCallingPayload(req.getMessage());
        JsonNode result = openAiServiceHttp.chatWithFunctionCalling(payload);

        return ResponseEntity.ok(result);
    }
}
