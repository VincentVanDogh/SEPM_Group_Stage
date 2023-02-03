package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
public class EventRepositoryTest implements TestData {

    @Autowired
    private ActRepository actRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private StageTemplateRepository stageTemplateRepository;


    private final Artist artist1 = new Artist(FIRST_NAME, LAST_NAME, BAND_NAME, ARTIST_STAGE_NAME);
    private final Artist artist2 = new Artist(FIRST_NAME, LAST_NAME, BAND_NAME, ARTIST_STAGE_NAME);
    private final Address address = new Address(STREET, CITY, COUNTRY, POSTAL_CODE);
    private final Event event = new Event(TEST_EVENT_NAME, TEST_EVENT_DESCRIPTION, TEST_EVENT_TYPE, TEST_EVENT_DURATION);

    @BeforeEach
    public void beforeEach() {
        actRepository.deleteAll();
        eventRepository.deleteAll();
        addressRepository.deleteAll();
        locationRepository.deleteAll();
        artistRepository.deleteAll();
        stageRepository.deleteAll();
        stageTemplateRepository.deleteAll();


        Artist savedArtist1 = artistRepository.save(artist1);
        Artist savedArtist2 = artistRepository.save(artist2);
        List<Artist> artistList = new ArrayList<>();
        artistList.add(savedArtist1);
        artistList.add(savedArtist2);

        Address savedAddress = addressRepository.save(address);
        Location location1 = new Location(VENUE_NAME, savedAddress);

        Location savedLocation = locationRepository.save(location1);

        StageTemplate stageTemplate = StageTemplate.StageTemplateBuilder.aStage()
            .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
            .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
            .build();
        stageTemplateRepository.save(stageTemplate);

        Stage stage1 = Stage.StageBuilder.aStage()
            .withName(STAGE_NAME)
            .withStageTemplate(stageTemplate)
            .withLocation(location1).build();

        Stage savedStage1 = stageRepository.save(stage1);

        event.setName(TEST_EVENT_NAME);
        event.setLocation(savedLocation);
        event.setFeaturedArtists(artistList);
        List<Act> actList = new ArrayList<>();
        Act.ActBuilder actBuilder = Act.ActBuilder.anAct()
            .withEvent(event)
            .withStage(stageRepository.getReferenceById(savedStage1.getId()))
            .withStart(LocalDateTime.now().plusMinutes(10L))
            .withNrTicketsSold(0)
            .withNrTicketsReserved(0);
        actList.add(actBuilder.build());
        event.setActs(actList);

        Event savedEvent = eventRepository.save(event);

        event.setId(savedEvent.getId());
    }

    @Test
    @Transactional
    public void givenNothing_whenSaveEvent_thenFindListWithOneElementAndFindEventById() {

        assertAll(
            () -> assertEquals(1, eventRepository.findAll().size()),
            () -> assertNotNull(eventRepository.getReferenceById(event.getId()))
        );
    }

    @Test
    @Transactional
    public void givenOne_whenSearching_thenGetEventMatchingToSearchQuery() {
        eventRepository.save(event);


        LocalDateTime dateFrom = LocalDateTime.of(1970,1,1,0,0);
        LocalDateTime dateTo = LocalDateTime.of(3000,1,1,0,0);


        assertAll(
            () -> assertEquals(1, eventRepository.findBySearchWithoutCategory("TEST", dateFrom, dateTo,"", PageRequest.of(0, 10))
                .size())
        );
    }

}

