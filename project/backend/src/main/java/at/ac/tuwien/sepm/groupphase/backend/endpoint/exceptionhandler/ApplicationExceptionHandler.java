package at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ConflictErrorRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ValidationErrorRestDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.invoke.MethodHandles;

/**
 * Handler class for handling exceptions that arise during validation.
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Method handles {@link ValidationException}s, which are exceptions that arise when data sent to the server is invalid
     * and violates some constraint and therefore cannot be saved in the persistent data store.
     *
     * @param e The exception being handled
     * @return A message to be sent to the frontend detailing the errors in the DTO sent to the server.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ValidationErrorRestDto handleValidationException(ValidationException e) {
        LOG.warn("Terminating request processing with status 422 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ValidationErrorRestDto(e.summary(), e.errors());
    }

    /**
     * Method handles {@link ConflictException}s, which are exceptions that arise when data sent to the server is valid,
     * but conflicts with the current state of the server and cannot be saved in the persistent data store for this reason.
     *
     * @param e The exception being handled
     * @return A message to be sent to the frontend detailing the errors in the DTO sent to the server.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ConflictErrorRestDto handleConflictException(ConflictException e) {
        LOG.warn("Terminating request processing with status 409 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ConflictErrorRestDto(e.summary(), e.errors());
    }
}
