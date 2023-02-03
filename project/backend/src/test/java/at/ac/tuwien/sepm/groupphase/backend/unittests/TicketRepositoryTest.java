package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketRepositoryTest implements TestData {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActRepository actRepository;
    @Autowired
    private SectorMapRepository sectorMapRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private StageTemplateRepository stageTemplateRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ActSectorMappingRepository actSectorMappingRepository;
    private final Faker faker = new Faker(new Random(1));

    @BeforeAll
    @Transactional
    public void beforeAll() {
        insertTestData();
    }

    @AfterAll
    @Transactional
    public void afterAll() {
        deleteTestData();
    }

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAll();
    }

    /**
     * Tests ability of repository to save a new ticket
     */
    @Test
    public void saveNewTicket() {
        Ticket provided = createTestTicket();
        Ticket returned = ticketRepository.save(provided);

        assertAll(
            () -> assertEquals(provided.getBuyer().getId(), returned.getBuyer().getId()),
            () -> assertEquals(provided.getTicketOrder(), returned.getTicketOrder()),
            () -> assertEquals(provided.getAct().getId(), returned.getAct().getId()),
            () -> assertEquals(provided.getSectorMap().getId(), returned.getSectorMap().getId()),
            () -> assertEquals(provided.getCreationDate(), returned.getCreationDate()),
            () -> assertEquals(provided.getTicketFirstName(), returned.getTicketFirstName()),
            () -> assertEquals(provided.getTicketLastName(), returned.getTicketLastName()),
            () -> assertEquals(provided.getSeatNo(), returned.getSeatNo()),
            () -> assertEquals(provided.getReservation(), returned.getReservation()),
            () -> assertEquals(provided.isCancelled(), returned.isCancelled())
        );
    }

    /**
     * Tests ability of repository to update already saved tickets
     */
    @Test
    public void updateTicket() {
        Ticket provided = createTestTicket();
        Ticket temp = ticketRepository.save(provided);

        provided.setId(temp.getId());
        provided.setTicketFirstName("Changed");
        provided.setTicketLastName("Name");
        Ticket returned = ticketRepository.save(provided);

        assertAll(
            () -> assertEquals(provided.getId(), returned.getId()),
            () -> assertEquals(provided.getBuyer().getId(), returned.getBuyer().getId()),
            () -> assertEquals(provided.getTicketOrder(), returned.getTicketOrder()),
            () -> assertEquals(provided.getAct().getId(), returned.getAct().getId()),
            () -> assertEquals(provided.getSectorMap().getId(), returned.getSectorMap().getId()),
            () -> assertEquals(provided.getCreationDate(), returned.getCreationDate()),
            () -> assertEquals(provided.getTicketFirstName(), returned.getTicketFirstName()),
            () -> assertEquals(provided.getTicketLastName(), returned.getTicketLastName()),
            () -> assertEquals(provided.getSeatNo(), returned.getSeatNo()),
            () -> assertEquals(provided.getReservation(), returned.getReservation()),
            () -> assertEquals(provided.isCancelled(), returned.isCancelled())
        );
    }

    /**
     * Tests ability of repository to successfully delete tickets
     */
    @Test
    public void deleteTicket() {
        Ticket provided = createTestTicket();
        Ticket returned = ticketRepository.save(provided);

        Ticket fetched = ticketRepository.findTicketById(returned.getId());
        assertNotNull(fetched);

        ticketRepository.deleteById(returned.getId());
        Ticket fetched2 = ticketRepository.findTicketById(returned.getId());
        assertNull(fetched2);
    }

    /**
     * Tests ability of repository to fetch tickets by ID number
     */
    @Test
    public void fetchTicketById() {
        Ticket provided = createTestTicket();
        Ticket returned = ticketRepository.save(provided);

        Ticket fetched = ticketRepository.findTicketById(returned.getId());

        assertAll(
            () -> assertEquals(returned.getId(), fetched.getId()),
            () -> assertEquals(returned.getBuyer().getId(), fetched.getBuyer().getId()),
            () -> assertEquals(returned.getTicketOrder(), fetched.getTicketOrder()),
            () -> assertEquals(returned.getAct().getId(), fetched.getAct().getId()),
            () -> assertEquals(returned.getSectorMap().getId(), fetched.getSectorMap().getId()),
            () -> assertEquals(returned.getCreationDate().toString().substring(0,23), fetched.getCreationDate().toString().substring(0,23)), // Time is rounded in database, will never be true otherwise
            () -> assertEquals(returned.getTicketFirstName(), fetched.getTicketFirstName()),
            () -> assertEquals(returned.getTicketLastName(), fetched.getTicketLastName()),
            () -> assertEquals(returned.getSeatNo(), fetched.getSeatNo()),
            () -> assertEquals(returned.getReservation(), fetched.getReservation()),
            () -> assertEquals(returned.isCancelled(), fetched.isCancelled())
        );
    }

    /**
     * Tests ability of repository to fetch all tickets saved in repository
     */
    @Test
    public void fetchAllTickets() {
        Ticket returned1 = ticketRepository.save(createTestTicket());
        Ticket returned2 = ticketRepository.save(createTestTicket());
        Ticket returned3 = ticketRepository.save(createTestTicket());

        List<Ticket> ticketsSaved = ticketRepository.findAll();

        returned1.setCreationDate(ticketRepository.findTicketById(returned1.getId()).getCreationDate());

        assertAll(
            () -> assertEquals(ticketsSaved.size(), 3),
            () -> assertTrue(ticketIncludedInList(returned1, ticketsSaved)),
            () -> assertTrue(ticketIncludedInList(returned2, ticketsSaved)),
            () -> assertTrue(ticketIncludedInList(returned3, ticketsSaved))
        );
    }

    /**
     * Tests ability of repository to fetch all valid tickets. Valid is understood as not being bought (being initialised)
     * and the creation date of the ticket being within 15 minutes of the current time
     */
    @Test
    public void fetchAllValidTickets() {
        Ticket valid1 = ticketRepository.save(createTestTicket());
        Ticket valid2 = ticketRepository.save(createTestTicket());
        Ticket valid3 = ticketRepository.save(createTestTicket());
        Ticket valid4 = ticketRepository.save(createTestTicket());
        Ticket invalid1 = ticketRepository.save(createTestTicket(LocalDateTime.now().minus(24, ChronoUnit.HOURS)));
        Ticket invalid2 = ticketRepository.save(createTestTicket(LocalDateTime.now().minus(24, ChronoUnit.DAYS)));
        Ticket invalid3 = ticketRepository.save(createTestTicket(LocalDateTime.now().minus(24, ChronoUnit.YEARS)));
        Ticket invalid4 = ticketRepository.save(createTestTicket(LocalDateTime.now().minus(24, ChronoUnit.MINUTES)));

        List<ApplicationUser> buyers = userRepository.findAll();
        ApplicationUser buyer = buyers.get(0);
        assertNotNull(buyer);

        List<Ticket> fetched =
            ticketRepository.findTicketsByBuyerIdAndTicketOrderIsNullAndReservationNotAndCreationDateIsAfter(buyer.getId(),
                TicketStatus.RESERVED, LocalDateTime.now().minus(15, ChronoUnit.MINUTES));

        assertAll(
            () -> assertEquals(fetched.size(), 4),
            () -> assertTrue(ticketIncludedInList(valid1, fetched)),
            () -> assertTrue(ticketIncludedInList(valid2, fetched)),
            () -> assertTrue(ticketIncludedInList(valid3, fetched)),
            () -> assertTrue(ticketIncludedInList(valid4, fetched)),
            () -> assertFalse(ticketIncludedInList(invalid1, fetched)),
            () -> assertFalse(ticketIncludedInList(invalid2, fetched)),
            () -> assertFalse(ticketIncludedInList(invalid3, fetched)),
            () -> assertFalse(ticketIncludedInList(invalid4, fetched))
        );
    }

    private Ticket createTestTicket() {
        Ticket ticket = new Ticket();

        List<ApplicationUser> buyers = userRepository.findAll();
        if (!buyers.isEmpty()) {
            ticket.setBuyer(buyers.get(0));
        }
        assertNotNull(ticket.getBuyer());

        List<Act> acts = actRepository.findAll();
        if (!acts.isEmpty()) {
            ticket.setAct(acts.get(0));
        }
        assertNotNull(ticket.getAct());

        List<SectorMap> sectorMaps = sectorMapRepository.findAll();
        if (!sectorMaps.isEmpty()) {
            ticket.setSectorMap(sectorMaps.get(0));
        }
        assertNotNull(ticket.getSectorMap());

        ticket.setCreationDate(LocalDateTime.now());
        ticket.setTicketFirstName("Test");
        ticket.setTicketLastName("User");
        ticket.setSeatNo(1);
        ticket.setReservation(TicketStatus.INITIALISED);
        ticket.setCancelled(false);

        return ticket;
    }

    private Ticket createTestTicket(LocalDateTime creationDate) {
        Ticket ticket = createTestTicket();
        ticket.setCreationDate(creationDate);
        return ticket;
    }

    private Ticket createTestTicket(ApplicationUser buyer, Act act) {
        Ticket ticket = createTestTicket();
        ticket.setBuyer(buyer);
        ticket.setAct(act);
        return ticket;
    }

    private boolean ticketIncludedInList(Ticket ticket, List<Ticket> list) {
        for (Ticket t : list) {
            if (Objects.equals(t.getId(), ticket.getId()) &&
                Objects.equals(t.getBuyer().getId(), ticket.getBuyer().getId()) &&
                Objects.equals(t.getTicketOrder(), ticket.getTicketOrder()) &&
                Objects.equals(t.getAct().getId(), ticket.getAct().getId()) &&
                Objects.equals(t.getSectorMap().getId(), ticket.getSectorMap().getId()) &&
                Objects.equals(t.getCreationDate().toString().substring(0,23), ticket.getCreationDate().toString().substring(0,23)) && // Time is rounded in database, will never be true otherwise
                Objects.equals(t.getTicketFirstName(), ticket.getTicketFirstName()) &&
                Objects.equals(t.getTicketLastName(), ticket.getTicketLastName()) &&
                Objects.equals(t.getSeatNo(), ticket.getSeatNo()) &&
                Objects.equals(t.getReservation(), ticket.getReservation()) &&
                Objects.equals(t.isCancelled(), ticket.isCancelled())) {
                return true;
            }
        }
        return false;
    }

    private void insertTestData() {
        // Insert user
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("nhgdnmhnd@testy.com")
            .withPassword(passwordEncoder.encode("12345678"))
            .withFirstName("Test")
            .withLastName("McTesty")
            .withTimesWrongPwEntered(0)
            .isLockedOut(false)
            .isAdmin(false)
            .build());

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

        Stage actualStage = Stage.StageBuilder.aStage()
            .withName("Stage 1")
            .withStageTemplate(stage)
            .withLocation(location)
            .build();
        addressRepository.save(address);
        locationRepository.save(location);
        stageRepository.save(actualStage);

        // Insert artist
        Artist artist = new Artist();
        artist.setFirstName("David");
        artist.setLastName("Guetta");
        artistRepository.save(artist);

        // Insert act and event
        Event event = new Event();
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
        );

        // Insert act stage mapping
        List<ActSectorMapping> actSectorMappings = new ArrayList<>();
        List<Act> acts = actRepository.findAll();

        for (Act a : acts) {
            List<SectorMap> sectorMaps = sectorMapRepository.loadSectorMaps(a.getStage().getId());

            for (SectorMap sectorMap : sectorMaps) {
                ActSectorMapping actSectorMapping = ActSectorMapping.ActSectorMappingBuilder.aActSectorMap()
                    .withPrice(1000 + faker.number().positive() % 20000).withSectorMap(sectorMap).withAct(a).build();
                actSectorMappings.add(actSectorMapping);
            }
        }

        actSectorMappingRepository.saveAll(actSectorMappings);
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

    private Act generateAct(LocalDateTime start,
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
    }

    private void deleteTestData() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        actRepository.deleteAll();
        sectorMapRepository.deleteAll();
        sectorRepository.deleteAll();
        stageTemplateRepository.deleteAll();
        addressRepository.deleteAll();
        locationRepository.deleteAll();
        stageRepository.deleteAll();
        artistRepository.deleteAll();
        eventRepository.deleteAll();
        actSectorMappingRepository.deleteAll();
    }
}
