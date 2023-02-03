package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public record TicketAcquisitionDetailsWithPricesDto(
    Long id,
    Long buyerId,
    List<TicketDetailsDto> tickets,
    boolean cancelled) {
}
