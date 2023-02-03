package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

public record ArtistDto(
    @Null
    Long id,

    @Size(max = 255, message = "Artist first name must not exceed 255 characters")
    String firstName,


    @Size(max = 255, message = "Artist last name must not exceed 255 characters")
    String lastName,

    @Size(max = 255, message = "Artist band name must not exceed 255 characters")
    String bandName,

    @NotEmpty(message = "Stage name for artist must not be empty")
    @Size(max = 255, message = "Artist stage name must not exceed 255 characters")
    String stageName

) {
}
