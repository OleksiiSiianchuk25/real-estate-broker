package ua.oleksii.realestatebroker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.dto.ReviewDTO;
import ua.oleksii.realestatebroker.model.Review;
import ua.oleksii.realestatebroker.repository.PropertyRepository;
import ua.oleksii.realestatebroker.repository.ReviewRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final PropertyRepository propertyRepo;

    /**
     * Повертає список ReviewDTO, відсортований за датою створення (спаданням)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ReviewDTO> getReviews(Long propertyId) {
        return reviewRepo.findByPropertyIdOrderByCreatedAtDesc(propertyId)
                .stream()
                .map(r -> new ReviewDTO(
                        r.getId(),
                        r.getAuthor(),
                        r.getComment(),
                        r.getRating(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Upsert: оновлення існуючого відгуку або створення нового
     */
    @Transactional
    public ReviewDTO addReview(Long propertyId, String author, String comment, int rating) {
        Review saved = reviewRepo.findByPropertyIdAndAuthor(propertyId, author)
                .map(existing -> {
                    existing.setComment(comment);
                    existing.setRating(rating);
                    existing.setCreatedAt(Instant.now());
                    return reviewRepo.save(existing);
                })
                .orElseGet(() -> {
                    Review r = new Review();
                    r.setProperty(propertyRepo.getReferenceById(propertyId));
                    r.setAuthor(author);
                    r.setComment(comment);
                    r.setRating(rating);
                    r.setCreatedAt(Instant.now());
                    return reviewRepo.save(r);
                });

        return new ReviewDTO(
                saved.getId(),
                saved.getAuthor(),
                saved.getComment(),
                saved.getRating(),
                saved.getCreatedAt()
        );
    }
}
