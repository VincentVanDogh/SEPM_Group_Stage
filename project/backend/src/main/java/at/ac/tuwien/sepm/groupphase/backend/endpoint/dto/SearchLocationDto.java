package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Size;

public record SearchLocationDto(

    @Size(max = 255, message = "Search term for location name must not exceed 255 characters")
    String venueName
) {
}
