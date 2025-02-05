package ua.oleksii.realestatebroker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.dto.FavoriteDTO;
import ua.oleksii.realestatebroker.model.Favorite;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.repository.FavoriteRepository;
import ua.oleksii.realestatebroker.service.UserService;
import ua.oleksii.realestatebroker.service.PropertyService;

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
    private PropertyService propertyService;

    public Favorite convertToEntity(FavoriteDTO favoriteDTO) {
        Optional<User> user = userService.findById(favoriteDTO.getUserId());
        Optional<Property> property = propertyService.getPropertyById(favoriteDTO.getPropertyId());

        if (user.isPresent() && property.isPresent()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user.get());
            favorite.setProperty(property.get());

            return favorite;
        }
        return null;
    }

    public List<FavoriteDTO> getAllFavorites() {
        List<Favorite> favorites = favoriteRepository.findAll();
        return favorites.stream()
                .map(favorite -> new FavoriteDTO(
                        favorite.getId(),
                        favorite.getUser().getId(),
                        favorite.getProperty().getId()
                ))
                .collect(Collectors.toList());
    }

    public FavoriteDTO addFavorite(FavoriteDTO favoriteDTO) {
        Favorite favorite = convertToEntity(favoriteDTO);

        if (favorite == null) {
            return null;
        }

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return new FavoriteDTO(
                savedFavorite.getUser().getId(),
                savedFavorite.getProperty().getId()
        );
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public Favorite addToFavorites(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    public void removeFromFavorites(Long id) {
        favoriteRepository.deleteById(id);
    }
}
