package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchActDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

public interface ActService {

    /**
     * Get all acts stored in the persistence database with the corresponding foreign key eventId.
     *
     * @param eventId the id of the event of the acts
     * @return Stream of Dtos form the acts saved
     */
    Stream<ActDto> getByEventId(Long eventId);

    /**
     * Get all acts stored in the persistence database matching the event id and the search params.
     *
     * @param searchParams the search params to match the act
     *
     * @param eventId the id of the event of the acts
     * @return Stream of Dtos form the acts saved that match search criteria
     */
    Stream<ActDto> searchActsByEventId(Long eventId, SearchActDto searchParams) throws ValidationException;
}
