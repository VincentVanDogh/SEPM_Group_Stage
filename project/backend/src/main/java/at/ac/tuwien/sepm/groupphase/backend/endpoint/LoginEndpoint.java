package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import javax.annotation.security.PermitAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/authentication")
public class LoginEndpoint {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/authentication";

    public LoginEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("login({})", userLoginDto);
        try {
            return userService.login(userLoginDto);
        } catch (BadCredentialsException e) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }

    }
}
