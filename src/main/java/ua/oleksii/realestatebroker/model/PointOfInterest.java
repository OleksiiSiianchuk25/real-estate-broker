package ua.oleksii.realestatebroker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "points_of_interest")
@Getter
@Setter
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Назва POI */
    @Column(nullable = false, length = 255)
    private String name;

    /** Категорія (park, school, hospital тощо) */
    @Column(nullable = false, length = 50)
    private String category;

    /**
     * Геометрія точки у WGS84.
     * Hibernate-Spatial 6+ автоматично підключить цю колонку як PostGIS GEOMETRY.
     */
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point geom;
}
