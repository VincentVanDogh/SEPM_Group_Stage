package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record LocationDto(
    Long id,
    String venueName,
    Long addressId
) {
}
