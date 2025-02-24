package ua.oleksii.realestatebroker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.repository.PropertyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public List<Property> getPropertiesByRealtor(User realtor) {
        return propertyRepository.findByRealtor(realtor);
    }

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    public List<Property> getFilteredProperties(String search, String status, String type, String city, Double minPrice, Double maxPrice) {
        // Якщо жоден фільтр не обраний - повертаємо всі properties
        if ((search == null || search.isEmpty()) &&
                (status == null || status.isEmpty()) &&
                (type == null || type.isEmpty()) &&
                (city == null || city.isEmpty()) &&
                minPrice == null &&
                maxPrice == null) {
            return propertyRepository.findAll();
        }

        // Конвертація статусу у ENUM
        Property.Status enumStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                enumStatus = Property.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + status);
            }
        }

        // Конвертація типу у ENUM
        Property.Type enumType = null;
        if (type != null && !type.isEmpty()) {
            try {
                enumType = Property.Type.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid type: " + type);
            }
        }

        return propertyRepository.findFilteredProperties(
                search == null ? "" : search,
                enumStatus,
                enumType,
                city == null ? "" : city,
                minPrice == null ? 0 : minPrice,
                maxPrice == null ? 0 : maxPrice
        );
    }
}
