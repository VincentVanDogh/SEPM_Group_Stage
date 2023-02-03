package at.ac.tuwien.sepm.groupphase.backend.unittests;

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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketAcquisitionRepositoryTest {

    @Autowired
    private TicketAcquisitionRepository ticketAcquisitionRepository;
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
        ticketAcquisitionRepository.deleteAll();
        ticketRepository.deleteAll();
    }

    @Test
    public void saveAndGetTASuccessfully() {
        TicketAcquisition created = createTestTA();
        TicketAcquisition saved = saveTAinRepo(created);

        Optional<TicketAcquisition> fetched = ticketAcquisitionRepository.findById(saved.getId());
        assertTrue(fetched.isPresent());
        if (fetched.isPresent()) {
            TicketAcquisition extracted = fetched.get();

            // LazyInitializationException workaround:
            List<Ticket> ticketsInTA = ticketRepository.findTicketsByTicketOrderId(extracted.getId());
            assertEquals(3, ticketsInTA.size());

            List<Ticket> ticketsCreated = created.getTickets();
            for (Ticket t : ticketsCreated) {
                // Tickets in created are in the TA fetched
                assertSame(t.getTicketOrder().getId(), extracted.getId());
            }
        }
    }

    @Test
    public void getNonexistingTARejected() {
        Optional<TicketAcquisition> fetched = ticketAcquisitionRepository.findById(10000L);
        assertTrue(fetched.isEmpty());
    }

    @Test
    @Transactional
    public void getAllFetchesAll() {
        ApplicationUser user1 = createAndSaveTestUser("test_user@email.com");

        TicketAcquisition ta1 = new TicketAcquisition();
        TicketAcquisition ta2 = new TicketAcquisition();
        TicketAcquisition ta3 = new TicketAcquisition();

        ta1.setBuyer(user1);
        ta2.setBuyer(user1);
        ta3.setBuyer(user1);
        ta1.setTickets(new ArrayList<>());
        ta2.setTickets(new ArrayList<>());
        ta3.setTickets(new ArrayList<>());
        ta1.setCancelled(false);
        ta2.setCancelled(false);
        ta3.setCancelled(false);

        TicketAcquisition saved1 = ticketAcquisitionRepository.save(ta1);
        TicketAcquisition saved2 = ticketAcquisitionRepository.save(ta2);
        TicketAcquisition saved3 = ticketAcquisitionRepository.save(ta3);

        List<TicketAcquisition> fetched = ticketAcquisitionRepository.findAll();

        assertAll(
            () -> assertEquals(3, fetched.size()),
            () -> assertTrue(containsTA(fetched, saved1)),
            () -> assertTrue(containsTA(fetched, saved2)),
            () -> assertTrue(containsTA(fetched, saved3))
        );
    }

    @Test
    @Transactional
    public void getAllForUserFetchesAllTAOfUser() {
        ApplicationUser user1 = createAndSaveTestUser("test_user@email.com");
        ApplicationUser user2 = createAndSaveTestUser("test_user2@email.com");

        TicketAcquisition ta1 = new TicketAcquisition();
        TicketAcquisition ta2 = new TicketAcquisition();
        TicketAcquisition ta3 = new TicketAcquisition();

        ta1.setBuyer(user1);
        ta2.setBuyer(user1);
        ta3.setBuyer(user2);
        ta1.setTickets(new ArrayList<>());
        ta2.setTickets(new ArrayList<>());
        ta3.setTickets(new ArrayList<>());
        ta1.setCancelled(false);
        ta2.setCancelled(false);
        ta3.setCancelled(false);

        TicketAcquisition saved1 = ticketAcquisitionRepository.save(ta1);
        TicketAcquisition saved2 = ticketAcquisitionRepository.save(ta2);
        TicketAcquisition saved3 = ticketAcquisitionRepository.save(ta3);

        List<TicketAcquisition> fetched = ticketAcquisitionRepository.findAllByBuyerId(user1.getId());

        assertAll(
            () -> assertEquals(2, fetched.size()),
            () -> assertTrue(containsTA(fetched, saved1)),
            () -> assertTrue(containsTA(fetched, saved2)),
            () -> assertFalse(containsTA(fetched, saved3))
        );
    }

    @Test
    public void cancelTASuccessfully() {
        TicketAcquisition saved = ticketAcquisitionRepository.save(createTestTA());

        List<Ticket> tickets = saved.getTickets();
        for (Ticket t : tickets) {
            t.setCancelled(true);
        }

        saved.setTickets(tickets);
        saved.setCancelled(true);

        TicketAcquisition updated = saveTAinRepo(saved);
        assertEquals(saved.getId(), updated.getId());

        Optional<TicketAcquisition> fetched = ticketAcquisitionRepository.findById(updated.getId());
        assertTrue(fetched.isPresent());
        if (fetched.isPresent()) {
            TicketAcquisition extracted = fetched.get();
            assertTrue(extracted.isCancelled());

            // LazyInitializationException workaround:
            List<Ticket> ticketsInTA = ticketRepository.findTicketsByTicketOrderId(extracted.getId());
            for (Ticket t : ticketsInTA) {
                assertTrue(t.isCancelled());
            }
        }
    }

    private TicketAcquisition saveTAinRepo(TicketAcquisition taToBeSaved) {
        // Way of saving that is in the service layer must be mimicked here too
        List<Ticket> ticketsToSave = taToBeSaved.getTickets();
        taToBeSaved.setTickets(new ArrayList<>());

        TicketAcquisition saved = ticketAcquisitionRepository.save(taToBeSaved);

        for (Ticket t : ticketsToSave) {
            t.setTicketOrder(saved);
            ticketRepository.save(t);
        }

        return saved;
    }

    private TicketAcquisition createTestTA() {
        TicketAcquisition ticketAcquisition = new TicketAcquisition();

        List<ApplicationUser> buyers = userRepository.findAll();
        if (!buyers.isEmpty()) {
            ticketAcquisition.setBuyer(buyers.get(0));
        }
        assertNotNull(ticketAcquisition.getBuyer());

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(createTestTicket());
        tickets.add(createTestTicket());
        tickets.add(createTestTicket());
        ticketAcquisition.setTickets(tickets);

        ticketAcquisition.setCancelled(false);

        return ticketAcquisition;
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

    private ApplicationUser createAndSaveTestUser(String email) {
        ApplicationUser testUser = new ApplicationUser();
        testUser.setLockedOut(false);
        testUser.setAdmin(false);
        testUser.setEmail(email);
        testUser.setPassword("password");
        testUser.setFirstName("Testttt");
        testUser.setLastName("Userrrrr");
        return userRepository.save(testUser);
    }

    private boolean containsTA(List<TicketAcquisition> tas, TicketAcquisition ta) {
        for (TicketAcquisition t : tas) {
            if (t.getId().equals(ta.getId())) {
                return true;
            }
        }
        return false;
    }

    private void insertTestData() {
        // Insert user
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("dsacsda@testy.com")
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
