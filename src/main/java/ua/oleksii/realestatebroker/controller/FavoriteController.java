// src/main/java/ua/oleksii/realestatebroker/controller/FavoriteController.java
package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final PropertyService propertyService;

    /** Повернути всі закладки поточного користувача як DTO з деталями */
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getFavoritesForCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<FavoriteDTO> dtos = favoriteService.getFavoritesByUser(user.getId())
                .stream()
                .map(fav -> {
                    FavoriteDTO dto = new FavoriteDTO();
                    dto.setId(fav.getId());
                    dto.setUserId(fav.getUser().getId());
                    dto.setPropertyId(fav.getProperty().getId());
                    dto.setCreatedAt(fav.getCreatedAt());
                    // Підкладаємо детальний PropertyDTO
                    PropertyDTO p = propertyService.convertToDTO(fav.getProperty());
                    dto.setProperty(p);
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** Додати оголошення в обране поточного користувача */
    @PostMapping
    public ResponseEntity<FavoriteDTO> addFavorite(
            @AuthenticationPrincipal User user,
            @RequestBody FavoriteDTO favoriteDTO
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        favoriteDTO.setUserId(user.getId());
        FavoriteDTO saved = favoriteService.addFavorite(favoriteDTO);
        if (saved == null) {
            return ResponseEntity.badRequest().build();
        }
        // Підкладаємо подробиці властивості
        PropertyDTO p = propertyService.convertToDTO(
                propertyService.getPropertyById(saved.getPropertyId())
        );
        saved.setProperty(p);
        return ResponseEntity.ok(saved);
    }

    /** Видалити закладку за її ID */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.ok().build();
    }
}
