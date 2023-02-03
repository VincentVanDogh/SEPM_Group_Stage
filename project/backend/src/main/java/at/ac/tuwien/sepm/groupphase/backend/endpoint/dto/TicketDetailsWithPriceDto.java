package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;

import java.time.LocalDateTime;

public record TicketDetailsWithPriceDto(Long id,
    Long buyerId,
    ActDetailsDto act,
    Long sectorMapId,
    LocalDateTime creationDate,
    Integer seatNo,
    String ticketFirstName,
    String ticketLastName,
    TicketStatus reservation,
    boolean cancelled,
    Integer price) {
}
