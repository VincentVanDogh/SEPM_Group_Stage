package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArticlePurchaseMappingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = MerchArticleEndpoint.BASE_PATH)
public class MerchArticleEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "api/v1/merch_article";
    private final MerchArticleService service;

    public MerchArticleEndpoint(MerchArticleService service) {
        this.service = service;
    }

    @PermitAll
    @GetMapping("/all/page/{pageId}")
    public MerchPageDto getAllMerchArticles(MerchArticleSearchDto searchDto, @PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/all/page/{}", pageId);
        LOGGER.debug("getAllMerchArticles({}, {})", searchDto, pageId);
        return service.getAllMerchArticles(searchDto, pageId);
    }

    @PermitAll
    @GetMapping("/all/search/{pageId}")
    public MerchPageDto searchMerchArticles(@RequestBody MerchArticleSearchDto searchDto, @PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/all/search/{}", pageId);
        LOGGER.debug("searchMerchArticles({}, {})", searchDto, pageId);
        return service.searchMerchArticles(searchDto, pageId);
    }

    @Secured("ROLE_USER")
    @GetMapping("user")
    public Stream<ArticlePurchaseMappingDto> getAllMerchArticlesInRelationToUser() {
        LOGGER.info("GET " + BASE_PATH + "/user");
        LOGGER.debug("getAllMerchArticlesInRelationToUser()");
        return service.getAllMerchArticlesInRelationToUser().stream();
    }

    @PermitAll
    @GetMapping("/all/{id}")
    public ArticlePurchaseMappingDto getMerchArticleForUser(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_PATH + "/all/{}", id);
        LOGGER.debug("getMerchArticleForUser({})", id);
        try {
            return service.getMerchArticleInRelationToUser(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Article with id " + id + " not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("{id}")
    public ArticlePurchaseMappingDto getMerchArticle(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        LOGGER.debug("getMerchArticle({})", id);
        try {
            return service.getMerchArticleInRelationToUser(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Article with id " + id + " not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Add a merchandise article to the persistent data storage", security = @SecurityRequirement(name = "apiKey"))
    public MerchArticleDto saveMerchArticle(@RequestBody MerchArticleDto merchArticleDto) {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("saveMerchArticle({})", merchArticleDto);
        return service.saveMerchArticle(merchArticleDto);
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
