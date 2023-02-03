package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LocationMappingTest implements TestData {

    private final Address address = new Address(ID, STREET, CITY, COUNTRY, POSTAL_CODE);
    private final Location location = new Location(ID, VENUE_NAME, address);
    private final CreateNewStageDto createNewStageDto1 = new CreateNewStageDto(ID, ID, ARTIST_STAGE_NAME);
    private final CreateNewStageDto createNewStageDto2 = new CreateNewStageDto(ID+1, ID, STAGE_NAME);

    @Autowired
    private LocationMapper locationMapper;

    @Test
    public void givenNothing_whenMapCreateLocationDtoToLocation_thenLocationHasAllProperties() {

        List<CreateNewStageDto> createNewStageDtoList = new ArrayList<>();
        createNewStageDtoList.add(createNewStageDto1);
        createNewStageDtoList.add(createNewStageDto2);

        CreateLocationDto createLocationDto = locationMapper.locationAndStagesToCreateLocationDto(location, createNewStageDtoList);
        assertAll(
            () -> assertEquals(ID, createLocationDto.locationId()),
            () -> assertEquals(VENUE_NAME, createLocationDto.venueName()),
            () -> assertEquals(STREET, createLocationDto.street()),
            () -> assertEquals(CITY, createLocationDto.city()),
            () -> assertEquals(COUNTRY, createLocationDto.country()),
            () -> assertEquals(POSTAL_CODE, createLocationDto.postalCode()),
            () -> assertEquals(createNewStageDto1, createLocationDto.stages().get(0)),
            () -> assertEquals(createNewStageDto2, createLocationDto.stages().get(1))
        );
    }
}
