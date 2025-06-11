package ua.oleksii.realestatebroker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "realtor_ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"realtor_id", "author_id"}))
@Getter
@Setter
public class RealtorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realtor_id", nullable = false)
    private User realtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
