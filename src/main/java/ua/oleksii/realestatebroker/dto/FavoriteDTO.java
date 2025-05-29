package ua.oleksii.realestatebroker.dto;

import java.time.LocalDateTime;

/**
 * DTO для обраного оголошення з деталями властивості
 */
public class FavoriteDTO {
    private Long id;
    private Long userId;
    private Long propertyId;
    private LocalDateTime createdAt;
    private PropertyDTO property;  // Детальна інформація про властивість

    public FavoriteDTO() {
    }

    /**
     * для створення нової закладки
     */
    public FavoriteDTO(Long userId, Long propertyId) {
        this.userId = userId;
        this.propertyId = propertyId;
    }

    /**
     * для відображення існуючої закладки (без дати)
     */
    public FavoriteDTO(Long id, Long userId, Long propertyId) {
        this.id = id;
        this.userId = userId;
        this.propertyId = propertyId;
    }

    /**
     * повний конструктор з датою створення
     */
    public FavoriteDTO(Long id, Long userId, Long propertyId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.propertyId = propertyId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PropertyDTO getProperty() {
        return property;
    }

    public void setProperty(PropertyDTO property) {
        this.property = property;
    }
}
