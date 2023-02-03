package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActSectorMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class ActRepositoryTest implements TestData {

    @Autowired
    private ActRepository actRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StageTemplateRepository stageTemplateRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private SectorMapRepository sectorMapRepository;

    @Autowired
    private ActSectorMappingRepository actSectorMappingRepository;

    private final Event event = new Event(TEST_EVENT_NAME, TEST_EVENT_DESCRIPTION, TEST_EVENT_TYPE, TEST_EVENT_DURATION);
    private final Act act = new Act();

    @BeforeEach
    public void beforeEach() {
        actRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        stageTemplateRepository.deleteAll();

        Location location = new Location();
        location.setVenueName(TEST_VENUE_NAME);
        locationRepository.save(location);

        event.setLocation(location);
        eventRepository.save(event);

        Sector seatedSector = Sector.SectorBuilder.aSector()
            .withId(TEST_SEATED_SECTOR_ID)
            .withNumberOfSeats(TEST_SEATED_SECTOR_ROWS * TEST_SEATED_SECTOR_COLUMNS)
            .withNumberRows(TEST_SEATED_SECTOR_ROWS)
            .withNumberColumns(TEST_SEATED_SECTOR_ROWS)
            .withStanding(false)
            .withSectorMaps(new ArrayList<>())
            .build();

        sectorRepository.save(seatedSector);

        SectorMap sectorMap = SectorMap.SectorMapBuilder.aSectorMap()
            .withSectorX(1)
            .withSectorY(1)
            .withFirstSeatNr(1)
            .withSector(seatedSector)
            .withOrientation(Orientation.NORTH)
            .build();

        sectorMapRepository.save(sectorMap);

        List<SectorMap> sectorMaps = new ArrayList<>();
        sectorMaps.add(sectorMap);

        ActSectorMapping actSectorMapping = ActSectorMapping.ActSectorMappingBuilder.aActSectorMap()
            .withPrice(500)
            .withSectorMap(sectorMap)
            .build();

        actSectorMappingRepository.save(actSectorMapping);

        List<ActSectorMapping> actSectorMappings = new ArrayList<>();
        actSectorMappings.add(actSectorMapping);


        StageTemplate stageTemplate = StageTemplate.StageTemplateBuilder.aStage()
            .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
            .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
            .withSectorMaps(sectorMaps)
            .build();

        stageTemplateRepository.save(stageTemplate);

        List<Act> actList = new ArrayList<>();

        act.setEvent(eventRepository.findAll().get(0));
        act.setNrTicketsSold(TEST_NR_TICKETS_SOLD);
        act.setNrTicketsReserved(TEST_NR_TICKETS_RESERVED);
        act.setStart(LocalDateTime.now().plusYears(1L));
        act.setSectorMaps(actSectorMappings);

        Act savedAct = actRepository.save(act);
        act.setId(savedAct.getId());
        actList.add(act);
        event.setActs(actList);
        eventRepository.save(event);
    }

    @Test
    @Transactional
    public void givenNothing_whenSaveAct_thenFindListWithOneElementAndFindActById() {


        assertAll(
            () -> assertEquals(1, actRepository.findAll().size()),
            () -> assertNotNull(actRepository.findById(act.getId()))
        );
    }

    /*@Test
    @Transactional
    public void givenOne_whenSearchingActsWithMatchingQuery_thenFindListWithElements() {

        eventRepository.save(event);
        actRepository.save(act);


        assertAll(
            () -> assertEquals(1, actRepository.findActsByEventIdAndSearchParams(eventRepository.findAll().get(0).getId(),
                LocalDateTime.of(1970, 1, 1, 0, 0, 0),
                LocalDateTime.of(3000, 1, 1, 0, 0, 0),
                0L,
                3000L).size())
        );
    }*/

    @Test
    @Transactional
    public void givenOne_whenSearchingActsWithNonMatchingQuery_thenFindEmptyList() {

        assertAll(
            () -> assertEquals(0, actRepository.findActsByEventIdAndSearchParams(eventRepository.findAll().get(0).getId(),
                LocalDateTime.of(1970, 1, 1, 0, 0, 0),
                LocalDateTime.MAX,
                0L,
                0L).size())
        );
    }

}
