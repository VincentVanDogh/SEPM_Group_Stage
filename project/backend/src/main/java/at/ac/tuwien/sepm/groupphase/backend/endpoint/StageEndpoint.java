package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StageDto;
import at.ac.tuwien.sepm.groupphase.backend.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = StageEndpoint.basePath)
public class StageEndpoint {

    static final String basePath = "/api/v1/stages";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StageService service;

    public StageEndpoint(StageService service) {
        this.service = service;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get all locations with specific Ids", security = @SecurityRequirement(name = "apiKey"))
    public Stream<StageDto> getByLocationId(Long locationId) {
        LOGGER.info("GET " + basePath);
        LOGGER.debug("getByLocationId({})", locationId);

        return service.getByLocationId(locationId);
    }
}
