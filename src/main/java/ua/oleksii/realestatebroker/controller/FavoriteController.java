package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.FavoriteDTO;
import ua.oleksii.realestatebroker.model.Favorite;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavoritesForCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Favorite> favorites = favoriteService.getFavoritesByUser(user.getId());
        return ResponseEntity.ok(favorites);
    }


    @PostMapping
    public ResponseEntity<FavoriteDTO> addFavorite(
            @AuthenticationPrincipal User user,
            @RequestBody FavoriteDTO favoriteDTO) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        favoriteDTO.setUserId(user.getId());

        FavoriteDTO savedFavorite = favoriteService.addFavorite(favoriteDTO);
        if (savedFavorite == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(savedFavorite);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id) {
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.ok().build();
    }
}

