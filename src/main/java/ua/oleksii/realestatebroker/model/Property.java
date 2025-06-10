package ua.oleksii.realestatebroker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @ManyToOne
    @JoinColumn(name = "realtor_id", nullable = false)
    private User realtor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String imageUrl;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;


    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point geom;

    public Double getLatitude() {
        return (geom != null) ? geom.getY() : null;
    }

    public Double getLongitude() {
        return (geom != null) ? geom.getX() : null;
    }

    public enum Type {
        APARTMENT, HOUSE
    }

    public enum Status {
        FOR_SALE,
        FOR_RENT,
        SOLD;

        @JsonCreator
        public static Status fromString(String value) {
            return Status.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return name();
        }
    }
}
