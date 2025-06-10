package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.oleksii.realestatebroker.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propId")
    Double findAvgRatingByPropertyId(@Param("propId") Long propId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.realtor.id = :realtorId")
    Double findAvgRatingByRealtorId(@Param("realtorId") Long realtorId);
}

