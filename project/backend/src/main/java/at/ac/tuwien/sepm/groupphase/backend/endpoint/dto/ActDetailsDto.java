package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ActDetailsDto(
        Long id,

        @Future
        @NotNull(message = "Event date must not be null")
        LocalDateTime start,
        int nrTicketsReserved,
        int nrTicketsSold,

        @NotNull(message = "Stage id for act must not be null")
        Long stageId,

        EventDetailsDto event
) {
}
