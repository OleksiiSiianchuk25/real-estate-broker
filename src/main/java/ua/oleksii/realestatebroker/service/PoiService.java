package ua.oleksii.realestatebroker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.PointOfInterest;
import ua.oleksii.realestatebroker.repository.PoiRepository;

import org.locationtech.jts.geom.Point;
import java.util.List;

@Service
public class PoiService {
    @Autowired
    private PoiRepository poiRepository;

    public List<PointOfInterest> findPoisNearProperty(Long propertyId, double radius) {
        return poiRepository.findPoisNearProperty(propertyId, radius);
    }

    public PointOfInterest findNearestByCategory(Point loc, String category) {
        double lat = loc.getY();
        double lon = loc.getX();
        return poiRepository.findNearestByCategory(category, lat, lon);
    }
}
