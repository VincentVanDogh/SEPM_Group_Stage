package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = TicketEndpoint.BASE_PATH)
public class TicketEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "api/v1/ticket";
    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "{id}")
    @Operation(summary = "Get a specific ticket by ID number", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto get(@PathVariable Long id) throws NotFoundException, ValidationException {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        LOGGER.debug("get({})", id);
        try {
            return ticketService.get(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Ticket not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }  catch (ForbiddenException e) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            logClientError(status, "User does not have permission to view ticket", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("admin")
    @Operation(summary = "Get all tickets saved in system", security = @SecurityRequirement(name = "apiKey"))
    public Stream<TicketDto> getAll() {
        LOGGER.info("GET " + BASE_PATH + "/admin");
        LOGGER.debug("getAll()");
        return ticketService.getAll().stream();
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get all unfinalised tickets of a user", security = @SecurityRequirement(name = "apiKey"))
    public Stream<TicketDetailsDto> getAllUser() {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("getAllUser()");
        // return Stream.concat(getAllUserPurchases(), getAllUserReservations());
        return ticketService.getAllDetailedTickets().stream();
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/purchases")
    @Operation(summary = "Get all unfinalised tickets of a user that are to be purchases", security = @SecurityRequirement(name = "apiKey"))
    public Stream<TicketDetailsDto> getAllUserPurchases() {
        LOGGER.info("GET " + BASE_PATH + "/purchases");
        LOGGER.debug("getAllUserPurchases()");
        return ticketService.getAllDetailedPurchases().stream();
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/reservations")
    @Operation(summary = "Get all unfinalised tickets of a user that are to be reserved", security = @SecurityRequirement(name = "apiKey"))
    public Stream<TicketDetailsDto> getAllUserReservations() {
        LOGGER.info("GET " + BASE_PATH + "/reservations");
        LOGGER.debug("getAllUserReservations()");
        return ticketService.getAllDetailedReservations().stream();
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a temporary ticket in database", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto save(@RequestBody TicketDto ticket) throws ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("save({})", ticket);
        return ticketService.save(ticket);
    }

    @Secured("ROLE_USER")
    @PutMapping
    @Operation(summary = "Update an existing (temporary) ticket in the database", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto update(@RequestBody TicketDto ticket) throws ValidationException, ConflictException {
        LOGGER.info("PUT " + BASE_PATH);
        LOGGER.debug("update({})", ticket);
        try {
            return ticketService.update(ticket);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Ticket not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "{id}")
    @Operation(summary = "Delete an existing (temporary) ticket with given ID in the database", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE " + BASE_PATH + "/{}", id);
        LOGGER.debug("delete({})", id);
        try {
            ticketService.delete(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Ticket not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ForbiddenException e) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            logClientError(status, "User does not have permission to view ticket", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    @Operation(
            summary = "Delete all tickets (based on their status) from the shopping cart",
            security = @SecurityRequirement(name = "apiKey")
    )
    public void deleteAllTicketsInCart(@RequestParam(required = false) String status) {
        LOGGER.info("DELETE " + BASE_PATH);
        LOGGER.debug("deleteAllTicketsInCart({})", status);
        try {
            ticketService.deleteAllTicketsInCart(status);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Secured("ROLE_USER")
    @PatchMapping(value = "buy_reservation/{id}")
    @Operation(summary = "Remove given reserved ticket from its ticket acquisition and place it in shopping cart", security = @SecurityRequirement(name = "apiKey"))
    public void buyReservedTicket(@PathVariable Long id) {
        LOGGER.info("PATCH " + BASE_PATH + "/buy_reservation/{}", id);
        LOGGER.debug("buyReservedTicket({})", id);
        try {
            ticketService.buyReservedTicket(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Ticket not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ForbiddenException e) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            logClientError(status, "User does not have permission to view ticket", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Method for logging client errors.
     *
     * @param status Http status code of the response sent
     * @param message Message sent to the client
     * @param e Exception that arose because of the error
     */
    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }
}
