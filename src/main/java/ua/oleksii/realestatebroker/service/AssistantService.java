package ua.oleksii.realestatebroker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.service.PropertyAssistantService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private final PropertyAssistantService propertyAssistantService;

    public String generateReply(String userMessage) {
        Pattern p = Pattern.compile("біля\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(userMessage);
        if (m.find()) {
            String category = m.group(1).toLowerCase();
            double radiusMeters = 1000;

            List<Property> props = propertyAssistantService.getNear(category, radiusMeters);
            if (props.isEmpty()) {
                return "Вибач, я не знайшов квартир біля " + category + ".";
            }
            String list = props.stream()
                    .map(ppt -> "- " + ppt.getTitle() + ", адреса: " + ppt.getAddress())
                    .limit(5)
                    .collect(Collectors.joining("\n"));
            return "Ось кілька квартир біля " + category + ":\n" + list;
        }

        return "Не зовсім зрозумів запит. Спробуй, наприклад: \"квартири біля парку\".";
    }
}
