package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShoppingCartDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = ShoppingCartEndpoint.BASE_PATH)
public class ShoppingCartEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "api/v1/shopping_cart";
    private final ShoppingCartService cartService;

    public ShoppingCartEndpoint(ShoppingCartService cartService) {
        this.cartService = cartService;
    }

    @Transactional
    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Finalize purchase of tickets and merch stored in the shopping cart",
            security = @SecurityRequirement(name = "apiKey"))
    public ShoppingCartDto finalizePurchase(@RequestBody CreateInvoiceDto invoiceDto) throws ConflictException {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("finalizePurchase({})", invoiceDto);
        try {
            return cartService.finalizePurchase(invoiceDto);
        } catch (FatalException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST; // 400
            logClientError(status, "Syntactically invalid POST request to the shopping cart endpoint", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422
            logClientError(status, "Invalid POST request to the shopping cart endpoint", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Transactional
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get current shopping cart", security = @SecurityRequirement(name = "apiKey"))
    public ShoppingCartDto getShoppingCart() {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("getShoppingCart()");
        return cartService.getShoppingCart();
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
