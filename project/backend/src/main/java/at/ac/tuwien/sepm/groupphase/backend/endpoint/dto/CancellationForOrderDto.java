package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public record CancellationForOrderDto(
    Long cancellationId,
    TicketAcquisitionDetailsWithPricesDto ticketAcquisition,
    LocalDate cancellationDate
) {
}
