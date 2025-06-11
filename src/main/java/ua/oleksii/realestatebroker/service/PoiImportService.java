package ua.oleksii.realestatebroker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.oleksii.realestatebroker.model.PointOfInterest;
import ua.oleksii.realestatebroker.repository.PoiRepository;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PoiImportService {

    private static final Logger log = LoggerFactory.getLogger(PoiImportService.class);
    private final PoiRepository repo;
    private final ObjectMapper mapper;
    private final GeoJsonReader reader = new GeoJsonReader();

    public PoiImportService(PoiRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public void importFromGeoJson(InputStream geojson) throws IOException {
        JsonNode root = mapper.readTree(geojson);
        int imported = 0, skipped = 0;

        for (JsonNode feature : root.path("features")) {
            try {
                JsonNode props = feature.path("properties");
                JsonNode geomNode = feature.path("geometry");

                Point p = (Point) reader.read(geomNode.toString());

                PointOfInterest poi = new PointOfInterest();
                poi.setName(props.path("name").asText(""));

                String category = "";
                if (props.has("amenity")) {
                    category = props.get("amenity").asText();
                } else if (props.has("leisure")) {
                    category = props.get("leisure").asText();
                } else if (props.has("shop")) {
                    category = props.get("shop").asText();
                } else if (props.has("highway")) {
                    category = props.get("highway").asText();
                } else if (props.has("railway")) {
                    category = props.get("railway").asText();
                } else if (props.has("public_transport")) {
                    category = props.get("public_transport").asText();
                }
                poi.setCategory(category);

                poi.setGeom(p);

                repo.save(poi);
                imported++;

            } catch (ParseException e) {
                log.warn("Не вдалося розпарсити геометрію, пропускаємо feature: {}", e.getMessage());
                skipped++;
            }
        }

        log.info("POI Import: успішно імпортовано {}, пропущено {}", imported, skipped);
    }
}
