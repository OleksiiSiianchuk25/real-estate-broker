package ua.oleksii.realestatebroker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            @RequestBody PropertyDTO propertyDTO
    ) {
        Property updated = propertyService.updateProperty(id, propertyDTO, currentUser);
        PropertyDTO resultDTO = propertyService.convertToDTO(updated);
        return ResponseEntity.ok(resultDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getProperty(@PathVariable Long id) {
        Optional<Property> property = Optional.ofNullable(propertyService.getPropertyById(id));
        return ResponseEntity.ok(property.orElse(null));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<PropertyDTO> createProperty(
            @AuthenticationPrincipal User currentUser,
            @RequestBody PropertyDTO propertyDTO
    ) {
        Property property = propertyService.createProperty(propertyDTO, currentUser);
        PropertyDTO resultDTO = propertyService.convertToDTO(property);
        return ResponseEntity.ok(resultDTO);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<List<Property>> getMyProperties(@AuthenticationPrincipal User currentUser) {
        List<Property> properties = propertyService.getPropertiesByRealtor(currentUser);
        return ResponseEntity.ok(properties);
    }
}
