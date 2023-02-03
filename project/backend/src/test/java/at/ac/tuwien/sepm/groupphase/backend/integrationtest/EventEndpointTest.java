package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private StageTemplateRepository stageTemplateRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private SectorMapRepository sectorMapRepository;

    @Autowired
    private ActRepository actRepository;

    private static final Faker faker = new Faker(new Random(1));

    @BeforeEach
    @Transactional
    public void beforeEach() {
        actRepository.deleteAll();
        sectorMapRepository.deleteAll();
        sectorRepository.deleteAll();
        stageTemplateRepository.deleteAll();
        eventRepository.deleteAll();
        stageRepository.deleteAll();
        addressRepository.deleteAll();
        locationRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createWithValidInputReturnsOk() throws Exception {

        insertTestData();

        Location location = locationRepository.findAll().get(0);
        List<Stage> stages = stageRepository.findAllByLocationIdOrderById(location.getId());
        Stage stage1 = stages.get(0);
        Stage stage2 = stages.get(1);

        List<Artist> artists = artistRepository.findAll();
        Artist artist1 = artists.get(0);
        Artist artist2 = artists.get(1);


        mockMvc.perform(MockMvcRequestBuilders.post(EVENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "name": "Guns'n Roses concert",
                       "description": "Concert of Guns'n Roses",
                       "type": "CONCERT",
                       "duration": 60,
                       "locationId": %d,
                       "acts": [{
                            "start": "2023-10-12T23:04",
                            "stageId": "%d"
                       }, {
                            "start": "2023-10-12T23:04",
                            "stageId": "%d"
                       }],
                       "artistIds": [%d, %d],
                       "pricesPerAct": [[5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5],
                       [5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]]
                    }
                    """.formatted(location.getId(), stage1.getId(), stage2.getId(), artist1.getId(), artist2.getId()))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void createWithInValidInputReturnsBadRequest() throws Exception {

        insertTestData();

        Location location = locationRepository.findAll().get(0);
        List<Stage> stages = stageRepository.findAllByLocationIdOrderById(location.getId());
        Stage stage1 = stages.get(0);
        Stage stage2 = stages.get(1);

        List<Artist> artists = artistRepository.findAll();
        Artist artist1 = artists.get(0);
        Artist artist2 = artists.get(1);

        /* given nonexistent type */
        mockMvc.perform(MockMvcRequestBuilders.post(EVENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "name": "Guns'n Roses concert",
                       "description": "Concert of Guns'n Roses",
                       "type": "antything",
                       "duration": 60,
                       "locationId": %d,
                       "acts": [{
                            "start": "2023-10-12T23:04",
                            "stageId": "%d"
                       }, {
                            "start": "2023-10-12T23:04",
                            "stageId": "%d"
                       }],
                       "artistIds": [%d, %d],
                       "pricesPerAct": [[5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5],
                       [5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]]
                    }
                    """.formatted(location.getId(), stage1.getId(), stage2.getId(), artist1.getId(), artist2.getId()))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        /* date lies in the past */
        mockMvc.perform(MockMvcRequestBuilders.post(EVENT_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "name": "Guns'n Roses concert",
                       "description": "Concert of Guns'n Roses",
                       "type": "CONCERT",
                       "duration": 60,
                       "locationId": %d,
                       "acts": [{
                            "start": "2020-10-12T23:04",
                            "stageId": "%d"
                       }, {
                            "start": "2023-10-12T23:04",
                            "stageId": "%d"
                       }],
                       "artistIds": [%d, %d],
                       "pricesPerAct": [[5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5],
                       [5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]]
                    }
                    """.formatted(location.getId(), stage1.getId(), stage2.getId(), artist1.getId(), artist2.getId()))
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    private void insertTestData() {
        // Insert users
        /*userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("favsrvssa@testy.com")
            .withPassword(passwordEncoder.encode("12345678"))
            .withFirstName("Test")
            .withLastName("McTesty")
            .withTimesWrongPwEntered(0)
            .isLockedOut(false)
            .isAdmin(false)
            .build());*/

        // Insert sector
        sectorRepository.save(Sector.SectorBuilder.aSector()
            .withId(101L)
            .withNumberOfSeats(4 * 10)
            .withNumberRows(4)
            .withNumberColumns(10)
            .withStanding(false)
            .build());
        sectorRepository.save(Sector.SectorBuilder.aSector()
            .withId(102L)
            .withNumberOfSeats(2 * 20)
            .withNumberRows(2)
            .withNumberColumns(20)
            .withStanding(false)
            .build());
        sectorRepository.save(Sector.SectorBuilder.aSector()
            .withId(103L)
            .withNumberOfSeats(6 * 10)
            .withNumberRows(6)
            .withNumberColumns(10)
            .withStanding(false)
            .build());
        sectorRepository.save(Sector.SectorBuilder.aSector()
            .withId(104L)
            .withNumberOfSeats(3 * 20)
            .withNumberRows(3)
            .withNumberColumns(20)
            .withStanding(false)
            .build());

        // Insert stage plan
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(1L)
            .withName("Template Stage 1")
            .build();
        stageTemplateRepository.save(stage);
        linkSeatedSector(stage, (long) 101, Orientation.NORTH, 0, 1, 81);

        linkSeatedSector(stage, (long) 102, Orientation.NORTH, -1, 1, 41);
        linkSeatedSector(stage, (long) 102, Orientation.NORTH, 1, 1, 121);
        linkSeatedSector(stage, (long) 102, Orientation.NORTH, -2, 1, 1);
        linkSeatedSector(stage, (long) 102, Orientation.NORTH, 2, 1, 161);

        linkSeatedSector(stage, (long) 103, Orientation.NORTH, 0, 2, 301);

        linkSeatedSector(stage, (long) 104, Orientation.NORTH, -1, 2, 241);
        linkSeatedSector(stage, (long) 104, Orientation.NORTH, 1, 2, 361);
        linkSeatedSector(stage, (long) 102, Orientation.NORTH, -2, 2, 201);
        linkSeatedSector(stage, (long) 102, Orientation.NORTH, 2, 2, 421);

        // Insert address and location and stage
        Address address = new Address();
        address.setCountry("Austria");
        address.setCity("Wien");
        address.setPostalCode(1040);
        address.setStreet("Karlsplatz 13");

        Location location = new Location();
        location.setVenueName("TU Wien");

        address.setLocation(location);
        location.setAddress(address);

        Stage actualStage1 = Stage.StageBuilder.aStage()
            .withName("Stage 1")
            .withStageTemplate(stage)
            .withLocation(location)
            .build();

        Stage actualStage2 = Stage.StageBuilder.aStage()
            .withName("Stage 2")
            .withStageTemplate(stage)
            .withLocation(location)
            .build();

        addressRepository.save(address);
        locationRepository.save(location);
        stageRepository.save(actualStage1);
        stageRepository.save(actualStage2);

        // Insert artists
        for (int i = 0; i < 2; i++) {
            Artist artist = new Artist();
            artist.setFirstName(faker.name().firstName());
            artist.setLastName(faker.name().lastName());
            artistRepository.save(artist);
        }

        // Insert act and event
        /*Event event = new Event();
        event.setName("Event #1");
        event.setDescription("First ever event on the Ticketline webpage!");
        event.setType(EventType.CONCERT);
        event.setDuration(120);
        event.setLocation(locationRepository.findAll().get(0));
        event.setFeaturedArtists(artistRepository.findAll());
        event = eventRepository.save(event);

        Stage stageAct = stageRepository.findAll().get(0);

        Act act = generateAct(
            LocalDateTime.now().plusDays(20),
            0,
            0,
            stageAct,
            null,
            eventRepository.findAll().get(eventRepository.findAll().size() - 1)
        );*/

        // Insert act stage mapping
        /*List<ActSectorMapping> actSectorMappings = new ArrayList<>();
        List<Act> acts = actRepository.findAll();

        for (Act a : acts) {
            List<SectorMap> sectorMaps = sectorMapRepository.loadSectorMaps(a.getStage().getId());

            for (SectorMap sectorMap : sectorMaps) {
                ActSectorMapping actSectorMapping = ActSectorMapping.ActSectorMappingBuilder.aActSectorMap()
                    .withPrice(1000 + faker.number().positive() % 20000).withSectorMap(sectorMap).withAct(a).build();
                actSectorMappings.add(actSectorMapping);
            }
        }

        actSectorMappingRepository.saveAll(actSectorMappings);*/
    }

    private void linkSeatedSector(StageTemplate stageTemplate, Long sectorId, Orientation o, Integer x, Integer y, Integer firstSeatNr) {
        Sector sector;
        Optional<Sector> maybeSector = sectorRepository.findById(sectorId);
        if (maybeSector.isPresent()) {
            sector = maybeSector.get();
        } else {
            throw new RuntimeException(String.format("Failed to link seated sector. Could not find stage with id %s", 2));
        }
        SectorMap sectorMap = SectorMap.SectorMapBuilder.aSectorMap()
            .withSectorX(x)
            .withSectorY(y)
            .withOrientation(o)
            .withFirstSeatNr(firstSeatNr)
            .withStageTemplate(stageTemplate)
            .withSector(sector)
            .build();
        sectorMapRepository.save(sectorMap);

        List<SectorMap> sectorMaps = stageTemplate.getSectorMaps();
        if (sectorMaps == null) {
            sectorMaps = new ArrayList<>();
        }
        sectorMaps.add(sectorMap);
        stageTemplate.setSectorMaps(sectorMaps);
    }

    /*private Act generateAct(LocalDateTime start,
                            Integer nrTicketsReserved,
                            Integer nrTicketsSold,
                            Stage stage,
                            List<ActSectorMapping> sectorMaps,
                            Event event) {
        Act act = new Act();
        act.setStart(start);
        act.setNrTicketsReserved(nrTicketsReserved);
        act.setNrTicketsSold(nrTicketsSold);
        act.setStage(stage);
        act.setSectorMaps(sectorMaps);
        act.setEvent(event);
        return actRepository.save(act);
    }*/

}
