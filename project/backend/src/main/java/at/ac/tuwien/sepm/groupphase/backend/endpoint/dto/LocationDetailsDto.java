package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;

public record LocationDetailsDto(
        Long id,
        String venueName,
        AddressDto address
) {
}
