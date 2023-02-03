package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.List;

public record CreateLocationDto(

    @Null
    Long locationId,

    @NotEmpty(message = "Venue must not be empty")
    @Size(max = 255, message = "Venue name must not exceed 255 characters")
    String venueName,

    @NotEmpty(message = "Street must not be empty")
    @Size(max = 255, message = "Street name must not exceed 255 characters")
    String street,

    @NotEmpty(message = "City must not be empty")
    @Size(max = 255, message = "City name must not exceed 255 characters")
    String city,

    @NotEmpty(message = "Country must not be empty")
    @Size(max = 255, message = "Country name must not exceed 255 characters")
    String country,

    @NotNull(message = "Postal Code must not be empty")
    @Min(value = 100, message = "Postal Code must be inside of [100, 999999999]")
    @Max(999999999)
    int postalCode,
    @Valid
    List<CreateNewStageDto> stages

) {
}
