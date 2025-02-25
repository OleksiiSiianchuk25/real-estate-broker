package ua.oleksii.realestatebroker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
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

    public List<Property> getPropertiesByRealtor(User realtor) {
        return propertyRepository.findByRealtor(realtor);
    }

    public List<Property> getFilteredProperties(String search, String status, String type, String city, Double minPrice, Double maxPrice) {
        if ((search == null || search.isEmpty()) &&
                (status == null || status.isEmpty()) &&
                (type == null || type.isEmpty()) &&
                (city == null || city.isEmpty()) &&
                minPrice == null &&
                maxPrice == null) {
            return propertyRepository.findAll();
        }

        Property.Status enumStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                enumStatus = Property.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + status);
            }
        }

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

    public Property createProperty(PropertyDTO dto, User realtor) {
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setType(dto.getType());
        property.setStatus(dto.getStatus());
        property.setAddress(dto.getAddress());
        property.setLatitude(dto.getLatitude());
        property.setLongitude(dto.getLongitude());
        property.setCity(dto.getCity());
        property.setImageUrl(dto.getImageUrl());
        property.setRealtor(realtor);
        return propertyRepository.save(property);
    }

    // Оновлений метод, який приймає DTO і поточного користувача
    public Property updateProperty(Long id, PropertyDTO dto, User currentUser) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оголошення не знайдено"));

        // Перевірка: чи поточний користувач є власником оголошення
        if (!property.getRealtor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Ви не маєте права редагувати це оголошення");
        }

        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setCity(dto.getCity());
        property.setAddress(dto.getAddress());
        property.setImageUrl(dto.getImageUrl());
        property.setStatus(dto.getStatus());
        property.setType(dto.getType());
        return propertyRepository.save(property);
    }

    public PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setType(property.getType());
        dto.setStatus(property.getStatus());
        dto.setAddress(property.getAddress());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setCity(property.getCity());
        dto.setImageUrl(property.getImageUrl());
        return dto;
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оголошення не знайдено"));
    }
}
