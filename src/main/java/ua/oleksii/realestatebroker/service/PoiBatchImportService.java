package ua.oleksii.realestatebroker.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.repository.PoiRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Service
@Slf4j
public class PoiBatchImportService {

    private final PoiImportService importer;
    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final PoiRepository repo;

    public PoiBatchImportService(PoiImportService importer, PoiRepository repo) {
        this.importer = importer;
        this.repo = repo;
    }

    /**
     * Імпортує всі файли pois_*.geojson із папки states/
     * Викликається при старті контексту
     */
    @PostConstruct
    public void importAllStates() {
        if (repo.count() > 0) {
            log.info("POI вже імпортовані — пропускаємо цей процес'");
            return;
        }

        try {
            Resource[] resources = resolver.getResources("classpath:states/pois_*.geojson");
            for (Resource r : resources) {
                try (InputStream is = r.getInputStream()) {
                    importer.importFromGeoJson(is);
                    System.out.println("Imported POIs from " + r.getFilename());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Не вдалося знайти/прочитати GeoJSON-файли", e);
        }
    }

}
