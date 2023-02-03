package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchTop10Events;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;


@RestController
@RequestMapping(value = EventEndpoint.basePath)
public class EventEndpoint {

    static final String basePath = "/api/v1/events";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService service;
    private final EventMapper mapper;

    public EventEndpoint(EventService service, EventMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public CreateEventDto save(@Valid @RequestBody CreateEventDto createEventDto) {
        LOGGER.info("POST " + basePath);
        LOGGER.debug("save({})", createEventDto);

        return service.save(createEventDto);
    }

    @PermitAll
    @GetMapping("/search/all/{page}")
    public EventPageDto getAll(@PathVariable int page) {
        LOGGER.info("GET " + basePath + "/search/all/{}", page);
        LOGGER.debug("getAll({})", page);

        return new EventPageDto(service.getNumberOfPages(),
            service.getAll(page));
    }

    @Transactional
    @PermitAll
    @GetMapping("/search/page/{pageId}")
    public EventPageDto getSearchResults(SearchEventDto searchParams, @PathVariable int pageId) throws ValidationException {
        LOGGER.info("GET " + basePath + "search/page/{}", pageId);
        LOGGER.debug("getSearchResults({}, {})", searchParams, pageId);

        return new EventPageDto(service.getSearchSize(searchParams),
            service.searchEvents(searchParams, pageId));
    }

    @Transactional
    @PermitAll
    @GetMapping("/search/{id}")
    public EventDetailsDto getEventById(@PathVariable Long id) {
        LOGGER.info("GET " + basePath + "search/{}", id);
        LOGGER.debug("getEventById({})", id);

        return mapper.eventToEventDetailsDto(service.getById(id));
    }

    @PermitAll
    @GetMapping("/top10")
    public Stream<EventDetailsDto> getTop10(SearchTop10Events searchParams) throws ValidationException {
        LOGGER.info("GET" + basePath + "/top10");
        LOGGER.debug("getTop10({})", searchParams);

        return service.getTop10(searchParams);
    }
}
