package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.oleksii.realestatebroker.model.RealtorRating;

import java.util.List;

public interface RealtorRatingRepository extends JpaRepository<RealtorRating, Long> {

    List<RealtorRating> findByRealtorId(Long realtorId);

    @Query("SELECT COALESCE(AVG(r.rating),0) FROM RealtorRating r WHERE r.realtor.id = :realtorId")
    double averageRatingByRealtor(@Param("realtorId") Long realtorId);

    long countByRealtorId(Long realtorId);
}
