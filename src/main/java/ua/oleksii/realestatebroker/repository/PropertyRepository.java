package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByStatus(Property.Status status);
    List<Property> findByRealtor(User realtor);
}
