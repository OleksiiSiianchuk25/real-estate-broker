package ua.oleksii.realestatebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyBriefDTO {
    private Long id;
    private String title;
    private String price;
    private String imageUrl;
}
