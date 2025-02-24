package ua.oleksii.realestatebroker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.PropertyService;
import ua.oleksii.realestatebroker.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<Property> properties = propertyService.getFilteredProperties(search, status, type, city, minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        Long realtorId = property.getRealtorId();

        Optional<User> realtor = userService.findById(realtorId);
        if (realtor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (property.getCity() == null || property.getCity().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        property.setRealtor(realtor.get());

        Property savedProperty = propertyService.createProperty(property);

        return ResponseEntity.ok(savedProperty);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property updatedProperty) {
        Optional<Property> existingProperty = propertyService.getPropertyById(id);
        if (existingProperty.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Property property = existingProperty.get();
        property.setTitle(updatedProperty.getTitle());
        property.setPrice(updatedProperty.getPrice());
        property.setDescription(updatedProperty.getDescription());

        Property savedProperty = propertyService.createProperty(property);

        return ResponseEntity.ok(savedProperty);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok().build();
    }
}
