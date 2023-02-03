package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;

public record AddressDto(
        Long id,
        String street,
        String city,
        Long postalCode,
        String country
) {
}
