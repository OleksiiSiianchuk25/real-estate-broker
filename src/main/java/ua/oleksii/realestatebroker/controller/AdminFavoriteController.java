package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.FavoriteDTO;
import ua.oleksii.realestatebroker.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getAll() {
        List<FavoriteDTO> list = favoriteService.getAllFavorites();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.ok().build();
    }
}
