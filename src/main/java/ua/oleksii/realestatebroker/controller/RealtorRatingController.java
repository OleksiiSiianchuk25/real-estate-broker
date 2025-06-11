package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.CreateReviewRequest;
import ua.oleksii.realestatebroker.dto.RealtorRatingSummaryDTO;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.RealtorRatingService;

@RestController
@RequestMapping("/api/realtors/{realtorId}/ratings")
@RequiredArgsConstructor
public class RealtorRatingController {

    private final RealtorRatingService ratingService;

    @GetMapping("/summary")
    public ResponseEntity<RealtorRatingSummaryDTO> getSummary(@PathVariable Long realtorId) {
        return ResponseEntity.ok(ratingService.getSummary(realtorId));
    }

    @PostMapping
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> addRating(
            @PathVariable Long realtorId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody CreateReviewRequest req
    ) {
        ratingService.addRating(
                realtorId,
                currentUser.getId(),
                req.getRating(),
                req.getComment()
        );
        return ResponseEntity.ok().build();
    }
}

