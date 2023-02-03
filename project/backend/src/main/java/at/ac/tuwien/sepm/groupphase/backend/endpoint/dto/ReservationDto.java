package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public record ReservationDto(Long reservationNo,
                              Long customerId,
                              List<TicketDetailsDto> tickets) { }
