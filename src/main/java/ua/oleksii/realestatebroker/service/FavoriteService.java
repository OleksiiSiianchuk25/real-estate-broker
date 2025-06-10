package ua.oleksii.realestatebroker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.dto.FavoriteDTO;
import ua.oleksii.realestatebroker.model.Favorite;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.repository.FavoriteRepository;
import ua.oleksii.realestatebroker.repository.PropertyRepository;
import ua.oleksii.realestatebroker.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyRepository propertyRepository;

    public Favorite convertToEntity(FavoriteDTO favoriteDTO) {
        User user = userService.findById(favoriteDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Користувач не знайдений"));
        Property property = propertyRepository.findById(favoriteDTO.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Оголошення не знайдено"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        return favorite;
    }

    public List<FavoriteDTO> getAllFavorites() {
        List<Favorite> favorites = favoriteRepository.findAll();
        return favorites.stream()
                .map(fav -> new FavoriteDTO(
                        fav.getId(),
                        fav.getUser().getId(),
                        fav.getProperty().getId()
                ))
                .collect(Collectors.toList());
    }

    public FavoriteDTO addFavorite(FavoriteDTO favoriteDTO) {
        Favorite favorite = convertToEntity(favoriteDTO);
        Favorite saved = favoriteRepository.save(favorite);

        FavoriteDTO result = new FavoriteDTO();
        result.setId(saved.getId());
        result.setUserId(saved.getUser().getId());
        result.setPropertyId(saved.getProperty().getId());
        result.setCreatedAt(saved.getCreatedAt());
        return result;
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public void removeFromFavorites(Long id) {
        favoriteRepository.deleteById(id);
    }
}
