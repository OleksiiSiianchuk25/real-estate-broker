package ua.oleksii.realestatebroker.service;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
import ua.oleksii.realestatebroker.dto.PropertyBriefDTO;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.repository.PropertyRepository;
import ua.oleksii.realestatebroker.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final ReviewRepository   reviewRepository;

    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropertyDTO> getPropertiesByRealtor(User realtor) {
        return propertyRepository.findByRealtor(realtor)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropertyDTO> getFilteredProperties(String search,
                                                   String status,
                                                   String type,
                                                   String city,
                                                   Double minPrice,
                                                   Double maxPrice) {

        Property.Status enumStatus = (status != null && !status.isEmpty())
                ? Property.Status.valueOf(status.toUpperCase())
                : null;
        Property.Type enumType = (type != null && !type.isEmpty())
                ? Property.Type.valueOf(type.toUpperCase())
                : null;

        return propertyRepository.findFilteredProperties(
                        search == null ? "" : search,
                        enumStatus,
                        enumType,
                        city == null ? "" : city,
                        minPrice == null ? 0 : minPrice,
                        maxPrice == null ? 0 : maxPrice
                )
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Property createProperty(PropertyDTO dto, User realtor) {
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setType(dto.getType());
        property.setStatus(dto.getStatus());
        property.setAddress(dto.getAddress());
        property.setCity(dto.getCity());
        Point geom = new GeometryFactory()
                .createPoint(new org.locationtech.jts.geom.Coordinate(
                        dto.getLongitude(), dto.getLatitude()));
        property.setGeom(geom);
        property.setLatitude(dto.getLatitude());
        property.setLongitude(dto.getLongitude());
        property.setImageUrl(dto.getImageUrl());
        property.setRealtor(realtor);
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, PropertyDTO dto, User currentUser) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оголошення не знайдено"));

        if (!property.getRealtor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Ви не маєте права редагувати це оголошення");
        }

        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setCity(dto.getCity());
        property.setAddress(dto.getAddress());
        Point geom = new GeometryFactory()
                .createPoint(new org.locationtech.jts.geom.Coordinate(
                        dto.getLongitude(), dto.getLatitude()));
        property.setGeom(geom);
        property.setLatitude(dto.getLatitude());
        property.setLongitude(dto.getLongitude());
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
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setLatitude(property.getGeom().getY());
        dto.setLongitude(property.getGeom().getX());
        dto.setPrice(property.getPrice());
        dto.setType(property.getType());
        dto.setStatus(property.getStatus());
        dto.setImageUrl(property.getImageUrl());
        dto.setRealtorId(property.getRealtor().getId());
        dto.setRealtorFullName(property.getRealtor().getFullName());
        dto.setRealtorPhone(property.getRealtor().getPhone());
        dto.setRealtorEmail(property.getRealtor().getEmail());

        Double avgPropRating     = reviewRepository.findAvgRatingByPropertyId(property.getId());
        Double avgRealtorRating  = reviewRepository.findAvgRatingByRealtorId(property.getRealtor().getId());
        dto.setRating(avgPropRating != null ? avgPropRating : 0.0);
        dto.setRealtorRating(avgRealtorRating != null ? avgRealtorRating : 0.0);

        return dto;
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оголошення не знайдено"));
        return convertToDTO(property);
    }

    public List<PropertyBriefDTO> getPropertiesByRealtor(Long realtorId) {
        return propertyRepository.findAllByRealtorId(realtorId)
                .stream()
                .map(p -> new PropertyBriefDTO(
                        p.getId(),
                        p.getTitle(),
                        p.getPrice().toString(),
                        p.getImageUrl()))
                .collect(Collectors.toList());
    }

    public List<Property> findPropertiesNearCategory(String category, double radiusMeters) {
        return propertyRepository.findPropertiesNearCategory(category, radiusMeters);
    }

    public List<Property> getPropertiesByType(Property.Type type) {
        return propertyRepository.findFilteredProperties(
                "",
                null,
                type,
                "", 0.0, 0.0
        );
    }

    public double getDistanceToCategory(Long propertyId, String category) {
        return propertyRepository.findMinDistanceToCategory(propertyId, category);
    }

    public Property findByTitle(String title) {
        return propertyRepository.findByTitle(title).orElse(null);
    }

}
