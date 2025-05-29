package ua.oleksii.realestatebroker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.repository.PropertyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyAssistantService {
    private final PropertyRepository propertyRepo;

    public List<Property> getNear(String category, double radiusMeters) {
        return propertyRepo.findPropertiesNearCategory(category, radiusMeters);
    }
}