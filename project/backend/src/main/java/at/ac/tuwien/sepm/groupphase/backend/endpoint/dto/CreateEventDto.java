package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.List;

public record CreateEventDto(

    @Null
    Long eventId,

    @Size(max = 255, message = "Event name must not exceed 255 characters")
    @NotBlank(message = "Event name must not be empty")
    String name,

    @Size(max = 4095, message = "Event description must not exceed 4095 characters")
    String description,

    @NotNull(message = "Event type must not be null")
    EventType type,

    @Min(1)
    @Max(527040) /* maximum is a year */
    @NotNull(message = "Event duration must not be null")
    int duration, /* given in minutes */

    @NotNull(message = "Event location must not be null")
    Long locationId,

    @Valid
    @NotEmpty
    List<ActDto> acts,

    @UniqueElements
    @NotEmpty
    List<Long> artistIds,

    List<int[]> pricesPerAct

) {
}
