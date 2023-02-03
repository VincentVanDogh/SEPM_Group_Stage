package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderPageDto;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = OrderEndpoint.BASE_PATH)
public class OrderEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/order";
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderEndpoint(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{pageId}")
    @Transactional
    @Operation(summary = "Get stream of orders linked to a user", security = @SecurityRequirement(name = "apiKey"))
    public OrderPageDto getAll(@PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/{}", pageId);
        LOGGER.debug("getAll({})", pageId);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findApplicationUserByEmail(username).getId();
        return orderService.getAll(userId, pageId);
    }

}
