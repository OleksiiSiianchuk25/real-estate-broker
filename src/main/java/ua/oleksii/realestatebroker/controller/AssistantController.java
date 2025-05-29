package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.service.PropertyAssistantService;

import java.util.List;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AssistantController {
    private final PropertyAssistantService assistant;

    @GetMapping("/properties/near")
    public List<Property> near(
            @RequestParam String poiCategory,
            @RequestParam(defaultValue="1000") double radiusMeters) {
        return assistant.getNear(poiCategory, radiusMeters);
    }
}