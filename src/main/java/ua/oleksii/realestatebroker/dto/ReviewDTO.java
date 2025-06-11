package ua.oleksii.realestatebroker.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDTO {
    private Long id;
    private String author;
    private String comment;
    private Integer rating;
    private Instant createdAt;
}
