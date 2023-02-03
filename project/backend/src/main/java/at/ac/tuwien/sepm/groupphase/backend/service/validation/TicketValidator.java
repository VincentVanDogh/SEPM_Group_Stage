package at.ac.tuwien.sepm.groupphase.backend.service.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class TicketValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final ActRepository actRepository;
    private final SectorMapRepository sectorMapRepository;
    private final TicketRepository ticketRepository;

    public TicketValidator(UserRepository userRepository,
                           ActRepository actRepository,
                           SectorMapRepository sectorMapRepository,
                           TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.actRepository = actRepository;
        this.sectorMapRepository = sectorMapRepository;
        this.ticketRepository = ticketRepository;
    }

    public void validateForSaving(TicketDto ticket) throws ValidationException, ConflictException {
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();

        // USER
        if (ticket.getBuyerId() != null) {
            if (userRepository.findById(ticket.getBuyerId()).isEmpty()) {
                conflictErrors.add("Buyer is not saved in the system");
            }
        } else {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                ApplicationUser user = userRepository.findApplicationUserByEmail(username);
                if (user == null) {
                    conflictErrors.add("User does not exist in system!");
                }
            /*if (!user.getAdmin() && (!Objects.equals(user.getId(), ticket.getId())) && ticket.getId() != null) {
                conflictErrors.add("User saving ticket is not buyer!");
            }*/
            } else {
                validationErrors.add("Buyer is not identifiable!");
            }
        }

        // ACT
        if (ticket.getActId() == null) {
            validationErrors.add("No act ID number specified!");
        } else {
            Optional<Act> fromRepo = actRepository.findById(ticket.getActId());
            if (fromRepo.isPresent()) {
                // Act exists in the system
                Act extracted = fromRepo.get();

                // Check if act is in the past
                if (extracted.getStart().isBefore(LocalDateTime.now())) {
                    conflictErrors.add("Act is in the past!");
                }
            } else {
                conflictErrors.add("Act does not exist!");
            }
        }

        // FIRST NAME OF PERSON ON TICKET
        if (ticket.getTicketFirstName() == null) {
            validationErrors.add("First name of ticket owner not given!");
        } else {
            if (ticket.getTicketFirstName().length() > 4095) {
                validationErrors.add("First name of ticket owner too long: over 4095 characters!");
            }
            if (!ticket.getTicketFirstName().matches("^[0-9A-Za-z]+$")) {
                validationErrors.add("First name of ticket owner contains forbidden characters: Only letters, numbers, dashes and spaces allowed");
            }
        }

        // LAST NAME OF PERSON ON TICKET
        if (ticket.getTicketLastName() == null) {
            validationErrors.add("Last name of ticket owner not given!");
        } else {
            if (ticket.getTicketLastName().length() > 4095) {
                validationErrors.add("Last name of ticket owner too long: over 4095 characters!");
            }
            if (!ticket.getTicketLastName().matches("^[0-9A-Za-z]+$")) {
                validationErrors.add("First name of ticket owner contains forbidden characters: Only letters, numbers, dashes and spaces allowed");
            }
        }

        // SEAT NUMBER
        if (ticket.getSeatNo() == null) {
            validationErrors.add("No seat number given!");
        }

        // SECTION
        if (ticket.getSectorMapId() == null) {
            validationErrors.add("No sector given!");
        } else {
            // Check if sector map exists in system
            Optional<SectorMap> sectorMapFetched = sectorMapRepository.findById(ticket.getSectorMapId());
            if (sectorMapFetched.isEmpty()) {
                conflictErrors.add("Sector map is not saved in system!");
            }
        }

        // PURCHASE/RESERVATION
        if (ticket.getReservation() == null) {
            validationErrors.add("Ticket does not have a reservation status!");
        } else {
            if (ticket.getId() != null) {
                Optional<Ticket> ticketCheck = ticketRepository.findById(ticket.getId());
                if (ticketCheck.isPresent()) {
                    // If ticket already exists in system
                    Ticket ticketCheckExtracted = ticketCheck.get();
                    if (ticketCheckExtracted.getReservation() == TicketStatus.PURCHASED && ticketCheckExtracted.getTicketOrder() != null) {
                        conflictErrors.add("Ticket has already been purchased!");
                    }
                    /*if (!ticket.isReservation() && ticketCheckExtracted.isReservation() && details.getId() == null) {
                        validationErrors.add("Ticket reservation ID number must be provided!");
                    }*/
                }
            }
        }

        // CANCELLATION
        if (ticket.getReservation() == TicketStatus.PURCHASED && ticket.isCancelled()) {
            validationErrors.add("New ticket cannot be cancelled!");
            // Reservation can be cancelled
        }
        /*if (!ticket.isCancelled() && details.isCancelled()) {
            validationErrors.add("Ticket is cancelled while order is not cancelled!");
        }*/

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of ticket for saving failed", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Ticket failed to be saved in system due to potential conflict(s)", conflictErrors);
        }
    }

    public void validateForUpdate(TicketDto ticket) throws ValidationException, ConflictException, NotFoundException {
        validateForSaving(ticket);

        List<String> conflictErrors = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();

        if (ticket.getId() != null) {
            Optional<Ticket> fromDb = ticketRepository.findById(ticket.getId());
            if (fromDb.isEmpty()) {
                throw new NotFoundException("Ticket with ID " + ticket.getId() + " does not exist in system!");
            }
        } else {
            validationErrors.add("No ticket ID provided!");
        }

        // Check if user logged in is buyer (Normal user cannot manage tickets of other buyers)
        if (ticket.getBuyerId() != null) {
            Optional<ApplicationUser> buyer = userRepository.findById(ticket.getBuyerId());
            if (buyer.isPresent()) {
                String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                ApplicationUser user = userRepository.findApplicationUserByEmail(username);
                if (!user.equals(buyer.get()) && !user.getAdmin()) {
                    conflictErrors.add("Only specific user and admin can manage tickets belonging to that user");
                }
            } else {
                conflictErrors.add("Buyer does not exist in system!");
            }
        } else {
            validationErrors.add("Buyer ID is not provided!");
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Ticket failed to be saved in system due to potential conflict(s)", conflictErrors);
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of ticket for saving failed", validationErrors);
        }
    }

}
