package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsWithPricesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketAcquisitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;


import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "api/v1/ticket_order")
public class TicketAcquisitionEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketAcquisitionService ticketAcquisitionService;
    static final String BASE_PATH = "/api/v1/ticket_order";

    @Autowired
    public TicketAcquisitionEndpoint(TicketAcquisitionService ticketAcquisitionService) {
        this.ticketAcquisitionService = ticketAcquisitionService;
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "{id}")
    @Operation(summary = "Get a specific order/reservation", security = @SecurityRequirement(name = "apiKey"))
    public TicketAcquisitionDto get(@PathVariable Long id) throws ValidationException, NotFoundException {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        try {
            return ticketAcquisitionService.get(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Order not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get all orders/reservations saved in the system", security = @SecurityRequirement(name = "apiKey"))
    public Stream<TicketAcquisitionDto> getAll() {
        LOGGER.info("GET " + BASE_PATH);
        return ticketAcquisitionService.getAll().stream();
    }

    @Transactional
    @Secured("ROLE_USER")
    @GetMapping(value = "/overview")
    @Operation(summary = "Get all orders/reservations of one user saved in system", security = @SecurityRequirement(name = "apiKey"))
    public Stream<TicketAcquisitionDetailsDto> getAllForUser() {
        LOGGER.info("GET " + BASE_PATH + "/overview");
        return ticketAcquisitionService.getAllForUser().stream();
    }

    @Transactional
    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save a ticket order/reservation", security = @SecurityRequirement(name = "apiKey"))
    public TicketAcquisitionDto save(@RequestBody TicketAcquisitionDto ticketAcq) throws ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH);
        return ticketAcquisitionService.save(ticketAcq);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "{purchId}")
    @Operation(summary = "Cancel ticket purchase", security = @SecurityRequirement(name = "apiKey"))
    public void cancel(@PathVariable Long purchId) throws ValidationException {
        LOGGER.info("DELETE " + BASE_PATH + "/{}", purchId);
        LOGGER.debug("cancel({})", purchId);
        try {
            ticketAcquisitionService.cancel(purchId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Ticket acquisition to cancel not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Transactional
    @Secured("ROLE_USER")
    @PostMapping(value = "cancel")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save a cancellation for the contained tickets", security = @SecurityRequirement(name = "apiKey"))
    public TicketAcquisitionDto cancel(@RequestBody TicketAcquisitionDetailsWithPricesDto ticketAcq) throws NotFoundException, ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH + "/cancel");
        LOGGER.debug("cancel({})", ticketAcq);
        return ticketAcquisitionService.cancel(ticketAcq);
    }

    @Secured("ROLE_USER")
    @PatchMapping(value = "buy_reservation/{id}")
    @Operation(summary = "Remove reserved tickets from ticket acquisition with given ID and place them in shopping cart", security = @SecurityRequirement(name = "apiKey"))
    public void buyReservedTickets(@PathVariable Long id) throws NotFoundException, ForbiddenException {
        LOGGER.info("PATCH " + BASE_PATH + "/buy_reservation/{}", id);
        LOGGER.debug("buyReservedTickets({})", id);
        try {
            ticketAcquisitionService.buyReservedTickets(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Ticket acquisition to buy reserved tickets from not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ForbiddenException e) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            logClientError(status, "Ticket acquisition to buy reserved tickets from belongs to other user", e);
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
