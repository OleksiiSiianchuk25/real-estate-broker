package ua.oleksii.realestatebroker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.CreateReviewRequest;
import ua.oleksii.realestatebroker.dto.ReviewDTO;
import ua.oleksii.realestatebroker.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/properties/{propertyId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewDTO> list(@PathVariable Long propertyId) {
        return reviewService.getReviews(propertyId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ReviewDTO create(
            @PathVariable Long propertyId,
            @RequestBody @Valid CreateReviewRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        return reviewService.addReview(
                propertyId,
                user.getUsername(),
                req.getComment(),
                req.getRating()
        );
    }
}
