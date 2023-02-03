package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActRepository actRepository;
    @Autowired
    private SectorMapRepository sectorMapRepository;
    @Autowired
    private TicketAcquisitionRepository ticketAcquisitionRepository;
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
     * Test checks whether service layer is capable of allowing a correct ticket DTO to pass through.
     *
     * @throws Exception
     */
    @Test
    public void saveNewCorrectly() throws Exception {
        TicketDto provided = createTestTicketDto();
        TicketDto returned = ticketService.save(provided);

        assertAll(
            () -> assertEquals(provided.getBuyerId(), returned.getBuyerId()),
            () -> assertEquals(provided.getActId(), returned.getActId()),
            () -> assertEquals(provided.getSectorMapId(), returned.getSectorMapId()),
            () -> assertEquals(provided.getCreationDate(), returned.getCreationDate()),
            () -> assertEquals(provided.getTicketFirstName(), returned.getTicketFirstName()),
            () -> assertEquals(provided.getTicketLastName(), returned.getTicketLastName()),
            () -> assertEquals(provided.getSeatNo(), returned.getSeatNo()),
            () -> assertEquals(provided.getReservation(), returned.getReservation()),
            () -> assertEquals(provided.isCancelled(), returned.isCancelled())
        );
    }

    /**
     * Test checks whether the service layer is capable of stopping an empty Ticket DTO from being saved.
     */
    @Test
    public void rejectEmptySaveCorrectly() {
        assertThrows(ValidationException.class, () -> ticketService.save(new TicketDto()));
    }

    /**
     * Test checks whether the service layer is capable of stopping an empty Ticket DTO from being updated.
     */
    @Test
    public void rejectEmptyUpdateCorrectly() {
        assertThrows(ValidationException.class, () -> ticketService.update(new TicketDto()));
    }

    /**
     * Test checks whether the service layer is capable of stopping a correct Ticket DTO from updating a non-existent ticket.
     */
    @Test
    @Transactional
    public void rejectCorrectUpdateOfNonexistentTicket() {
        ApplicationUser tuSaved = createAndSaveTestUser();
        setSecurityContextAlt();

        assertThrows(ValidationException.class, () -> {
            TicketDto temp = createTestTicketDto();
            ApplicationUser buyer = userRepository.findApplicationUserByEmail("testttt@test.com");
            temp.setBuyerId(buyer.getId());
            ticketService.update(temp);
        });

        userRepository.deleteById(tuSaved.getId());
    }

    /**
     * Test performs a series of checks to see whether the service layer rejects ticket DTOs that have missing mandatory
     * fields from being saved.
     * Fields checked: Buyer (ID), Act (ID), SectorMap (ID), First name of ticket owner, Last name of ticket owner, Seat
     * number, Reservation status
     */
    @Test
    public void rejectMissingFieldsSaveCorrectly() {
        TicketDto ticket = createTestTicketDto();
        ticket.setBuyerId(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket));

        TicketDto ticket2 = createTestTicketDto();
        ticket2.setActId(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket2));

        TicketDto ticket3 = createTestTicketDto();
        ticket3.setSectorMapId(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket3));

        TicketDto ticket4 = createTestTicketDto();
        ticket4.setTicketFirstName(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket4));

        TicketDto ticket5 = createTestTicketDto();
        ticket5.setTicketLastName(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket5));

        TicketDto ticket6 = createTestTicketDto();
        ticket6.setSeatNo(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket6));

        TicketDto ticket7 = createTestTicketDto();
        ticket7.setReservation(null);
        assertThrows(ValidationException.class, () -> ticketService.save(ticket7));
    }

    /**
     * Test performs a series of checks to see whether the service layer rejects ticket DTOs that have missing mandatory
     * fields from being updated.
     * Fields checked: Buyer (ID), Act (ID), SectorMap (ID), First name of ticket owner, Last name of ticket owner, Seat
     * number, Reservation status
     */
    @Test
    @Transactional
    public void rejectMissingFieldsUpdateCorrectly() {
        Ticket ticketSaved = ticketRepository.save(createTestTicket());

        TicketDto ticket = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket.setBuyerId(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket));

        TicketDto ticket2 = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket2.setActId(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket2));

        TicketDto ticket3 = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket3.setSectorMapId(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket3));

        TicketDto ticket4 = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket4.setTicketFirstName(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket4));

        TicketDto ticket5 = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket5.setTicketLastName(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket5));

        TicketDto ticket6 = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket6.setSeatNo(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket6));

        TicketDto ticket7 = createTestTicketDto();
        ticket.setId(ticketSaved.getId());
        ticket7.setReservation(null);
        assertThrows(ValidationException.class, () -> ticketService.update(ticket7));

        ticketRepository.deleteById(ticketSaved.getId());
    }

    /**
     * Test performs a series of checks to see whether exceptions are thrown by the service layer where saving a ticket
     * would cause a conflict in the system.
     * Fields that can cause a system conflict: Buyer, Act, SectorMap
     * IDs used do not exist in system.
     */
    @Test
    @Transactional
    public void rejectConflictErrorsForSaveCorrectly() {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        TicketDto ticket = createTestTicketDto();
        ticket.setBuyerId(100000L);
        assertThrows(Exception.class, () -> ticketService.save(ticket));

        TicketDto ticket2 = createTestTicketDto();
        ticket2.setActId(100000L);
        assertThrows(ConflictException.class, () -> ticketService.save(ticket2));

        TicketDto ticket3 = createTestTicketDto();
        ticket3.setSectorMapId(100000L);
        assertThrows(ConflictException.class, () -> ticketService.save(ticket3));

        userRepository.deleteById(testUser.getId());
    }

    /**
     * Test performs a series of checks to see whether exceptions are thrown by the service layer where saving a ticket
     *      * would cause a conflict in the system.
     *      * Fields that can cause a system conflict: Buyer, Act, SectorMap
     *      * IDs used do not exist in system.
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectConflictErrorsForUpdateCorrectly() throws Exception {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        TicketDto ticket = createTestTicketDto();
        ticket.setBuyerId(testUser.getId());
        TicketDto ticketSaved = ticketService.save(ticket);
        ticketSaved.setBuyerId(1000L);
        assertThrows(ConflictException.class, () -> ticketService.update(ticketSaved));

        TicketDto ticket2 = createTestTicketDto();
        ticket2.setBuyerId(testUser.getId());
        TicketDto ticketSaved2 = ticketService.save(ticket2);
        ticketSaved2.setActId(1000L);
        assertThrows(ConflictException.class, () -> ticketService.update(ticketSaved2));

        TicketDto ticket3 = createTestTicketDto();
        ticket3.setBuyerId(testUser.getId());
        TicketDto ticketSaved3 = ticketService.save(ticket3);
        ticketSaved3.setSectorMapId(1000L);
        assertThrows(ConflictException.class, () -> ticketService.update(ticketSaved3));

        userRepository.deleteById(testUser.getId());
    }

    /**
     * Test checks whether the service layer rejects an update to a Ticket when the user that owns the ticket is not logged in.
     * @throws Exception
     */
    @Test
    public void rejectUpdateWhenNotLoggedIn() throws Exception {
        TicketDto ticket = createTestTicketDto();
        TicketDto ticketSaved = ticketService.save(ticket);
        assertThrows(Exception.class, () -> ticketService.update(ticketSaved));
    }

    /**
     * Test checks whether it is possible to update the details of a ticket DTO through the service layer.
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateTempTicketCorrectly() throws Exception {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        TicketDto provided = createTestTicketDto();
        provided.setBuyerId(testUser.getId());
        TicketDto returned = ticketService.save(provided);

        returned.setTicketFirstName("Name");
        returned.setTicketLastName("Changed");
        TicketDto returned2 = ticketService.update(returned);

        assertAll(
            () -> assertEquals(returned.getBuyerId(), returned2.getBuyerId()),
            () -> assertEquals(returned.getActId(), returned2.getActId()),
            () -> assertEquals(returned.getSectorMapId(), returned2.getSectorMapId()),
            () -> assertEquals(returned.getCreationDate(), returned2.getCreationDate()),
            () -> assertEquals(returned.getTicketFirstName(), returned2.getTicketFirstName()),
            () -> assertEquals(returned.getTicketLastName(), returned2.getTicketLastName()),
            () -> assertEquals(returned.getSeatNo(), returned2.getSeatNo()),
            () -> assertEquals(returned.getReservation(), returned2.getReservation()),
            () -> assertEquals(returned.isCancelled(), returned2.isCancelled())
        );

        userRepository.deleteById(testUser.getId());
    }

    /**
     * Test checks whether it is possible to change the reservation status of a ticket.
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateTempTicketToPermTicketCorrectly() throws Exception {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        TicketDto provided = createTestTicketDto();
        provided.setReservation(TicketStatus.INITIALISED);
        provided.setBuyerId(testUser.getId());
        TicketDto returned = ticketService.save(provided);

        assertEquals(returned.getReservation(), TicketStatus.INITIALISED);

        returned.setReservation(TicketStatus.RESERVED);
        TicketDto returned2 = ticketService.update(returned);

        assertAll(
            () -> assertEquals(returned.getBuyerId(), returned2.getBuyerId()),
            () -> assertEquals(returned.getActId(), returned2.getActId()),
            () -> assertEquals(returned.getSectorMapId(), returned2.getSectorMapId()),
            () -> assertEquals(returned.getCreationDate(), returned2.getCreationDate()),
            () -> assertEquals(returned.getTicketFirstName(), returned2.getTicketFirstName()),
            () -> assertEquals(returned.getTicketLastName(), returned2.getTicketLastName()),
            () -> assertEquals(returned.getSeatNo(), returned2.getSeatNo()),
            () -> assertEquals(returned2.getReservation(), TicketStatus.RESERVED),
            () -> assertEquals(returned.isCancelled(), returned2.isCancelled())
        );
        userRepository.deleteById(testUser.getId());
    }

    /**
     * Test checks whether the service layer is capabale of deleting a ticket that hasn't been bought yet.
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteUnboughtCorrectly() throws Exception {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        TicketDto provided = createTestTicketDto();
        provided.setBuyerId(testUser.getId());
        TicketDto returned = ticketService.save(provided);

        ticketService.delete(returned.getId());

        assertThrows(NotFoundException.class, () -> ticketService.get(returned.getId()));

        userRepository.deleteById(testUser.getId());
    }

    /**
     * Test checks whether the service layer correctly cancels a ticket that has already been bought, and does NOT
     * permanently delete it from the system.
     * @throws Exception
     */
    @Test
    @Transactional
    public void cancelBoughtCorrectly() throws Exception {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        TicketDto provided = createTestTicketDto();
        provided.setBuyerId(testUser.getId());
        TicketDto returned = ticketService.save(provided);

        TicketAcquisition acq = new TicketAcquisition();
        acq.setBuyer(testUser);
        acq.setCancelled(false);
        List<Ticket> tickets = new ArrayList<>();
        Ticket temp = ticketRepository.findTicketById(returned.getId());
        tickets.add(temp);
        acq.setTickets(tickets);

        TicketAcquisition taReturned = ticketAcquisitionRepository.save(acq);
        temp.setTicketOrder(taReturned);
        ticketRepository.save(temp);

        ticketService.delete(returned.getId());

        TicketDto fetched = ticketService.get(returned.getId());

        assertAll(
            () -> assertEquals(returned.getBuyerId(), fetched.getBuyerId()),
            () -> assertEquals(returned.getActId(), fetched.getActId()),
            () -> assertEquals(returned.getSectorMapId(), fetched.getSectorMapId()),
            () -> assertEquals(returned.getCreationDate(), fetched.getCreationDate()),
            () -> assertEquals(returned.getTicketFirstName(), fetched.getTicketFirstName()),
            () -> assertEquals(returned.getTicketLastName(), fetched.getTicketLastName()),
            () -> assertEquals(returned.getSeatNo(), fetched.getSeatNo()),
            () -> assertTrue(returned.getReservation() == fetched.getReservation()),
            () -> assertNotEquals(returned.isCancelled(), fetched.isCancelled())
        );

        userRepository.deleteById(testUser.getId());
    }

    /**
     * Test checks whether the service layer correctly fetches the tickets (both reservations and purchases) belonging
     * to a specific user that have not been bought yet.
     */
    @Test
    @Transactional
    public void fetchUnboughtCorrectly() {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        Ticket correct1 = createTestTicket();
        Ticket correct2 = createTestTicket();
        Ticket wrong1 = createTestTicket();
        Ticket wrong2 = createTestTicket();
        Ticket wrong3 = createTestTicket();
        correct1.setReservation(TicketStatus.RESERVED);
        correct2.setReservation(TicketStatus.PURCHASED);
        wrong1.setReservation(TicketStatus.RESERVED);
        wrong2.setReservation(TicketStatus.PURCHASED);
        wrong3.setReservation(TicketStatus.PURCHASED);
        correct1.setBuyer(testUser);
        correct2.setBuyer(testUser);
        wrong1.setBuyer(testUser);
        wrong1.setCreationDate(LocalDateTime.now().minus(30, ChronoUnit.MINUTES));
        wrong3.setCreationDate(LocalDateTime.now().minus(45, ChronoUnit.MINUTES));

        Ticket correct1Saved = ticketRepository.save(correct1);
        Ticket correct2Saved = ticketRepository.save(correct2);
        Ticket wrong1Saved = ticketRepository.save(wrong1);
        Ticket wrong2Saved = ticketRepository.save(wrong2);
        Ticket wrong3Saved = ticketRepository.save(wrong3);

        List<TicketDetailsDto> ticketsFetched = ticketService.getAllDetailedTickets();

        assertAll(
            () -> assertEquals(ticketsFetched.size(), 2),
            () -> assertTrue(contains(ticketsFetched, correct1Saved)),
            () -> assertTrue(contains(ticketsFetched, correct2Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong1Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong2Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong3Saved))
        );
    }

    /**
     * Test checks whether the service layer correctly fetches the tickets (ONLY purchases) belonging
     * to a specific user that have not been bought yet.
     */
    @Test
    @Transactional
    public void fetchUnboughtPurchasesCorrectly() {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        Ticket correct1 = createTestTicket();
        Ticket correct2 = createTestTicket();
        Ticket wrong1 = createTestTicket();
        Ticket wrong2 = createTestTicket();
        Ticket wrong3 = createTestTicket();
        correct1.setReservation(TicketStatus.RESERVED);
        correct2.setReservation(TicketStatus.PURCHASED);
        wrong1.setReservation(TicketStatus.RESERVED);
        wrong2.setReservation(TicketStatus.PURCHASED);
        wrong3.setReservation(TicketStatus.PURCHASED);
        correct1.setBuyer(testUser);
        correct2.setBuyer(testUser);
        wrong1.setBuyer(testUser);
        wrong1.setCreationDate(LocalDateTime.now().minus(30, ChronoUnit.MINUTES));
        wrong3.setCreationDate(LocalDateTime.now().minus(45, ChronoUnit.MINUTES));

        Ticket correct1Saved = ticketRepository.save(correct1);
        Ticket correct2Saved = ticketRepository.save(correct2);
        Ticket wrong1Saved = ticketRepository.save(wrong1);
        Ticket wrong2Saved = ticketRepository.save(wrong2);
        Ticket wrong3Saved = ticketRepository.save(wrong3);

        List<TicketDetailsDto> ticketsFetched = ticketService.getAllDetailedPurchases();

        assertAll(
            () -> assertEquals(ticketsFetched.size(), 1),
            () -> assertFalse(contains(ticketsFetched, correct1Saved)),
            () -> assertTrue(contains(ticketsFetched, correct2Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong1Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong2Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong3Saved))
        );
    }

    /**
     * Test checks whether the service layer correctly fetches the tickets (ONLY reservations) belonging
     * to a specific user that have not been bought yet.
     */
    @Test
    @Transactional
    public void fetchUnboughtReservationsCorrectly() {
        ApplicationUser testUser = createAndSaveTestUser();
        setSecurityContextAlt();

        Ticket correct1 = createTestTicket();
        Ticket correct2 = createTestTicket();
        Ticket wrong1 = createTestTicket();
        Ticket wrong2 = createTestTicket();
        Ticket wrong3 = createTestTicket();
        correct1.setReservation(TicketStatus.RESERVED);
        correct2.setReservation(TicketStatus.PURCHASED);
        wrong1.setReservation(TicketStatus.RESERVED);
        wrong2.setReservation(TicketStatus.PURCHASED);
        wrong3.setReservation(TicketStatus.PURCHASED);
        correct1.setBuyer(testUser);
        correct2.setBuyer(testUser);
        wrong1.setBuyer(testUser);
        wrong1.setCreationDate(LocalDateTime.now().minus(30, ChronoUnit.MINUTES));
        wrong3.setCreationDate(LocalDateTime.now().minus(45, ChronoUnit.MINUTES));

        Ticket correct1Saved = ticketRepository.save(correct1);
        Ticket correct2Saved = ticketRepository.save(correct2);
        Ticket wrong1Saved = ticketRepository.save(wrong1);
        Ticket wrong2Saved = ticketRepository.save(wrong2);
        Ticket wrong3Saved = ticketRepository.save(wrong3);

        List<TicketDetailsDto> ticketsFetched = ticketService.getAllDetailedReservations();

        assertAll(
            () -> assertEquals(ticketsFetched.size(), 1),
            () -> assertTrue(contains(ticketsFetched, correct1Saved)),
            () -> assertFalse(contains(ticketsFetched, correct2Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong1Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong2Saved)),
            () -> assertFalse(contains(ticketsFetched, wrong3Saved))
        );
    }

    private TicketDto createTestTicketDto() {
        TicketDto ticket = new TicketDto();

        List<ApplicationUser> buyers = userRepository.findAll();
        if (!buyers.isEmpty()) {
            ticket.setBuyerId(buyers.get(0).getId());
        }
        assertNotNull(ticket.getBuyerId());

        List<Act> acts = actRepository.findAll();
        if (!acts.isEmpty()) {
            ticket.setActId(acts.get(0).getId());
        }
        assertNotNull(ticket.getActId());

        List<SectorMap> sectorMaps = sectorMapRepository.findAll();
        if (!sectorMaps.isEmpty()) {
            ticket.setSectorMapId(sectorMaps.get(0).getId());
        }
        assertNotNull(ticket.getSectorMapId());

        ticket.setCreationDate(LocalDateTime.now());
        ticket.setTicketFirstName("Test");
        ticket.setTicketLastName("User");
        ticket.setSeatNo(1);
        ticket.setReservation(TicketStatus.INITIALISED);
        ticket.setCancelled(false);

        return ticket;
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

    private ApplicationUser createAndSaveTestUser() {
        ApplicationUser testUser = new ApplicationUser();
        testUser.setLockedOut(false);
        testUser.setAdmin(false);
        testUser.setEmail("testttt@test.com");
        testUser.setPassword("password");
        testUser.setFirstName("Testttt");
        testUser.setLastName("Userrrrr");
        return userRepository.save(testUser);
    }

    private void setSecurityContextAlt() {
        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = new UsernamePasswordAuthenticationToken("testttt@test.com", "password");
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private boolean contains(List<TicketDetailsDto> tickets, Ticket ticket) {
        for (TicketDetailsDto t : tickets) {
            if (Objects.equals(t.getId(), ticket.getId())) {
                return true;
            }
        }
        return false;
    }

    private void insertTestData() {
        // Insert users
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("asdfghjk@testy.com")
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
