package ua.oleksii.realestatebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealtorDTO {
    private Long id;
    private String fullName;
    private String agency;
    private double averageRating;
}
