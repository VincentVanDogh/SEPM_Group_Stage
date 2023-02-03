package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StageDto;

import java.util.stream.Stream;

public interface StageService {

    /**
     * Get all stages by corresponding location ID form the persistence database.
     *
     * @param locationId ID of the corresponding location
     * @return Stream of StageDtos
     */
    Stream<StageDto> getByLocationId(Long locationId);

}
