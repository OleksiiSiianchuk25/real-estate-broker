package ua.oleksii.realestatebroker.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.Review;
import ua.oleksii.realestatebroker.repository.PropertyRepository;
import ua.oleksii.realestatebroker.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final PropertyRepository propertyRepo;

    public List<Review> getReviews(Long propertyId) {
        return reviewRepo.findByPropertyIdOrderByCreatedAtDesc(propertyId);
    }

    public Review addReview(Long propertyId, String author, String comment, Integer rating) {
        Property prop = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        Review rv = new Review();
        rv.setProperty(prop);
        rv.setAuthor(author);
        rv.setComment(comment);
        rv.setRating(rating);
        return reviewRepo.save(rv);
    }
}
