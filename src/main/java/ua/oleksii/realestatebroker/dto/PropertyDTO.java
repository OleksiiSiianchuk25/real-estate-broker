package ua.oleksii.realestatebroker.dto;

import lombok.Data;
import ua.oleksii.realestatebroker.model.Property;

import java.math.BigDecimal;

@Data
public class PropertyDTO {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private BigDecimal price;
    private Property.Type type;
    private Property.Status status;
    private String imageUrl;
    private Long realtorId;
    private String realtorFullName;
    private String realtorPhone;
    private String realtorEmail;
}
