package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.FavoriteDTO;
import ua.oleksii.realestatebroker.dto.PropertyDTO;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.FavoriteService;
import ua.oleksii.realestatebroker.service.PropertyService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final PropertyService propertyService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesForCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        // user гарантовано не null через PreAuthorize
        List<FavoriteDTO> dtos = favoriteService.getFavoritesByUser(user.getId())
                .stream()
                .map(fav -> {
                    FavoriteDTO dto = new FavoriteDTO();
                    dto.setId(fav.getId());
                    dto.setUserId(user.getId());
                    dto.setPropertyId(fav.getProperty().getId());
                    dto.setCreatedAt(fav.getCreatedAt());

                    // Отримуємо готовий PropertyDTO з рейтингами
                    PropertyDTO propertyDto = propertyService.getPropertyById(fav.getProperty().getId());
                    dto.setProperty(propertyDto);

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriteDTO> addFavorite(
            @AuthenticationPrincipal User user,
            @RequestBody FavoriteDTO favoriteDTO
    ) {
        favoriteDTO.setUserId(user.getId());
        FavoriteDTO saved = favoriteService.addFavorite(favoriteDTO);
        if (saved == null) {
            return ResponseEntity.badRequest().build();
        }

        PropertyDTO propertyDto = propertyService.getPropertyById(saved.getPropertyId());
        saved.setProperty(propertyDto);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.ok().build();
    }
}
