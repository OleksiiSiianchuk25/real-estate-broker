package ua.oleksii.realestatebroker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateReviewRequest {
    @NotBlank
    private String comment;
    @Min(1)
    @Max(5)
    private Integer rating;
}