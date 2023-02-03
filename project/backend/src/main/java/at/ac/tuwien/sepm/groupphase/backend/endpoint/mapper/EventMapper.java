package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface EventMapper {

    Event createEventDtoToEvent(CreateEventDto eventDto);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "name", source = "event.name")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "type", source = "event.type")
    @Mapping(target = "duration", source = "event.duration")
    @Mapping(target = "locationId", source = "event.location.id")
    @Mapping(target = "acts", source = "acts")
    @Mapping(target = "artistIds", source = "artistIds")
    CreateEventDto eventAndActsAndArtistsToCreateEventDto(Event event, List<ActDto> acts, List<Long> artistIds);

    @Mapping(target = "locationId", source = "event.location.id")
    EventDto eventToEventDto(Event event);

    EventDetailsDto eventToEventDetailsDto(Event event);

    EventDetailsDto eventToEventDetailsDto(Event event, int totalNrOfTicketsSold);
}
