package ua.oleksii.realestatebroker.dto;

import lombok.Getter;

@Getter
public class RealtorRatingSummaryDTO {
    private final double averageRating;
    private final long count;

    public RealtorRatingSummaryDTO(double averageRating, long count) {
        this.averageRating = averageRating;
        this.count = count;
    }
}
