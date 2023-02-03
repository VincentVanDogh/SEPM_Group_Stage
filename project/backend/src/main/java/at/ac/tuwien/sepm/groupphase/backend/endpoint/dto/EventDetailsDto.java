package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;

import java.util.List;

public record EventDetailsDto(
        Long id,
        String name,
        String description,
        EventType type,
        int duration,
        LocationDetailsDto location,
        int totalNrOfTicketsSold,
        List<ArtistDto> featuredArtists
) {
}
