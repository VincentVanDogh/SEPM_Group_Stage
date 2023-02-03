package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.ActService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = ActEndpoint.basePath)
public class ActEndpoint {

    static final String basePath = "/api/v1/acts";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ActService service;

    public ActEndpoint(ActService service) {
        this.service = service;
    }

    @PermitAll
    @GetMapping("{id}")
    public Stream<ActDto> getActsByEventId(@PathVariable Long id) {
        LOGGER.info("GET " + basePath + "{}", id);
        LOGGER.debug("getActsByEventId({})", id);

        return service.getByEventId(id);
    }

    @PermitAll
    @GetMapping("search/{id}")
    public Stream<ActDto> searchActsByEventId(@PathVariable Long id, SearchActDto searchParams) throws ValidationException {
        LOGGER.info("GET " + basePath + "/search/{}", id);
        LOGGER.debug("searchActsByEventId({}, {})", id, searchParams);

        return service.searchActsByEventId(id, searchParams).distinct();
    }
}
