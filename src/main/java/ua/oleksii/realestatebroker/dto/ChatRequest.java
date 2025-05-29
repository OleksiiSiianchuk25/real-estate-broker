package ua.oleksii.realestatebroker.dto;

import lombok.Getter;
import lombok.Setter;

/** Запит віджета чату */
@Setter
@Getter
public class ChatRequest {
    private String message;
    public ChatRequest() {}
    public ChatRequest(String message) { this.message = message; }

}
