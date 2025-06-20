package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.ChatRequest;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
import ua.oleksii.realestatebroker.model.PointOfInterest;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.PoiService;
import ua.oleksii.realestatebroker.service.PropertyService;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class ChatController {

    private final PropertyService propertyService;
    private final PoiService      poiService;
    private final OpenAiService   openAi;

    private static final Map<String, String> CAT_MAP = Map.ofEntries(
            Map.entry("парк", "park"),
            Map.entry("школ", "school"),
            Map.entry("садок", "kindergarten"),
            Map.entry("дитяч", "kindergarten"),
            Map.entry("лікарн", "hospital"),
            Map.entry("клінік", "hospital"),
            Map.entry("університет", "university"),
            Map.entry("майданчик", "playground"),
            Map.entry("ігров", "playground"),
            Map.entry("спорт", "sports_centre"),
            Map.entry("тренаж", "sports_centre"),
            Map.entry("поле", "pitch"),
            Map.entry("супермаркет", "supermarket"),
            Map.entry("молл", "mall"),
            Map.entry("торгівельн", "mall"),
            Map.entry("торговий", "mall"),
            Map.entry("магазин", "convenience"),
            Map.entry("маркет", "convenience"),
            Map.entry("пекар", "bakery"),
            Map.entry("зупинк", "bus_stop"),
            Map.entry("автобус", "bus_stop"),
            Map.entry("трамва", "tram_stop"),
            Map.entry("метро", "subway"),
            Map.entry("вхід", "subway_entrance"),
            Map.entry("автостан", "bus_station"),
            Map.entry("автовокзал", "bus_station"),
            Map.entry("платформ", "platform"),
            Map.entry("станці", "station"),
            Map.entry("парков", "parking"),
            Map.entry("стоян", "parking"),
            Map.entry("велосипедн", "bicycle_parking"),
            Map.entry("апте", "pharmacy"),
            Map.entry("банк", "bank"),
            Map.entry("банкомат", "atm"),
            Map.entry("ресторан", "restaurant"),
            Map.entry("кафе", "cafe")
    );

    private static final Map<String, String> CAT_UA = Map.ofEntries(
            Map.entry("park","парк"),
            Map.entry("school","школа"),
            Map.entry("kindergarten","дитячий садок"),
            Map.entry("hospital","лікарня"),
            Map.entry("university","університет"),
            Map.entry("playground","майданчик"),
            Map.entry("sports_centre","спортивний центр"),
            Map.entry("pitch","поле"),
            Map.entry("supermarket","супермаркет"),
            Map.entry("mall","торговий центр"),
            Map.entry("convenience","магазин"),
            Map.entry("bakery","пекарня"),
            Map.entry("bus_stop","автобусна зупинка"),
            Map.entry("tram_stop","трамвайна зупинка"),
            Map.entry("subway","метро"),
            Map.entry("subway_entrance","вхід до метро"),
            Map.entry("bus_station","автовокзал"),
            Map.entry("platform","платформа"),
            Map.entry("station","станція"),
            Map.entry("parking","парковка"),
            Map.entry("bicycle_parking","велопарковка"),
            Map.entry("pharmacy","аптека"),
            Map.entry("bank","банк"),
            Map.entry("atm","банкомат"),
            Map.entry("restaurant","ресторан"),
            Map.entry("cafe","кафе")
    );

    private static final Pattern EXIST_AT_PROPERTY = Pattern.compile(
            "(?:чи\\s+)?є\\s+біля\\s+квартир[аи]?\\s+(.+?)\\s+(\\S+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );
    private static final Pattern NEAR_SPECIFIC = Pattern.compile(
            "який\\s+(\\S+)\\s+біля\\s+(.+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );
    private static final Pattern NEAR_LIST = Pattern.compile(
            "біля\\s+яких\\s+квартир(?:и)?\\s+є\\s+(.+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );
    private static final Pattern NEAR_PROPERTIES = Pattern.compile(
            "квартир[аи]?\\s+біля\\s+(\\S+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    @PostMapping("/chat")
    public ResponseEntity<List<String>> chat(
            @AuthenticationPrincipal User user,
            @RequestBody ChatRequest req
    ) {
        if (user == null) {
            return ResponseEntity.status(403).build();
        }

        String text = req.getMessage().trim().toLowerCase(Locale.ROOT);
        double radius = 1000;  // м
        GeometryFactory gf = new GeometryFactory();

        // 0) “квартир біля {категорія}”
        Matcher m0 = NEAR_PROPERTIES.matcher(text);
        if (m0.matches()) {
            String catKey = m0.group(1);
            String category = CAT_MAP.entrySet().stream()
                    .filter(e -> catKey.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
            if (category != null) {
                List<Property> props = propertyService.findPropertiesNearCategory(category, radius);
                if (props.isEmpty()) {
                    return ResponseEntity.ok(
                            List.of("Квартир поблизу «" + CAT_UA.get(category) + "» не знайдено.")
                    );
                }
                List<String> out = new ArrayList<>();
                out.add("Знайдено " + props.size() + " квартир поблизу «" + CAT_UA.get(category) + "»:");
                props.forEach(p ->
                        out.add(String.format("<a href=\"/property/%d\">%s</a>", p.getId(), p.getTitle()))
                );
                return ResponseEntity.ok(out);
            }
        }

        // 1) “є біля квартири X Y?”
        Matcher m1 = EXIST_AT_PROPERTY.matcher(text);
        if (m1.matches()) {
            String titleQuery = m1.group(1).trim();
            String catKey     = m1.group(2);
            String category = CAT_MAP.entrySet().stream()
                    .filter(e -> catKey.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
            if (category != null) {
                Property p = propertyService.findByTitle(titleQuery);
                if (p == null) {
                    return ResponseEntity.ok(List.of("Квартира «" + titleQuery + "» не знайдена."));
                }
                Point loc = p.getGeom();
                PointOfInterest poi = poiService.findNearestByCategory(loc, category);
                if (poi != null) {
                    double meters = loc.distance(poi.getGeom()) * 111319.9;
                    return ResponseEntity.ok(
                            List.of(String.format("%s — %.0f м від %s", p.getTitle(), meters, poi.getName()))
                    );
                } else {
                    return ResponseEntity.ok(List.of("POI «" + CAT_UA.get(category) + "» неподалік не знайдено."));
                }
            }
        }

        // 2) “який Y біля X”
        Matcher m2 = NEAR_SPECIFIC.matcher(text);
        if (m2.matches()) {
            String catKey     = m2.group(1);
            String titleQuery = m2.group(2).trim();
            String category = CAT_MAP.entrySet().stream()
                    .filter(e -> catKey.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
            if (category != null) {
                Property p = propertyService.findByTitle(titleQuery);
                if (p == null) {
                    return ResponseEntity.ok(List.of("Квартира «" + titleQuery + "» не знайдена."));
                }
                Point loc = p.getGeom();
                PointOfInterest poi = poiService.findNearestByCategory(loc, category);
                if (poi != null) {
                    double meters = loc.distance(poi.getGeom()) * 111319.9;
                    return ResponseEntity.ok(
                            List.of(String.format("%s — %.0f м від %s", p.getTitle(), meters, poi.getName()))
                    );
                } else {
                    return ResponseEntity.ok(List.of("POI «" + CAT_UA.get(category) + "» неподалік не знайдено."));
                }
            }
        }

        // 3) “біля яких квартир є Y”
        Matcher m3 = NEAR_LIST.matcher(text);
        if (m3.matches()) {
            String catKey = m3.group(1).trim();
            String category = CAT_MAP.entrySet().stream()
                    .filter(e -> catKey.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
            if (category != null) {
                List<Property> props = propertyService.findPropertiesNearCategory(category, radius);
                if (props.isEmpty()) {
                    return ResponseEntity.ok(
                            List.of("Квартир поблизу «" + CAT_UA.get(category) + "» не знайдено.")
                    );
                }
                List<String> out = new ArrayList<>();
                out.add("Знайдено " + props.size() + " результатів:");
                props.forEach(p ->
                        out.add(String.format("<a href=\"/property/%d\">%s</a>", p.getId(), p.getTitle()))
                );
                return ResponseEntity.ok(out);
            }
        }

        // 4) загальний пошук по всіх квартирах згаданих категорій
        List<String> cats = CAT_MAP.entrySet().stream()
                .filter(e -> text.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .distinct()
                .collect(Collectors.toList());
        if (!cats.isEmpty()) {
            List<PropertyDTO> allProps = propertyService.getAllProperties();
            List<String> out = new ArrayList<>();
            out.add("Проаналізовано " + allProps.size() + " квартир. POI поруч:");
            for (PropertyDTO dto : allProps) {
                Point loc = gf.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
                List<String> found = new ArrayList<>();
                List<String> missing = new ArrayList<>();
                for (String cat : cats) {
                    PointOfInterest poi = poiService.findNearestByCategory(loc, cat);
                    if (poi != null) {
                        double meters = loc.distance(poi.getGeom()) * 111319.9;
                        found.add(CAT_UA.get(cat) + " (≈" + String.format("%.0f", meters) + " м)");
                    } else {
                        missing.add(CAT_UA.get(cat));
                    }
                }
                String line = String.format("<a href=\"/property/%d\">%s</a>: ", dto.getId(), dto.getTitle());
                if (!found.isEmpty())   line += String.join(", ", found);
                if (!missing.isEmpty()) line += "; відсутні: " + String.join(", ", missing);
                out.add(line);
            }
            return ResponseEntity.ok(out);
        }

        ChatCompletionRequest chatReq = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(
                        new ChatMessage("system", "Ви – експерт-рієлтор, даєте корисні поради."),
                        new ChatMessage("user", req.getMessage())
                ))
                .build();
        ChatCompletionResult result = openAi.createChatCompletion(chatReq);
        String advice = result.getChoices().get(0).getMessage().getContent().trim();
        return ResponseEntity.ok(List.of(advice));
    }
}
