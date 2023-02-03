package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActStagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapperAlt;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketAcquisitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StagePlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.validation.TicketValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.HashSet;

@Service
@EnableScheduling
public class TicketServiceImpl implements TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketRepository ticketRepository;
    private final TicketMapperAlt ticketMapper;
    private final StagePlanMapper stagePlanMapper;
    private final TicketValidator ticketValidator;
    private final UserRepository userRepository;
    private final TicketAcquisitionRepository ticketAcquisitionRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             TicketMapperAlt ticketMapper,
                             StagePlanMapper stagePlanMapper,
                             TicketValidator ticketValidator,
                             UserRepository userRepository,
                             TicketAcquisitionRepository ticketAcquisitionRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.stagePlanMapper = stagePlanMapper;
        this.ticketValidator = ticketValidator;
        this.userRepository = userRepository;
        this.ticketAcquisitionRepository = ticketAcquisitionRepository;
    }

    @Override
    public TicketDto get(Long id) throws ValidationException, NotFoundException {
        if (id == null) {
            List<String> error = new ArrayList<>();
            error.add("ID of ticket must be given in order to cancel!");
            throw new ValidationException("Could not get ticket!", error);
        }

        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new NotFoundException("Ticket with ID number " + id + " could not be found");
        }
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userRepository.findApplicationUserByEmail(username);
        if (!Objects.equals(ticket.get().getBuyer().getId(), user.getId()) && !user.getAdmin()) {
            throw new ForbiddenException("User does not have the right to fetch this ticket!");
        }

        return ticketMapper.ticketToTicketDto(ticket.get());
    }

    @Override
    public List<TicketDto> getAll() {
        return ticketMapper.ticketToTicketDto(ticketRepository.findAll());
    }

    @Override
    public List<TicketDetailsDto> getAllDetailedTickets() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();
        LocalDateTime maxTime = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        return ticketMapper.ticketToTicketDetailsDto(
            ticketRepository
                .findTicketsByBuyerIdAndTicketOrderIsNullAndReservationNotAndCreationDateIsAfter(
                    userId, TicketStatus.INITIALISED, maxTime
                )
        );
    }

    @Override
    public List<TicketDetailsDto> getAllDetailedReservations() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();
        LocalDateTime maxTime = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        return ticketMapper.ticketToTicketDetailsDto(ticketRepository
            .findTicketsByBuyerIdAndTicketOrderIsNullAndReservationAndCreationDateIsAfter(userId, TicketStatus.RESERVED, maxTime));
    }

    @Override
    public List<TicketDetailsDto> getAllDetailedPurchases() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userRepository.findApplicationUserByEmail(username).getId();
        LocalDateTime maxTime = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        return ticketMapper.ticketToTicketDetailsDto(ticketRepository
            .findTicketsByBuyerIdAndTicketOrderIsNullAndReservationAndCreationDateIsAfter(userId, TicketStatus.PURCHASED, maxTime));
    }

    @Override
    public ActStagePlanDto claimSeat(TicketDto ticket) throws ValidationException, ConflictException {
        ticketValidator.validateForSaving(ticket);
        Ticket claimedTicket = ticketRepository.save(ticketMapper.ticketDtoToTicket(ticket));
        return stagePlanMapper.toActStagePlanDto(claimedTicket.getAct());
    }

    @Override
    public TicketDto save(TicketDto ticket) throws ValidationException, ConflictException {
        ticketValidator.validateForSaving(ticket);
        ticket.setCreationDate(LocalDateTime.now());
        return ticketMapper.ticketToTicketDto(ticketRepository.save(ticketMapper.ticketDtoToTicket(ticket)));
    }

    public List<TicketDto> save(List<TicketDto> ticket) throws ValidationException, ConflictException {
        List<TicketDto> toReturn = new ArrayList<>();
        for (TicketDto t : ticket) {
            toReturn.add(save(t));
        }
        return toReturn;
    }

    @Override
    public TicketDto update(TicketDto ticketDto) throws ValidationException, ConflictException, NotFoundException {
        ticketValidator.validateForUpdate(ticketDto);
        return ticketMapper.ticketToTicketDto(ticketRepository.save(ticketMapper.ticketDtoToTicket(ticketDto)));
    }

    public List<TicketDto> update(List<TicketDto> ticket) throws ValidationException, ConflictException, NotFoundException {
        List<TicketDto> toReturn = new ArrayList<>();
        for (TicketDto t : ticket) {
            toReturn.add(update(t));
        }
        return toReturn;
    }

    @Override
    public void delete(Long id) {
        Optional<Ticket> fromDb = ticketRepository.findById(id);
        if (fromDb.isPresent()) {
            Ticket extracted = fromDb.get();

            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ApplicationUser user = userRepository.findApplicationUserByEmail(username);
            if (!Objects.equals(extracted.getBuyer().getId(), user.getId()) && !user.getAdmin()) {
                throw new ForbiddenException("User does not have the right to delete this ticket!");
            }

            if (extracted.getTicketOrder() == null) {
                ticketRepository.deleteById(id);
            } else {
                extracted.setCancelled(true);
                ticketRepository.save(extracted);

                Long orderId = extracted.getTicketOrder().getId();
                Optional<TicketAcquisition> orderOpt = ticketAcquisitionRepository.findById(orderId);
                if (orderOpt.isPresent()) {
                    TicketAcquisition order = orderOpt.get();
                    List<Ticket> ticketsOfOrder = ticketRepository.findTicketsByTicketOrderId(order.getId());
                    if (ticketsOfOrder.size() == 1) {
                        order.setCancelled(true);
                        ticketAcquisitionRepository.save(order);
                    } else {
                        boolean allCancelled = true;
                        for (Ticket t : ticketsOfOrder) {
                            if (!t.isCancelled()) {
                                allCancelled = false;
                                break;
                            }
                        }
                        order.setCancelled(allCancelled);
                        ticketAcquisitionRepository.save(order);
                    }
                }
            }
        } else {
            throw new NotFoundException("Ticket to delete was not found in system!");
        }
    }

    @Override
    public void deleteAllTicketsInCart(String status) throws ValidationException {
        List<Ticket> ticketsInCart;
        if (status == null) {
            ticketsInCart = ticketRepository.findTicketsByBuyerIdAndTicketOrderIsNull(getUser().getId());
        } else {
            String ticketStatus = status.toUpperCase();
            if (!ticketStatus.equals("RESERVED") && !ticketStatus.equals("PURCHASED")) {
                throw new ValidationException(
                        "Error deleting all tickets from the shopping cart with the status: \"" + ticketStatus + "\"",
                        List.of("Provided ticket status must be either \"RESERVED\" or \"PURCHASED\"")
                );
            }
            ticketsInCart = ticketRepository.findTicketsByBuyerIdAndTicketOrderIsNullAndReservation(
                    getUser().getId(),
                    TicketStatus.valueOf(status)
            );
        }
        for (Ticket ticket : new HashSet<>(ticketsInCart)) {
            ticketRepository.deleteById(ticket.getId());
        }
    }

    @Override
    public void buyReservedTicket(Long id) throws NotFoundException, ForbiddenException {
        // Fetch ticket and its TA from db
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isEmpty()) {
            throw new NotFoundException("Ticket could not be found in the system!");
        }

        Ticket ticket = ticketOpt.get();
        /*Optional<TicketAcquisition> taOpt = ticketAcquisitionRepository.findById(ticket.getTicketOrder().getId());
        if (taOpt.isEmpty()) {
            throw new NotFoundException("Ticket acquisition could not be found in the system!");
        }*/

        // Remove ticket acquisition id in ticket, set creation date of ticket to now
        // Check if TA is empty after deletion, if empty, delete from db
        // Order changed because we need ticket order ID number to check tickets in ticket order
        List<Ticket> allTicketsInTicketAcquisition = ticketRepository.findTicketsByTicketOrderId(ticket.getTicketOrder().getId());
        if (allTicketsInTicketAcquisition.size() == 1) {
            ticketAcquisitionRepository.deleteById(ticket.getTicketOrder().getId());
        }
        ticket.setReservation(TicketStatus.PURCHASED);
        ticket.setTicketOrder(null);
        ticket.setCreationDate(LocalDateTime.now());

        // Update ticket in db
        ticketRepository.save(ticket);
    }

    public Integer getPrice(Long id) throws NotFoundException {
        Integer result = ticketRepository.getPriceForTicket(id);
        if (result == null) {
            throw new NotFoundException("Ticket with id %d not found".formatted(id));
        } else {
            return result;
        }

    }

    public List<Ticket> getAllTicketsForTicketIds(List<Long> ticketIds) {
        return ticketRepository.findAllByIds(ticketIds);
    }

    //@Scheduled(fixedRate = 86400000) // Once a day, 24*60*60*1000 milliseconds
    //@Scheduled(fixedRate = 10000) // every 10 seconds
    @Scheduled(fixedRate = 60000) // every 60 seconds
    //@Scheduled(fixedRate = 900000) // every 15 minutes, 15*60*1000 milliseconds
    public void cleanDatabaseOfOldTickets() {
        LocalDateTime maxTime = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        List<Ticket> toDelete = ticketRepository.findTicketsByCreationDateIsBeforeAndTicketOrderIsNull(maxTime);

        for (Ticket t : toDelete) {
            ticketRepository.deleteById(t.getId());
        }
    }

    private ApplicationUser getUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userRepository.findApplicationUserByEmail(username);
        if (user == null) {
            throw new FatalException("Cannot find an existing user");
        }
        return user;
    }
}
