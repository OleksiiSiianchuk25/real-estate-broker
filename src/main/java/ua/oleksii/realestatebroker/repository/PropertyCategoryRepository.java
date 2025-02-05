package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.oleksii.realestatebroker.model.PropertyCategory;

import java.util.List;

public interface PropertyCategoryRepository extends JpaRepository<PropertyCategory, Long> {
    List<PropertyCategory> findByPropertyId(Long propertyId);
}
