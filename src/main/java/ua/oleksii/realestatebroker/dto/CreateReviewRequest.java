package ua.oleksii.realestatebroker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateReviewRequest {
    private String comment;
    @Min(1)
    @Max(5)
    private Integer rating;
}