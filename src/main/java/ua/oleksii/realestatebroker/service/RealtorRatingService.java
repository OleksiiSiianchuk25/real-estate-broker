package ua.oleksii.realestatebroker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.oleksii.realestatebroker.dto.RealtorRatingSummaryDTO;
import ua.oleksii.realestatebroker.dto.ReviewDTO;
import ua.oleksii.realestatebroker.model.RealtorRating;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.repository.RealtorRatingRepository;
import ua.oleksii.realestatebroker.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealtorRatingService {

    private final RealtorRatingRepository ratingRepo;
    private final UserRepository userRepo;


        @Transactional(readOnly = true)
    public RealtorRatingSummaryDTO getSummary(Long realtorId) {
        double avg = ratingRepo.averageRatingByRealtor(realtorId);
        long count = ratingRepo.countByRealtorId(realtorId);
        return new RealtorRatingSummaryDTO(avg, count);
    }


    @Transactional
    public ReviewDTO addRating(Long realtorId, Long authorId, int rating, String comment) {
        User realtor = userRepo.findById(realtorId)
                .orElseThrow(() -> new IllegalArgumentException("Realtor not found"));
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        RealtorRating rr = new RealtorRating();
        rr.setRealtor(realtor);
        rr.setAuthor(author);
        rr.setRating(rating);
        rr.setComment(comment);
        rr = ratingRepo.save(rr);

        return new ReviewDTO(
                rr.getId(),
                rr.getAuthor().getFullName(),
                rr.getComment(),
                rr.getRating(),
                rr.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviews(Long realtorId) {
        return ratingRepo.findByRealtorId(realtorId).stream()
                .map(rr -> new ReviewDTO(
                        rr.getId(),
                        rr.getAuthor().getFullName(),
                        rr.getComment(),
                        rr.getRating(),
                        rr.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
