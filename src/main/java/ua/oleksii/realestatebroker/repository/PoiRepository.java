package ua.oleksii.realestatebroker.repository;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.oleksii.realestatebroker.model.PointOfInterest;

import java.util.List;

public interface PoiRepository extends JpaRepository<PointOfInterest, Long> {

    /**
     * Знаходить POI заданої категорії, які в межах відстані (в метрах)
     * від точки location.
     */
    @Query(value = ""
            + "SELECT * "
            + "FROM points_of_interest p "
            + "WHERE p.category = :category "
            + "  AND ST_DWithin( "
            + "        p.geom, "
            + "        ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), "
            + "        :distance"
            + "  )",
            nativeQuery = true
    )
    List<PointOfInterest> findNearbyByCategory(
            @Param("category") String category,
            @Param("lng")      double longitude,
            @Param("lat")      double latitude,
            @Param("distance") double distanceInMeters
    );

    @Query(value = """
      SELECT poi.* 
      FROM points_of_interest poi
      JOIN properties p 
        ON p.id = :propertyId
      WHERE ST_DWithin(
        poi.geom::geography, 
        p.geom::geography, 
        :radiusMeters
      )
      """, nativeQuery = true)
    List<PointOfInterest> findPoisNearProperty(
            @Param("propertyId") Long propertyId,
            @Param("radiusMeters") double radiusMeters
    );

    @Query(value = """
        SELECT *
          FROM points_of_interest
         WHERE category = :category
         ORDER BY geom <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
         LIMIT 1
        """,
            nativeQuery = true)
    PointOfInterest findNearestByCategory(
            @Param("category") String category,
            @Param("lat") double latitude,
            @Param("lon") double longitude
    );
}
