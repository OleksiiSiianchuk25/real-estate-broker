package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.model.PropertyCategory;
import ua.oleksii.realestatebroker.service.PropertyCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/property-categories")
public class PropertyCategoryController {

    @Autowired
    private PropertyCategoryService propertyCategoryService;

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<PropertyCategory>> getCategoriesByProperty(@PathVariable Long propertyId) {
        List<PropertyCategory> categories = propertyCategoryService.getCategoriesByProperty(propertyId);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<PropertyCategory> addCategoryToProperty(@RequestBody PropertyCategory propertyCategory) {
        PropertyCategory saved = propertyCategoryService.addCategoryToProperty(propertyCategory);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCategoryFromProperty(@PathVariable Long id) {
        propertyCategoryService.removeCategoryFromProperty(id);
        return ResponseEntity.ok().build();
    }
}
