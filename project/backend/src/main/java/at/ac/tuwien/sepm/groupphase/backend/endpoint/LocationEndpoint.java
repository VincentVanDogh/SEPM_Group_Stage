package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
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
@RequestMapping(value = LocationEndpoint.basePath)
public class LocationEndpoint {

    static final String basePath = "/api/v1/locations";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;

    public LocationEndpoint(LocationService locationService) {
        this.locationService = locationService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create new Location with Address and Stage", security = @SecurityRequirement(name = "apiKey"))
    public CreateLocationDto save(@Valid @RequestBody CreateLocationDto createLocationDto) {
        LOGGER.info("Post " + basePath);
        LOGGER.debug("save({})", createLocationDto);

        return locationService.save(createLocationDto);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get all locations rom the database", security = @SecurityRequirement(name = "apiKey"))
    public Stream<LocationDto> getAll() {
        LOGGER.info("GET " + basePath);
        LOGGER.debug("getAll()");

        return locationService.getAll();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search")
    @Operation(summary = "Search for all locations containing specific term in their venue name", security = @SecurityRequirement(name = "apiKey"))
    public Stream<LocationDto> searchByName(SearchLocationDto searchParams) throws ValidationException {
        LOGGER.info("GET " + basePath + "/search");
        LOGGER.debug("searchByName({})", searchParams);

        return locationService.searchByName(searchParams);
    }

}
