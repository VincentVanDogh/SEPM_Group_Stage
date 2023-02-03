package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;

import java.util.stream.Stream;

public interface LocationService {

    /**
     * Save the given CreateLocationDto into the persistence layer, where it will be assigned a unique ID.
     *
     * @param createLocationDto Dto to be saved
     * @return Dto of the saved Location with ID
     */
    CreateLocationDto save(CreateLocationDto createLocationDto);

    /**
     * Get all locations stored in the persistence database.
     *
     * @return Stream of Dtos containing the locations
     */
    Stream<LocationDto> getAll();

    /**
     * Finds a maximum of 10 locations with matching venue Names stored in the persistence database.
     *
     * @param searchParams contains the venue name to search after
     * @return Stream of Dtos containing the locations
     */
    Stream<LocationDto> searchByName(SearchLocationDto searchParams);
}
