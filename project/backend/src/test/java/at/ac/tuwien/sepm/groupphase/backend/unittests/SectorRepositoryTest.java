package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class SectorRepositoryTest implements TestData {

    @Autowired
    private SectorRepository sectorRepository;

    @BeforeEach
    public void beforeEach() {
        sectorRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenSaveSector_thenFindListWithOneElementAndFindSectorById() {
        Sector seatedSector = Sector.SectorBuilder.aSector()
            .withId(TEST_SEATED_SECTOR_ID)
            .withNumberOfSeats(TEST_SEATED_SECTOR_ROWS * TEST_SEATED_SECTOR_COLUMNS)
            .withNumberRows(TEST_SEATED_SECTOR_ROWS)
            .withNumberColumns(TEST_SEATED_SECTOR_ROWS)
            .withStanding(false)
            .withSectorMaps(new ArrayList<>())
            .build();

        sectorRepository.save(seatedSector);

        assertAll(
            () -> assertEquals(1, sectorRepository.findAll().size()),
            () -> assertNotNull(sectorRepository.findById(seatedSector.getId()))
        );
    }

}
