package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.PropertyService;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getProperties(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        List<PropertyDTO> dtos = propertyService.getFilteredProperties(
                search, status, type, city, minPrice, maxPrice
        );
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        PropertyDTO dto = propertyService.getPropertyById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<PropertyDTO> createProperty(
            @AuthenticationPrincipal User currentUser,
            @RequestBody PropertyDTO propertyDTO
    ) {
        PropertyDTO dto = propertyService.convertToDTO(
                propertyService.createProperty(propertyDTO, currentUser)
        );
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REALTOR','ADMIN')")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            @RequestBody PropertyDTO propertyDTO
    ) {
        PropertyDTO dto = propertyService.convertToDTO(
                propertyService.updateProperty(id, propertyDTO, currentUser)
        );
        return ResponseEntity.ok(dto);
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
        List<PropertyDTO> dtos = propertyService.getPropertiesByRealtor(currentUser);
        return ResponseEntity.ok(dtos);
    }
}
