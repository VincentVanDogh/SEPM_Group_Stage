package at.ac.tuwien.sepm.groupphase.backend.service.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsWithPricesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketAcquisitionValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserService userService;
    private final TicketService ticketService;

    public TicketAcquisitionValidator(UserService userService, TicketService ticketService) {
        this.userService = userService;
        this.ticketService = ticketService;
    }

    public void validateForCancellation(TicketAcquisitionDetailsWithPricesDto ticketAcquisitionDto) throws ValidationException, ConflictException, ForbiddenException {
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();
        List<Long> ticketIds =
            ticketAcquisitionDto.tickets().stream().map(TicketDetailsDto::getId).collect(Collectors.toList());

        //get & check user
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userService.findApplicationUserByEmail(username);
        if (user == null) {
            conflictErrors.add("User does not exist in system!");
        }
        // TODO: Write better query - This one throws exceptions for correct objects
        /*List<ApplicationUser> involvedUsers = userService.getAllUsersForTicketIds(ticketIds);
        if (involvedUsers.isEmpty()) {
            if (ticketAcquisitionDto.tickets().size() < 1) {
                //no tickets were sent
                validationErrors.add("No tickets to cancel sent");
            } else {
                //something failed when saving tickets
                throw new RuntimeException("Some tickets not properly linked to buyer");
            }
        } else if (involvedUsers.size() > 1 || !involvedUsers.get(0).getId().equals(user.getId())) {
            //should this be allowed if logged in user is admin?
            throw new ForbiddenException("Attempting to modify tickets of different user");
        }*/


        List<Ticket> foundTicketsList = ticketService.getAllTicketsForTicketIds(ticketIds);
        if (foundTicketsList.size() < ticketIds.size()) {
            //Not all tickets found
            conflictErrors.add("Some tickets to cancel were not found");

        }
        //none of the ticket must belong to acts that already happened
        for (Ticket t : foundTicketsList) {
            if (LocalDate.now().isAfter(t.getAct().getStart().toLocalDate())) {
                validationErrors.add("At least one Ticket belongs to an Act that has already happened");
                break;
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of cancellation failed", validationErrors);
        }
        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Cancellation failed to be saved in system due to potential conflict(s)",
                conflictErrors);
        }
    }
}
