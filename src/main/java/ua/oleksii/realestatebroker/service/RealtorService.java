package ua.oleksii.realestatebroker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.dto.RealtorDTO;
import ua.oleksii.realestatebroker.dto.ReviewDTO;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealtorService {

    private final UserRepository userRepository;
    private final RealtorRatingService ratingService;

    public List<RealtorDTO> getAllRealtors() {
        return userRepository.findAllByRole(User.Role.REALTOR)
                .stream()
                .map(u -> new RealtorDTO(
                        u.getId(),
                        u.getFullName(),
                        u.getAgency(),
                        ratingService.getSummary(u.getId()).getAverageRating()
                ))
                .collect(Collectors.toList());
    }

    public RealtorDTO getRealtorById(Long id) {
        var u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Realtor not found"));
        var summary = ratingService.getSummary(id);
        return new RealtorDTO(
                u.getId(),
                u.getFullName(),
                u.getAgency(),
                summary.getAverageRating()
        );
    }

    public List<ReviewDTO> getReviewsByRealtor(Long realtorId) {
        return ratingService.getAllReviews(realtorId);
    }

    public ReviewDTO addReview(Long realtorId, Long userId, Integer rating, String comment) {
        return ratingService.addRating(realtorId, userId, rating, comment);
    }
}
