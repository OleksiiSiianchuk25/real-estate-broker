package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("SELECT p FROM Property p WHERE (:status IS NULL OR p.status = :status)")
    List<Property> findByStatus(@Param("status") Property.Status status);

    List<Property> findByRealtor(User realtor);

    @Query("SELECT p FROM Property p WHERE " +
            "(COALESCE(:search, '') = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(COALESCE(:status, '') = '' OR p.status = :status) AND " +
            "(COALESCE(:type, '') = '' OR p.type = :type) AND " +
            "(COALESCE(:city, '') = '' OR p.city = :city) AND " +
            "(COALESCE(:minPrice, 0) = 0 OR p.price >= :minPrice) AND " +
            "(COALESCE(:maxPrice, 0) = 0 OR p.price <= :maxPrice)")
    List<Property> findFilteredProperties(
            @Param("search") String search,
            @Param("status") Property.Status status,
            @Param("type") Property.Type type,
            @Param("city") String city,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

}
