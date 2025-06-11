package ua.oleksii.realestatebroker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String comment;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
