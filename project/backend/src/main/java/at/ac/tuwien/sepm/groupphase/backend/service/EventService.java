package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchTop10Events;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

public interface EventService {

    /**
     * Save the given EventDto into the persistence layer, where it will be assigned a unique ID.
     *
     * @param eventDto Dto to be saved
     * @return Dto of the saved Event with ID
     */
    CreateEventDto save(CreateEventDto eventDto);

    /**
     * Get all events stored in the persistence database.
     *
     * @param page The page of events that shall be loaded
     * @return Stream of Dtos form the events saved
     */
    List<EventDetailsDto> getAll(int page);

    /**
     * Get the number of event pages in the repository.
     *
     * @return int representing the number of total event pages
     */
    int getNumberOfPages();

    /**
     * Get a specific event stored in the persistence database.
     *
     * @return Stream of Dtos from the events saved
     */
    Event getById(Long id);

    /**
     * Get all events matching the search criteria stored in the persistence database.
     *
     * @param searchParams The given search criteria
     * @param page         The page of events that shall be loaded
     * @return Stream of Dtos from the events saved
     * @throws ValidationException if DateFrom is before DateTo
     */
    List<EventDetailsDto> searchEvents(SearchEventDto searchParams, int page) throws ValidationException;

    /**
     * Get the number of events in the repository by search results.
     *
     * @param searchParams The given search criteria
     * @return int representing the number of events stored
     * @throws ValidationException if DateFrom is before DateTo
     */
    int getSearchSize(SearchEventDto searchParams) throws ValidationException;

    /**
     * Get Top 10 events by number of sold tickets from teh persistence database with or without a specific category.
     *
     * @param searchParams The criteria for the search
     * @return Stream of Dtos
     */
    Stream<EventDetailsDto> getTop10(SearchTop10Events searchParams) throws ValidationException;
}
