package ua.oleksii.realestatebroker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.PropertyCategory;
import ua.oleksii.realestatebroker.repository.PropertyCategoryRepository;

import java.util.List;

@Service
public class PropertyCategoryService {

    @Autowired
    private PropertyCategoryRepository propertyCategoryRepository;

    public List<PropertyCategory> getCategoriesByProperty(Long propertyId) {
        return propertyCategoryRepository.findByPropertyId(propertyId);
    }

    public PropertyCategory addCategoryToProperty(PropertyCategory propertyCategory) {
        return propertyCategoryRepository.save(propertyCategory);
    }

    public void removeCategoryFromProperty(Long id) {
        propertyCategoryRepository.deleteById(id);
    }
}
