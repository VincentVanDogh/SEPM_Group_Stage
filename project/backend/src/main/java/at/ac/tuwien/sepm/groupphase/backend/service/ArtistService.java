package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;

import java.util.stream.Stream;

public interface ArtistService {

    /**
     * Save the given ArtistDto into the persistence layer, where it is assigned a unique ID.
     *
     * @param artistDto Dto that is saved
     * @return Dto of the saved Artist with ID
     */
    ArtistDto save(ArtistDto artistDto);

    /**
     * Get all Artists from the persistence layer.
     *
     * @return Stream of artist dtos
     */
    Stream<ArtistDto> getAll();

    /**
     * Finds first 10 artists with matching names stored in the persistence database.
     *
     * @param searchParams contains the name to search after
     * @return Stream of Dtos containing the artists
     */
    Stream<ArtistDto> searchByName(String searchParams);
}
