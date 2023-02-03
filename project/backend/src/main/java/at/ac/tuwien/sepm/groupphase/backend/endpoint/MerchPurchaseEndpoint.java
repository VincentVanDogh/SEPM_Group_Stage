package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleQuantityDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchPurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = MerchPurchaseEndpoint.BASE_PATH)
public class MerchPurchaseEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "api/v1/merch_purchase";
    private final MerchPurchaseService service;

    public MerchPurchaseEndpoint(MerchPurchaseService service) {
        this.service = service;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get merchandises (optionally based on status) stored in the persistent data store",
            security = @SecurityRequirement(name = "apiKey"))
    public List<MerchPurchaseDto> getMerchPurchases(@RequestParam(required = false) Boolean purchased) {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("getMerchPurchases({})", purchased);
        return service.getMerchPurchases(purchased);
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Store merch articles as merch purchases", security = @SecurityRequirement(name = "apiKey"))
    public MerchPurchaseDto saveMerchArticleAsPurchase(@RequestBody MerchArticleQuantityDto merchArticleQuantityDto) {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("saveMerchArticleAsPurchase({})", merchArticleQuantityDto);
        try {
            return service.saveMerchArticleInShoppingList(merchArticleQuantityDto);
        } catch (FatalException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST; // 400
            logClientError(status, "Syntactically invalid POST request for saving merch article in shopping cart", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND; // 422
            logClientError(status, "Invalid POST request a purchase article to the purchase in shopping cart", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422
            logClientError(status, "Invalid POST request for saving a merch article in the shopping cart", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @PutMapping
    @Operation(summary = "Edit merch article in the shopping cart", security = @SecurityRequirement(name = "apiKey"))
    public MerchPurchaseDto storeMerchPurchaseInShoppingCart(@RequestBody MerchArticleQuantityDto quantityDto) {
        LOGGER.info("PUT " + BASE_PATH);
        LOGGER.debug("storeMerchPurchaseInShoppingCart({})", quantityDto);
        try {
            return service.editArticle(quantityDto);
        } catch (FatalException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST; // 400
            logClientError(status, "Syntactically invalid PUT request for editing article in shopping cart", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @DeleteMapping("{id}")
    @Operation(summary = "Delete merch article", security = @SecurityRequirement(name = "apiKey"))
    public void deleteMerchArticle(@PathVariable Long id) {
        LOGGER.info("DELETE " + BASE_PATH + "/{}", id);
        LOGGER.debug("deleteMerchArticle({})", id);
        try {
            this.service.deleteMerchArticle(id);
        } catch (FatalException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST; // 400
            logClientError(status, "Syntactically invalid DELETE request for merch article in shopping cart", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND; // 422
            logClientError(status, "Invalid POST request for a horse", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    @Operation(summary = "Delete all merch articles stored in the shopping cart",
            security = @SecurityRequirement(name = "apiKey"))
    public void deleteAllMerchArticlesInCart() {
        LOGGER.info("DELETE " + BASE_PATH);
        LOGGER.debug("deleteAllMerchArticlesInCart()");
        try {
            this.service.deleteAllMerchArticlesInCart();
        } catch (FatalException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST; // 400
            logClientError(status, "Syntactically invalid DELETE request for all merch articles in shopping cart", e);
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
