package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.oleksii.realestatebroker.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    List<Review> findAllByPropertyId(Long propertyId);

    Optional<Review> findByPropertyIdAndAuthor(Long propertyId, String author);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.property.id = :propId")
    Double findAvgRatingByPropertyId(@Param("propId") Long propId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.property.realtor.id = :realtorId")
    Double findAvgRatingByRealtorId(@Param("realtorId") Long realtorId);
}
