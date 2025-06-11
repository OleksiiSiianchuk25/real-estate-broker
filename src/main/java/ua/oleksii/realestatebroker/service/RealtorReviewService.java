package ua.oleksii.realestatebroker.service;

import ua.oleksii.realestatebroker.dto.ReviewDTO;

import java.util.List;

public interface RealtorReviewService {
    double getAverageRating(Long realtorId);

    List<ReviewDTO> getReviewsForRealtor(Long realtorId);

    ReviewDTO addReview(Long realtorId, Long userId, int rating, String comment);
}
