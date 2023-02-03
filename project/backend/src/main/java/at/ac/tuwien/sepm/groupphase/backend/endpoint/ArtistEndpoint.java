package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = ArtistEndpoint.basePath)
public class ArtistEndpoint {

    static final String basePath = "/api/v1/artists";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService service;

    public ArtistEndpoint(ArtistService service) {
        this.service = service;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Save an artist in the database", security = @SecurityRequirement(name = "apiKey"))
    public ArtistDto save(@Valid @RequestBody ArtistDto artistDto) {
        LOGGER.info("POST " + basePath);
        LOGGER.debug("save({})", artistDto);

        return service.save(artistDto);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get all artists from the database", security = @SecurityRequirement(name = "apiKey"))
    public Stream<ArtistDto> getAll() {
        LOGGER.info("GET " + basePath);
        LOGGER.debug("getAll()");

        return service.getAll();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search")
    @Operation(summary = "Search for all artists containing specific term in one of their names", security = @SecurityRequirement(name = "apiKey"))
    public Stream<ArtistDto> searchByName(String searchParams) throws ValidationException {
        LOGGER.info("Get " + basePath + "/search");
        LOGGER.debug("searchByName({})", searchParams);

        return service.searchByName(searchParams);
    }
}
