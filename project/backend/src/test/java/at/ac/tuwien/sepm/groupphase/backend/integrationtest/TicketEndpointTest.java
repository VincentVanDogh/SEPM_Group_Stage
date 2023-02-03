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
public class TicketEndpointTest implements TestData {

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
    private String token = "";
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
        // Clean tickets
        ticketRepository.deleteAll();
    }

    /**
     * Test checks whether a new ticket (syntactically correct and without any conflicts) is saved correctly in the system.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void saveNewCorrectly() throws Exception {
        ApplicationUser currentUser = userRepository.findApplicationUserByEmail("test@test.com");
        List<Act> acts = actRepository.findAll();
        assertFalse(acts.isEmpty());
        List<SectorMap> sectorMaps = sectorMapRepository.findAll();
        assertFalse(sectorMaps.isEmpty());
        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{"
                        + "\"buyerId\": " + currentUser.getId() + ","
                        + "\"actId\": " + acts.get(0).getId() + ","
                        + "\"sectorMapId\": " + sectorMaps.get(0).getId() + ","
                        + "\"ticketFirstName\": \"Test\","
                        + "\"ticketLastName\": \"User\","
                        + "\"seatNo\": " + sectorMaps.get(0).getFirstSeatNr() + ","
                        + "\"reservation\": \"INITIALISED\""
                        + "}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn().getResponse();
    }

    /**
     * Test checks whether the system rejects a POST request with empty curly brackets as the content.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectEmptyTicket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {}
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    }

    /**
     * Test performs a series of checks to see whether the system checks whether the mandatory fields in the TicketDTO
     * are filled in and rejects POST requests with missing mandatory data accordingly.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectMissingValues() throws Exception {
        // Buyer ID added automatically, creationDate added automatically, ticket ID added automatically

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "sectorMapId": 1,
                                   "ticketFirstName": "Test",
                                   "ticketLastName": "User",
                                   "seatNo": 1,
                                   "reservation": "INITIALISED"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "actId": 1,
                                   "ticketFirstName": "Test",
                                   "ticketLastName": "User",
                                   "seatNo": 1,
                                   "reservation": "INITIALISED"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "actId": 1,
                                   "sectorMapId": 1,
                                   "ticketLastName": "User",
                                   "seatNo": 1,
                                   "reservation": "INITIALISED"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "actId": 1,
                                   "sectorMapId": 1,
                                   "ticketFirstName": "Test",
                                   "seatNo": 1,
                                   "reservation": "INITIALISED"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "actId": 1,
                                   "sectorMapId": 1,
                                   "ticketFirstName": "Test",
                                   "ticketLastName": "User",
                                   "reservation": "INITIALISED"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "actId": 1,
                                   "sectorMapId": 1,
                                   "ticketFirstName": "Test",
                                   "ticketLastName": "User",
                                   "seatNo": 1
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    }

    /**
     * Test checks whether a PUT request to the server (no validation or confict errors) correctly updates a ticket
     * saved in the system.
     *
     * @throws Exception
     */
    @Test
    public void updateTempTicketCorrectly() throws Exception {
        List<Act> acts = actRepository.findAll();
        assertFalse(acts.isEmpty());
        List<SectorMap> sectorMaps = sectorMapRepository.findAll();
        assertFalse(sectorMaps.isEmpty());
        MockHttpServletResponse posted = mockMvc.perform(MockMvcRequestBuilders.post(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{"
                        + "\"actId\": " + acts.get(0).getId() + ","
                        + "\"sectorMapId\": " + sectorMaps.get(0).getId() + ","
                        + "\"ticketFirstName\": \"Test\","
                        + "\"ticketLastName\": \"User\","
                        + "\"seatNo\": " + sectorMaps.get(0).getFirstSeatNr() + ","
                        + "\"reservation\": \"INITIALISED\""
                        + "}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn().getResponse();

        assertTrue(posted.getContentAsString().contains("\"ticketFirstName\":\"Test\","));
        assertTrue(posted.getContentAsString().contains("\"ticketLastName\":\"User\","));

        MockHttpServletResponse put = mockMvc.perform(MockMvcRequestBuilders.put(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(posted.getContentAsString()
                    .replace("Test", "New").replace("User", "Account"))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertTrue(put.getContentAsString().contains("\"ticketFirstName\":\"New\","));
        assertTrue(put.getContentAsString().contains("\"ticketLastName\":\"Account\","));
    }

    /**
     * Test checks whether the system rejects the update of a ticket not saved in the system. (ID number not in system)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectUpdateOfNonexistentTicket() throws Exception {
        ApplicationUser currentUser = userRepository.findApplicationUserByEmail("test@test.com");
        List<Act> acts = actRepository.findAll();
        assertFalse(acts.isEmpty());
        List<SectorMap> sectorMaps = sectorMapRepository.findAll();
        assertFalse(sectorMaps.isEmpty());
        mockMvc.perform(MockMvcRequestBuilders.put(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{"
                        + "\"id\": 10000000,"
                        + "\"buyerId\": " + currentUser.getId() + ","
                        + "\"actId\": " + acts.get(0).getId() + ","
                        + "\"sectorMapId\": " + sectorMaps.get(0).getId() + ","
                        + "\"ticketFirstName\": \"Test\","
                        + "\"ticketLastName\": \"User\","
                        + "\"seatNo\": " + sectorMaps.get(0).getFirstSeatNr() + ","
                        + "\"reservation\": \"INITIALISED\""
                        + "}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
            .andReturn().getResponse();
    }

    /**
     * Test checks whether system forbids non-admin users to update tickets not belonging to them.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectUpdateOfTicketNotBelongingToUser() throws Exception {
        Ticket saved = ticketRepository.save(createTestTicket());
        mockMvc.perform(MockMvcRequestBuilders.put(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(
                    "{" +
                        "\"id\": " + saved.getId() + "," +
                        "\"buyerId\": 1," +
                        "\"actId\": 1," +
                        "\"sectorMapId\": 1," +
                        "\"ticketFirstName\": \"Test\"," +
                        "\"ticketLastName\": \"User\"," +
                        "\"seatNo\": 1," +
                        "\"reservation\": \"INITIALISED\"" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
            .andReturn().getResponse();
    }

    /**
     * Test checks whether a correct DELETE request successfully deletes a ticket from the system.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteUnboughtCorrectlyBelongingToUser() throws Exception {
        Ticket ticket = createTestTicket();
        ticket.setBuyer(userRepository.findApplicationUserByEmail("test@test.com"));
        Ticket saved = ticketRepository.save(ticket);
        mockMvc.perform(MockMvcRequestBuilders.delete(TICKET_BASE_URI + "/" + saved.getId())
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertNull(ticketRepository.findTicketById(saved.getId()));
    }

    /**
     * Test checks whether the system forbids non-admin users from deleting tickets not belonging to them.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectDeleteUnboughtCorrectlyNotBelongingToUser() throws Exception {
        Ticket saved = ticketRepository.save(createTestTicket());
        mockMvc.perform(MockMvcRequestBuilders.delete(TICKET_BASE_URI + "/" + saved.getId())
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
            .andReturn().getResponse();

        assertNotNull(ticketRepository.findTicketById(saved.getId()));
    }

    /**
     * Test checks whether a DELETE request for a non-existing ticket returns a Not Found error.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteNonexistingTicketReturnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(TICKET_BASE_URI + "/1000000")
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
            .andReturn().getResponse();
    }

    /**
     * Test checks whether the system forbids non-admin users from fetching specific tickets not belonging to them.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void fetchSpecificTicketNotUsers() throws Exception {
        Ticket ticket = ticketRepository.save(createTestTicket());
        mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI + "/" + ticket.getId())
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
            .andReturn().getResponse();
    }

    /**
     * Test checks the fetching of a specific ticket belonging to a user.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void fetchSpecificUsersTicket() throws Exception {
        Ticket ticket = createTestTicket();
        ticket.setBuyer(userRepository.findApplicationUserByEmail("test@test.com"));
        Ticket returned = ticketRepository.save(ticket);
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI + "/" + returned.getId())
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertTrue(response.getContentAsString().contains("\"id\":" + returned.getId()));
    }

    /**
     * Test checks the fetching of all the unbought tickets stored in the system by a non-admin user in 2 different
     * scenarios, expecting an empty response (which is correct):
     *   * No tickets saved in system
     *   * No tickets belonging to user saved in system
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void fetchTicketsEmptyResponse() throws Exception {
        MockHttpServletResponse noneResponse = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertEquals("[]", noneResponse.getContentAsString());

        ticketRepository.save(createTestTicket());
        ticketRepository.save(createTestTicket());
        ticketRepository.save(createTestTicket());

        MockHttpServletResponse noneBelongingToUserResponse = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertEquals("[]", noneBelongingToUserResponse.getContentAsString());
    }

    /**
     * Test checks whether the function to fetch unbought tickets functions correctly. Test adds tickets to db and then
     * adds some of these tickets to an order, checking whether these tickets are then removed from the result of the
     * query of unbought tickets.
     *
     * @throws Exception
     */
    @Test
    public void fetchUnboughtCorrectly() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");

        Ticket ticket1 = createTestTicket();
        Ticket ticket2 = createTestTicket();
        Ticket ticket3 = createTestTicket();

        ticket1.setBuyer(buyer);
        ticket2.setBuyer(buyer);
        ticket3.setBuyer(buyer);
        ticket1.setReservation(TicketStatus.RESERVED);
        ticket2.setReservation(TicketStatus.PURCHASED);
        ticket3.setReservation(TicketStatus.RESERVED);

        Ticket ticket1Saved = ticketRepository.save(ticket1);
        Ticket ticket2Saved = ticketRepository.save(ticket2);
        Ticket ticket3Saved = ticketRepository.save(ticket3);

        MockHttpServletResponse someBelongingToUser = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertTrue(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket1Saved.getId()));
        assertTrue(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket2Saved.getId()));
        assertTrue(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket3Saved.getId()));

        TicketAcquisition temp = new TicketAcquisition();
        temp.setCancelled(false);
        temp.setBuyer(buyer);
        TicketAcquisition saved = ticketAcquisitionRepository.save(temp);
        ticket2Saved.setTicketOrder(saved);
        ticket3Saved.setTicketOrder(saved);
        ticketRepository.save(ticket2Saved);
        ticketRepository.save(ticket3Saved);

        MockHttpServletResponse someBelongingToUser2 = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
        assertTrue(someBelongingToUser2.getContentAsString().contains("\"id\":" + ticket1Saved.getId() + ",\"buyerId\":" + buyer.getId()));
        assertFalse(someBelongingToUser2.getContentAsString().contains("\"id\":" + ticket2Saved.getId() + ",\"buyerId\":" + buyer.getId()));
        assertFalse(someBelongingToUser2.getContentAsString().contains("\"id\":" + ticket3Saved.getId() + ",\"buyerId\":" + buyer.getId()));
    }

    @Test
    @Transactional
    public void fetchUnboughtReservationsCorrectly() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");

        Ticket ticket1 = createTestTicket();
        Ticket ticket2 = createTestTicket();
        Ticket ticket3 = createTestTicket();

        ticket1.setBuyer(buyer);
        ticket2.setBuyer(buyer);
        ticket3.setBuyer(buyer);
        ticket1.setReservation(TicketStatus.RESERVED);
        ticket2.setReservation(TicketStatus.PURCHASED);
        ticket3.setReservation(TicketStatus.RESERVED);

        Ticket ticket1Saved = ticketRepository.save(ticket1);
        Ticket ticket2Saved = ticketRepository.save(ticket2);
        Ticket ticket3Saved = ticketRepository.save(ticket3);

        MockHttpServletResponse someBelongingToUser = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI + "/reservations")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertTrue(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket1Saved.getId()));
        assertFalse(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket2Saved.getId()));
        assertTrue(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket3Saved.getId()));
    }

    @Test
    @Transactional
    public void fetchUnboughtPurchasesCorrectly() throws Exception {
        ApplicationUser buyer = userRepository.findApplicationUserByEmail("test@test.com");

        Ticket ticket1 = createTestTicket();
        Ticket ticket2 = createTestTicket();
        Ticket ticket3 = createTestTicket();

        ticket1.setBuyer(buyer);
        ticket2.setBuyer(buyer);
        ticket3.setBuyer(buyer);
        ticket1.setReservation(TicketStatus.RESERVED);
        ticket2.setReservation(TicketStatus.PURCHASED);
        ticket3.setReservation(TicketStatus.RESERVED);

        Ticket ticket1Saved = ticketRepository.save(ticket1);
        Ticket ticket2Saved = ticketRepository.save(ticket2);
        Ticket ticket3Saved = ticketRepository.save(ticket3);

        MockHttpServletResponse someBelongingToUser = mockMvc.perform(MockMvcRequestBuilders.get(TICKET_BASE_URI + "/purchases")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        assertFalse(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket1Saved.getId()));
        assertTrue(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket2Saved.getId()));
        assertFalse(someBelongingToUser.getContentAsString().contains("\"id\":" + ticket3Saved.getId()));
    }

    private Ticket createTestTicket() {
        Ticket ticket = new Ticket();

        ticket.setBuyer(userRepository.findApplicationUserByEmail("dsfgijkoldfvg@testy.com"));
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

    private void insertTestData() {
        // Insert users
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("dsfgijkoldfvg@testy.com")
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
