package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsWithPricesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AddressMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketAcquisitionMapperAlt;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapperAlt;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketAcquisitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketAcquisitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.validation.TicketAcquisitionValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validation.TicketValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketAcquisitionServiceImpl implements TicketAcquisitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final TicketAcquisitionRepository ticketAcquisitionRepository;
    private final TicketAcquisitionMapperAlt ticketAcquisitionMapper;
    private final UserRepository userRepository;
    private final TicketValidator ticketValidator;
    private final TicketAcquisitionValidator ticketAcquisitionValidator;
    private final TicketMapperAlt ticketMapper;
    private final InvoiceService invoiceService;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    @Autowired
    public TicketAcquisitionServiceImpl(TicketRepository ticketRepository,
                                        TicketAcquisitionRepository ticketAcquisitionRepository,
                                        TicketAcquisitionMapperAlt ticketAcquisitionMapper,
                                        UserRepository userRepository,
                                        TicketValidator ticketValidator,
                                        TicketAcquisitionValidator ticketAcquisitionValidator,
                                        TicketMapperAlt ticketMapper,
                                        InvoiceService invoiceService,
                                        AddressRepository addressRepository,
                                        AddressMapper addressMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketAcquisitionRepository = ticketAcquisitionRepository;
        this.ticketAcquisitionMapper = ticketAcquisitionMapper;
        this.userRepository = userRepository;
        this.ticketValidator = ticketValidator;
        this.ticketAcquisitionValidator = ticketAcquisitionValidator;
        this.ticketMapper = ticketMapper;
        this.invoiceService = invoiceService;
        this.addressMapper = addressMapper;
        this.addressRepository = addressRepository;
    }

    @Override
    public TicketAcquisitionDto get(Long id) throws ValidationException, NotFoundException, ForbiddenException {
        if (id == null) {
            // ID is null: Validation error
            List<String> error = new ArrayList<>();
            error.add("ID of ticket acquisition must be given to fetch order");
            throw new ValidationException("Could not get ticket!", error);
        }

        // Get current user's details and the ticket acquisition
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();
        Optional<TicketAcquisition> ticketAcq = ticketAcquisitionRepository.findById(id);

        Long buyerId;
        if (ticketAcq.isPresent()) {
            buyerId = ticketAcq.get().getBuyer().getId();
        } else {
            throw new NotFoundException("Order with ID number " + id + " could not be found!");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isRoleAdmin = auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
        if (!isRoleAdmin && !userId.equals(buyerId)) {
            // User is not admin and not the user to which the order relates
            throw new ForbiddenException("Could not get ticket! Logged in user is not permitted to access the requested order");
        }

        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDto(removeDuplicateTickets(ticketAcq.get()));
    }

    @Override
    public List<TicketAcquisitionDto> getAll() {
        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDto(ticketAcquisitionRepository.findAll());
    }

    @Override
    public List<TicketAcquisitionDto> getAllUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();
        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDto(
            ticketAcquisitionRepository.findAllByBuyerId(userId));
    }

    @Override
    public List<TicketAcquisitionDetailsDto> getAllForUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();
        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDetailsDto(
                ticketAcquisitionRepository.findAllByBuyerId(userId)
        );
    }

    @Override
    public ReservationPageDto getAllReservationsForUser(int page) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();

        return new ReservationPageDto(
            ticketAcquisitionRepository.findAllReservationsForUser(userId).size() % 3 == 0
                ? ticketAcquisitionRepository.findAllReservationsForUser(userId).size() / 3 :
                ticketAcquisitionRepository.findAllReservationsForUser(userId).size() / 3 + 1,
            ticketAcquisitionMapper.ticketAcquisitionToReservationDto(
                ticketAcquisitionRepository.findAllReservationsForUser(userId)
            ).stream().skip((page - 1) * 3L).limit(3)
        );
    }

    @Override
    public ReservationPageDto getAllCurrentReservationsForUser(int page) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();

        return new ReservationPageDto(
            ticketAcquisitionRepository.findAllCurrentReservationsForUser(userId).size() % 3 == 0
                ? ticketAcquisitionRepository.findAllCurrentReservationsForUser(userId).size() / 3 :
                ticketAcquisitionRepository.findAllCurrentReservationsForUser(userId).size() / 3 + 1,
            ticketAcquisitionMapper.ticketAcquisitionToReservationDto(
                ticketAcquisitionRepository.findAllCurrentReservationsForUser(userId)
            ).stream().skip((page - 1) * 3L).limit(3)
        );
    }

    @Override
    public TicketAcquisitionDto save(TicketAcquisitionDto details) throws ValidationException, ConflictException {
        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDto(saveRaw(details));
    }

    @Override
    public TicketAcquisitionDetailsDto save() throws ValidationException, ConflictException {
        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDetailsDto(saveRaw());
    }

    @Override
    public TicketAcquisition saveRaw(TicketAcquisitionDto details) throws ValidationException, ConflictException {
        // Validation:
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();

        // BUYER
        // Validate that buyer (user that sent request) exists in system, set buyer details if correct.
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ApplicationUser user = userRepository.findApplicationUserByEmail(username);
            if (user == null) {
                conflictErrors.add("User does not exist in system!");
            }
        } else {
            if (details.getBuyerId() == null) {
                validationErrors.add("Buyer is not identifiable!");
            }
        }

        // TICKETS
        if (details.getTickets().isEmpty()) {
            validationErrors.add("Tickets bought/reserved cannot be empty!");
        } else {
            for (TicketDto t : details.getTickets()) {
                ticketValidator.validateForSaving(t);
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of ticket acquisition failed", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Ticket acquisition failed to be saved in system due to potential conflict(s)",
                conflictErrors);
        }

        // There was a problem with the ticket order ID number saving in the ticket database when doing it directly from the ticketAcquisitionRepository.
        // This workaround actually works in saving the tickets and order properly (with correct links)

        // Tickets to be saved have been extracted and save ticket order first (to get ID number)
        List<TicketDto> tickets = details.getTickets();
        details.setTickets(new ArrayList<>());

        TicketAcquisition returned = ticketAcquisitionRepository.save(
            ticketAcquisitionMapper.ticketAcquisitionDtoToTicketAcquisition(details));

        // Assign ticket order number to each ticket and save in the db individually
        for (TicketDto t : tickets) {
            Ticket temp = ticketMapper.ticketDtoToTicket(t);
            temp.setTicketOrder(returned);
            temp.setCreationDate(LocalDateTime.now());
            ticketRepository.save(temp);
        }

        return ticketAcquisitionRepository.findById(returned.getId()).get();
    }

    public TicketAcquisition saveRaw() throws ValidationException, ConflictException {
        // Validation:
        List<String> validationErrors = new ArrayList<>();

        List<Ticket> ticketsInCart = ticketRepository.findAllByBuyerIdAndTicketOrderIsNull(getUser().getId());
        List<TicketDto> ticketDtoListInCart = ticketMapper.ticketToTicketDto(ticketsInCart);

        // TICKETS
        if (ticketsInCart.isEmpty()) {
            validationErrors.add("Tickets bought/reserved cannot be empty!");
        } else {
            for (TicketDto t : ticketDtoListInCart) {
                ticketValidator.validateForSaving(t);
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of ticket acquisition failed", validationErrors);
        }

        // There was a problem with the ticket order ID number saving in the ticket database when doing it directly from the ticketAcquisitionRepository.
        // This workaround actually works in saving the tickets and order properly (with correct links)

        // Tickets to be saved have been extracted and save ticket order first (to get ID number)
        TicketAcquisitionDto details = new TicketAcquisitionDto();
        details.setTickets(new ArrayList<>());

        TicketAcquisition returned = ticketAcquisitionRepository.save(
            ticketAcquisitionMapper.ticketAcquisitionDtoToTicketAcquisition(details));

        // Assign ticket order number to each ticket and save in the db individually
        for (Ticket t : ticketsInCart) {
            t.setTicketOrder(returned);
            t.setCreationDate(LocalDateTime.now());
            ticketRepository.save(t);
        }

        return removeDuplicateTickets(ticketAcquisitionRepository.findById(returned.getId()).get());
    }

    @Override
    public void cancel(Long id) throws ValidationException, NotFoundException {
        if (id == null) {
            List<String> error = new ArrayList<>();
            error.add("ID of ticket acquisition must be given in order to cancel!");
            throw new ValidationException("ID of ticket acquisition must be given in order to cancel!", error);
        }

        // To cancel the order and keep it for future reference:
        Optional<TicketAcquisition> savedAcq = ticketAcquisitionRepository.findById(id);
        if (savedAcq.isPresent()) {
            TicketAcquisition savedAcqExtracted = savedAcq.get();

            // if all tickets are in the future // Workaround because LazyInitializationException
            List<Ticket> ticketsInOrder = ticketRepository.findTicketsByTicketOrderId(id);
            savedAcqExtracted.setTickets(ticketsInOrder);
            if (allTicketsInFuture(ticketsInOrder)) {
                savedAcqExtracted.setCancelled(true);
            }

            // Cancel each ticket in the order by updating the cancelled variable and updating db
            if (ticketsInOrder != null) {
                for (Ticket t : ticketsInOrder) {
                    if (t.getAct().getStart().isAfter(LocalDateTime.now())) {
                        t.setCancelled(true);
                        ticketRepository.save(t);
                    }
                }
            }

            // Update db for order
            ticketAcquisitionRepository.save(savedAcqExtracted);
        } else {
            throw new NotFoundException("Could not find Ticket Acquisition with ID number " + id);
        }
    }


    public TicketAcquisitionDto cancel(TicketAcquisitionDetailsWithPricesDto ta) throws NotFoundException, ValidationException, ConflictException {
        LOGGER.debug("Cancelling tickets from dto");
        //Important: This method does not set the cancelled boolean for TAs, since that information is already saved through the individual cancelled
        //statuses of tickets.

        //validate
        ticketAcquisitionValidator.validateForCancellation(ta);

        //split off those tickets only reserved and deal with them separately
        List<Long> ids = ta.tickets().stream().map(TicketDetailsDto::getId).collect(Collectors.toList());
        List<Ticket> purchasedTicketsToCancel = ticketRepository.findUncancelledPurchasedByIds(ids);
        List<Ticket> reservedTicketsToCancel = ticketRepository.findUncancelledReservedByIds(ids);

        TicketAcquisition cancellation = null;
        if (!purchasedTicketsToCancel.isEmpty()) {
            //get the ticket acquisition of the original purchase
            TicketAcquisition originalAcq = ticketAcquisitionRepository.findTicketAcquisitionForBuyerAndContainingTicket(
                ta.buyerId(), ta.tickets().get(0).getId());
            if (originalAcq == null) {
                throw new NotFoundException("Could not match cancellation to purchase");
            }

            //save cancellation TA
            cancellation = ticketAcquisitionMapper.ticketAcquisitionDtoToTicketAcquisition(ta);
            cancellation.setCancelledTickets(purchasedTicketsToCancel);
            cancellation.setTickets(null);
            cancellation.setId(null);
            cancellation = ticketAcquisitionRepository.save(cancellation);

            //save cancellation invoice
            Optional<Address> address = addressRepository.findById(originalAcq.getInvoice().getAddress().getId());
            CreateInvoiceDto cancellationDto = new CreateInvoiceDto(InvoiceType.CANCELLATION,
                addressMapper.addressToAddressDto(address.get()),
                originalAcq.getInvoice().getRecipientName());
            invoiceService.save(cancellationDto, cancellation, null);

            //update Ticket status for linked tickets
            for (Ticket t : purchasedTicketsToCancel) {
                t.setCancelled(true);
                t.setCancellation(cancellation);
                ticketRepository.save(t);
            }
        }
        for (Ticket t : reservedTicketsToCancel) {
            //deal with reserved tickets based on if they were part of a purchase or not
            if (t.getTicketOrder() == null) {
                ticketRepository.deleteById(t.getId());
            } else {
                t.setCancelled(true);
                ticketRepository.save(t);
            }
        }

        return ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDto(cancellation);
    }

    @Override
    public void buyReservedTickets(Long id) throws NotFoundException, ForbiddenException {
        // 0. Fetch ticket acquisition and tickets from the db
        Optional<TicketAcquisition> taOpt = ticketAcquisitionRepository.findById(id);
        if (taOpt.isEmpty()) {
            LOGGER.info("TA not found, id number: " + id);
            throw new NotFoundException("Ticket Acquisition with ID number " + id + " could not be found in the system!");
        }

        List<Ticket> tickets = ticketRepository.findTicketsByTicketOrderId(id);

        // 1. Remove reserved tickets from original ticket acquisition/remove TA ID number from tickets
        // 2. Set the status of the tickets to PURCHASED
        // 3. Set the creation date of the tickets to now (so they are seen in the cart and not deleted in the next 15 mins)
        // 4. Update the tickets in the database.
        // Counter is used because making changes to ArrayLists in a foreach loop leads to ConcurrentModificationException.
        int count = tickets.size();
        for (Ticket t : tickets) {
            if (t.getReservation() == TicketStatus.RESERVED) {
                t.setTicketOrder(null);
                t.setReservation(TicketStatus.PURCHASED);
                t.setCreationDate(LocalDateTime.now());
                count--;
            }
        }

        // Delete empty TAs out of db
        if (count == 0) {
            //ticketAcquisitionRepository.delete(taOpt.get());

            // Temporary workaround:
            TicketAcquisition temp = taOpt.get();
            temp.setBuyer(null);
            temp.setTickets(new ArrayList<>());
            ticketAcquisitionRepository.save(temp);
        }

        // Update the tickets in the db
        ticketRepository.saveAll(tickets);
    }

    @Override
    public List<Ticket> ticketsInCart() {
        List<Ticket> ticketsInCart = this.ticketRepository.findAllByBuyerIdAndTicketOrderIsNull(getUser().getId());
        List<Ticket> ticketsUnder10Min = new ArrayList<>();
        for (Ticket ticket : ticketsInCart) {
            if (Math.abs(ChronoUnit.MINUTES.between(ticket.getCreationDate(), LocalDateTime.now())) < 10) {
                ticketsUnder10Min.add(ticket);
            }
        }
        return ticketsUnder10Min;
    }

    private boolean allTicketsInFuture(List<Ticket> tickets) {
        boolean allInFuture = true;
        if (tickets != null) {
            for (Ticket t : tickets) {
                allInFuture = t.getAct().getStart().isAfter(LocalDateTime.now());
            }
        }
        return allInFuture;
    }

    /**
     * Workaround to avoid TicketAcquisitions returning the same ticket 5 times in the list of tickets returned from the db.
     *
     * @param ta TicketAcquisition with duplicate tickets
     * @return TicketAcquisition without the duplicate tickets
     */
    private TicketAcquisition removeDuplicateTickets(TicketAcquisition ta) {
        List<Ticket> finalTickets = new ArrayList<>();

        if (ta.getTickets() == null) {
            return ta;
        }

        // Load lazy collection:
        ta.getTickets().size();
        for (Ticket t : ta.getTickets()) {
            if (!listContainsTicket(finalTickets, t)) {
                finalTickets.add(t);
            }
        }

        ta.setTickets(finalTickets);
        return ta;
    }

    private boolean listContainsTicket(List<Ticket> tickets, Ticket ticket) {
        for (Ticket t : tickets) {
            if (Objects.equals(t.getId(), ticket.getId())) {
                return true;
            }
        }
        return false;
    }

    private ApplicationUser getUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userRepository.findApplicationUserByEmail(username);
        if (user == null || user.getId() == null) {
            throw new FatalException("Cannot find an existing user");
        }
        return user;
    }
}
