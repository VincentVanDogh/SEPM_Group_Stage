package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationPageDto;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketAcquisitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "api/v1/reservation")
public class ReservationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketAcquisitionService ticketAcquisitionService;
    static final String BASE_PATH = "/api/v1/reservation";

    @Autowired
    public ReservationEndpoint(TicketAcquisitionService ticketAcquisitionService) {
        this.ticketAcquisitionService = ticketAcquisitionService;
    }

    @Transactional
    @Secured("ROLE_USER")
    @GetMapping(value = "/all/{pageId}")
    @Operation(summary = "Get all reservations of one user saved in system", security = @SecurityRequirement(name = "apiKey"))
    public ReservationPageDto getAllReservationsForUser(@PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/all/{}", pageId);
        LOGGER.debug("getAllReservationsForUser({})", pageId);
        return ticketAcquisitionService.getAllReservationsForUser(pageId);
    }

    @Transactional
    @Secured("ROLE_USER")
    @GetMapping(value = "/current/{pageId}")
    @Operation(summary = "Get all reservations containing tickets for future acts of one user saved in system", security = @SecurityRequirement(name = "apiKey"))
    public ReservationPageDto getReservationsForUser(@PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/current/{}", pageId);
        LOGGER.debug("getReservationsForUser({})", pageId);
        return ticketAcquisitionService.getAllCurrentReservationsForUser(pageId);
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
