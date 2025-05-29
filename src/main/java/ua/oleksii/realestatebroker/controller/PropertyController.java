package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.PropertyService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getAllProperties(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<PropertyDTO> dtos = propertyService.getFilteredProperties(
                        search, status, type, city, minPrice, maxPrice
                ).stream()
                .map(propertyService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        Property property = propertyService.getPropertyById(id);
        PropertyDTO dto = propertyService.convertToDTO(property);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<PropertyDTO> createProperty(
            @AuthenticationPrincipal User currentUser,
            @RequestBody PropertyDTO propertyDTO
    ) {
        PropertyDTO result = propertyService.convertToDTO(
                propertyService.createProperty(propertyDTO, currentUser)
        );
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            @RequestBody PropertyDTO propertyDTO
    ) {
        PropertyDTO result = propertyService.convertToDTO(
                propertyService.updateProperty(id, propertyDTO, currentUser)
        );
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<List<PropertyDTO>> getMyProperties(
            @AuthenticationPrincipal User currentUser
    ) {
        List<PropertyDTO> dtos = propertyService.getPropertiesByRealtor(currentUser)
                .stream()
                .map(propertyService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
