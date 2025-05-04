package ua.oleksii.realestatebroker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PoiDto {
    private Long id;

    @NotNull @Size(max = 255)
    private String name;

    @NotNull @Size(max = 100)
    private String category;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String osmType;
    private Long osmId;

}
