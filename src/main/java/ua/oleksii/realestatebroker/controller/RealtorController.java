package ua.oleksii.realestatebroker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.CreateReviewRequest;
import ua.oleksii.realestatebroker.dto.PropertyBriefDTO;
import ua.oleksii.realestatebroker.dto.RealtorDTO;
import ua.oleksii.realestatebroker.dto.ReviewDTO;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.PropertyService;
import ua.oleksii.realestatebroker.service.RealtorService;

import java.util.List;

@RestController
@RequestMapping("/api/realtors")
@RequiredArgsConstructor
public class RealtorController {

    private final RealtorService realtorService;
    private final PropertyService propertyService;

    @GetMapping
    public List<RealtorDTO> listRealtors() {
        return realtorService.getAllRealtors();
    }

    @GetMapping("/{id}")
    public RealtorDTO getOne(@PathVariable Long id) {
        return realtorService.getRealtorById(id);
    }

    @GetMapping("/{id}/properties")
    public List<PropertyBriefDTO> listRealtorProperties(@PathVariable Long id) {
        return propertyService.getPropertiesByRealtor(id);
    }

    @GetMapping("/{id}/reviews")
    public List<ReviewDTO> listRealtorReviews(@PathVariable Long id) {
        return realtorService.getReviewsByRealtor(id);
    }

    @PostMapping("/{id}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ReviewDTO addRealtorReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateReviewRequest req
    ) {
        return realtorService.addReview(
                id,
                user.getId(),
                req.getRating(),
                req.getComment()
        );
    }
}
