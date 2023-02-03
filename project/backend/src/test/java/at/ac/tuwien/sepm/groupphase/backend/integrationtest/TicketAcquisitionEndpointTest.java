package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketAcquisitionEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketAcquisitionRepository ticketAcquisitionRepository;
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

    private String token = "";

    /**
     * Before each test, we need to make sure that the Bearer token is valid, so we attempt a login. On failure, we
     * register a new user. The ticket database is also cleared before each test.
     *
     * @throws Exception
     */
    @BeforeEach
    @Transactional
    public void beforeEach() throws Exception {
        // Authenticate or register
        MvcResult resultLogin = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"password"
                                }
                                """)
            .accept(MediaType.APPLICATION_JSON)).andReturn();
        if (resultLogin.getResponse().getStatus() != 200) {
            MvcResult resultRegister = mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"password",
                                   "confirmation":"password",
                                   "firstName": "first_name",
                                   "lastName":"last_name"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
            if (resultRegister.getResponse().getStatus() == 201) {
                token = resultRegister.getResponse().getContentAsString();
            }
        } else {
            token = resultLogin.getResponse().getContentAsString();
        }
        // Clean tickets and ticket acquisitions
        ticketAcquisitionRepository.deleteAll();
        ticketRepository.deleteAll();
    }

    @Test
    @Transactional
    public void saveNewTAWithNewTickets() throws Exception {
        Ticket ticket1 = createTestTicket();
        Ticket ticket2 = createTestTicket();
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");
        assertNotNull(buyer);
        ticket1.setBuyer(buyer);
        ticket2.setBuyer(buyer);
        ticket1.setReservation(TicketStatus.PURCHASED);
        ticket2.setReservation(TicketStatus.PURCHASED);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(TICKET_ACQUISITION_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{" +
                        "\"tickets\": [" +
                        "{" +
                        "\"buyerId\": " + ticket1.getBuyer().getId() + "," +
                        "\"actId\": " + ticket1.getAct().getId() + "," +
                        "\"sectorMapId\": " + ticket1.getSectorMap().getId() + "," +
                        "\"ticketFirstName\": \"" + ticket1.getTicketFirstName() + "\"," +
                        "\"ticketLastName\": \"" + ticket1.getTicketLastName() + "\"," +
                        "\"seatNo\": " + ticket1.getSeatNo() + "," +
                        "\"reservation\": \"" + ticket1.getReservation().toString() + "\"," +
                        "\"cancelled\": " + ticket1.isCancelled() +
                        "}," +
                        "{" +
                        "\"buyerId\": " + ticket2.getBuyer().getId() + "," +
                        "\"actId\": " + ticket2.getAct().getId() + "," +
                        "\"sectorMapId\": " + ticket2.getSectorMap().getId() + "," +
                        "\"ticketFirstName\": \"" + ticket2.getTicketFirstName() + "\"," +
                        "\"ticketLastName\": \"" + ticket2.getTicketLastName() + "\"," +
                        "\"seatNo\": " + ticket2.getSeatNo() + "," +
                        "\"reservation\": \"" + ticket2.getReservation().toString() + "\"," +
                        "\"cancelled\": " + ticket1.isCancelled() +
                        "}]," +
                        "\"cancelled\": false" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn().getResponse();

        Long taId = Long.parseLong(response.getContentAsString().substring(6,7));
        Optional<TicketAcquisition> taFetched = ticketAcquisitionRepository.findById(taId);
        assertTrue(taFetched.isPresent());
        if (taFetched.isPresent()) {
            TicketAcquisition taExtracted = taFetched.get();

            // LazyInitializationException workaround:
            List<Ticket> ticketsInTA = ticketRepository.findTicketsByTicketOrderId(taExtracted.getId());

            assertAll(
                () -> assertEquals(2, ticketsInTA.size()),
                () -> assertEquals(ticketsInTA.get(0).getTicketFirstName(), ticket1.getTicketFirstName()),
                () -> assertEquals(ticketsInTA.get(1).getTicketFirstName(), ticket2.getTicketFirstName())
            );
        }
    }

    @Test
    @Transactional
    public void saveNewTAWithExistingTickets() throws Exception {
        Ticket t1 = createTestTicket();
        Ticket t2 = createTestTicket();
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");
        t1.setBuyer(buyer);
        t2.setBuyer(buyer);
        Ticket ticket1 = ticketRepository.save(t1);
        Ticket ticket2 = ticketRepository.save(t2);

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_ACQUISITION_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{" +
                        "\"tickets\": [" +
                        "{" +
                        "\"id\": " + ticket1.getId() + "," +
                        "\"buyerId\": " + ticket1.getBuyer().getId() + "," +
                        "\"actId\": " + ticket1.getAct().getId() + "," +
                        "\"sectorMapId\": " + ticket1.getSectorMap().getId() + "," +
                        "\"ticketFirstName\": \"" + ticket1.getTicketFirstName() + "\"," +
                        "\"ticketLastName\": \"" + ticket1.getTicketLastName() + "\"," +
                        "\"seatNo\": " + ticket1.getSeatNo() + "," +
                        "\"reservation\": \"" + ticket1.getReservation().toString() + "\"," +
                        "\"cancelled\": " + ticket1.isCancelled() +
                        "}," +
                        "{" +
                        "\"id\": " + ticket2.getId() + "," +
                        "\"buyerId\": " + ticket2.getBuyer().getId() + "," +
                        "\"actId\": " + ticket2.getAct().getId() + "," +
                        "\"sectorMapId\": " + ticket2.getSectorMap().getId() + "," +
                        "\"ticketFirstName\": \"" + ticket2.getTicketFirstName() + "\"," +
                        "\"ticketLastName\": \"" + ticket2.getTicketLastName() + "\"," +
                        "\"seatNo\": " + ticket2.getSeatNo() + "," +
                        "\"reservation\": \"" + ticket2.getReservation().toString() + "\"," +
                        "\"cancelled\": " + ticket1.isCancelled() +
                        "}]," +
                        "\"cancelled\": false" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void rejectSavingTAWithNoTickets() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_ACQUISITION_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{" +
                        "\"tickets\": []," +
                        "\"cancelled\": false" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void cancelExistingTASuccessfully() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");
        TicketAcquisition ta = new TicketAcquisition();
        ta.setBuyer(buyer);
        ta.setCancelled(false);

        Ticket t1 = createTestTicket();
        Ticket t2 = createTestTicket();
        t1.setBuyer(buyer);
        t2.setBuyer(buyer);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(t1);
        tickets.add(t2);
        ta.setTickets(tickets);

        TicketAcquisition taSaved = ticketAcquisitionRepository.save(ta);
        t1.setTicketOrder(taSaved);
        t2.setTicketOrder(taSaved);
        ticketRepository.save(t1);
        ticketRepository.save(t2);

        mockMvc.perform(MockMvcRequestBuilders.delete(TICKET_ACQUISITION_BASE_URI + "/" + taSaved.getId())
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        Optional<TicketAcquisition> taFetchedOpt = ticketAcquisitionRepository.findById(taSaved.getId());
        assertTrue(taFetchedOpt.isPresent());
        if (taFetchedOpt.isPresent()) {
            TicketAcquisition taFetched = taFetchedOpt.get();

            // Lazy Initialization workaround:
            List<Ticket> ticketsInTa = ticketRepository.findTicketsByTicketOrderId(taFetched.getId());

            assertAll(
                () -> assertEquals(taFetched.getId(), taSaved.getId()),
                () -> assertEquals(ticketsInTa.size(), 2),
                () -> assertTrue(taFetched.isCancelled()),
                () -> assertTrue(ticketsInTa.get(0).isCancelled()),
                () -> assertTrue(ticketsInTa.get(1).isCancelled())
            );
        }

    }

    @Test
    @Transactional
    public void getSpecificTASuccesfully() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");
        TicketAcquisition ta = new TicketAcquisition();
        ta.setBuyer(buyer);
        ta.setCancelled(false);

        Ticket t1 = createTestTicket();
        Ticket t2 = createTestTicket();
        t1.setBuyer(buyer);
        t2.setBuyer(buyer);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(t1);
        tickets.add(t2);
        ta.setTickets(tickets);

        TicketAcquisition taSaved = ticketAcquisitionRepository.save(ta);

        t1.setTicketOrder(taSaved);
        t2.setTicketOrder(taSaved);
        ticketRepository.save(t1);
        ticketRepository.save(t2);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_ACQUISITION_BASE_URI + "/" + taSaved.getId())
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        // LazyInitializationException workaround:
        List<Ticket> ticketsInTA = ticketRepository.findTicketsByTicketOrderId(taSaved.getId());

        assertAll(
            () -> assertTrue(response.getContentAsString().contains("\"id\":" + taSaved.getId())),
            () -> assertTrue(response.getContentAsString().contains("\"id\":" + ticketsInTA.get(0).getId())),
            () -> assertTrue(response.getContentAsString().contains("\"id\":" + ticketsInTA.get(1).getId()))
        );
    }

    @Test
    @Transactional
    public void NonExistentTANotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TICKET_ACQUISITION_BASE_URI + "/10000")
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void getAllTABelongingToUser() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");
        TicketAcquisition ta = new TicketAcquisition();
        ta.setBuyer(buyer);
        ta.setCancelled(false);

        Ticket t1 = createTestTicket();
        Ticket t2 = createTestTicket();
        t1.setBuyer(buyer);
        t2.setBuyer(buyer);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(t1);
        tickets.add(t2);
        ta.setTickets(tickets);

        TicketAcquisition taB = new TicketAcquisition();
        taB.setBuyer(buyer);
        taB.setCancelled(false);

        Ticket t1b = createTestTicket();
        Ticket t2b = createTestTicket();
        t1b.setBuyer(buyer);
        t2b.setBuyer(buyer);
        List<Ticket> ticketsB = new ArrayList<>();
        ticketsB.add(t1b);
        ticketsB.add(t2b);
        taB.setTickets(ticketsB);

        TicketAcquisition taSaved1 = ticketAcquisitionRepository.save(ta);
        t1.setTicketOrder(taSaved1);
        t2.setTicketOrder(taSaved1);
        Ticket t1Saved = ticketRepository.save(t1);
        Ticket t2Saved = ticketRepository.save(t2);

        TicketAcquisition taSaved2 = ticketAcquisitionRepository.save(taB);
        t1b.setTicketOrder(taSaved2);
        t2b.setTicketOrder(taSaved2);
        Ticket t1bSaved = ticketRepository.save(t1b);
        Ticket t2bSaved = ticketRepository.save(t2b);

        ApplicationUser buyer2 = userRepository.findApplicationUserByEmail("favsrvssa@testy.com");

        TicketAcquisition ta2 = new TicketAcquisition();
        ta2.setBuyer(buyer2);
        ta2.setCancelled(false);

        Ticket t3 = createTestTicket();
        Ticket t4 = createTestTicket();
        t3.setBuyer(buyer2);
        t4.setBuyer(buyer2);
        List<Ticket> tickets2 = new ArrayList<>();
        tickets2.add(t3);
        tickets2.add(t4);
        ta2.setTickets(tickets2);

        TicketAcquisition taSaved3 = ticketAcquisitionRepository.save(ta2);
        t3.setTicketOrder(taSaved3);
        t4.setTicketOrder(taSaved3);
        Ticket t3Saved = ticketRepository.save(t3);
        Ticket t4Saved = ticketRepository.save(t4);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_ACQUISITION_BASE_URI + "/overview")
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertAll(
            () -> assertTrue(response.getContentAsString().contains("[{\"id\":" + taSaved1.getId() + ",\"buyerId\":" +
                taSaved1.getBuyer().getId() + ",\"tickets\":[{")),
            () -> assertTrue(response.getContentAsString().contains("\"tickets\":[{\"id\":" + t1Saved.getId())),
            () -> assertTrue(response.getContentAsString().contains(",{\"id\":" + t2Saved.getId())),
            () -> assertTrue(response.getContentAsString().contains("\"id\":" + taSaved2.getId() + ",\"buyerId\":" +
                taSaved2.getBuyer().getId() + ",\"tickets\":[{")),
            () -> assertTrue(response.getContentAsString().contains("\"tickets\":[{\"id\":" + t1bSaved.getId())),
            () -> assertTrue(response.getContentAsString().contains(",{\"id\":" + t2bSaved.getId())),
            () -> assertFalse(response.getContentAsString().contains("[{\"id\":" + taSaved3.getId() + ",\"buyerId\":" +
                taSaved3.getBuyer().getId() + ",\"tickets\":[{")),
            () -> assertFalse(response.getContentAsString().contains("\"tickets\":[{\"id\":" + t3Saved.getId())),
            () -> assertFalse(response.getContentAsString().contains(",{\"id\":" + t4Saved.getId()))
        );
    }

    @Test
    @Transactional
    public void getAllTADoesNotFetchTANotBelongingToUser() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("favsrvssa@testy.com");

        TicketAcquisition ta = new TicketAcquisition();
        ta.setBuyer(buyer);
        ta.setCancelled(false);

        Ticket t1 = createTestTicket();
        t1.setBuyer(buyer);

        TicketAcquisition taSaved = ticketAcquisitionRepository.save(ta);
        t1.setTicketOrder(taSaved);
        ticketRepository.save(t1);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_ACQUISITION_BASE_URI + "/overview")
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertEquals("[]", response.getContentAsString());
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
            ticket.setSeatNo(sectorMaps.get(0).getFirstSeatNr());
        }
        assertNotNull(ticket.getSectorMap());

        ticket.setCreationDate(LocalDateTime.now());
        ticket.setTicketFirstName("Test");
        ticket.setTicketLastName("User");
        ticket.setReservation(TicketStatus.INITIALISED);
        ticket.setCancelled(false);

        return ticket;
    }

    private void insertTestData() {
        // Insert users
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("favsrvssa@testy.com")
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
