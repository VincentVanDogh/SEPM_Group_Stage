package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;

public record EventDto(

    Long id,
    String name,
    String description,
    EventType type,
    int duration,
    Long locationId
) {
}
