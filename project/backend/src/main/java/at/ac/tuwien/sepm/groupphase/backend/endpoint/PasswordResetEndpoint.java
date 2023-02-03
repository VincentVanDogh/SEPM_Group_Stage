package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Emails;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.naming.TimeLimitExceededException;
import java.lang.invoke.MethodHandles;


@RestController
@RequestMapping(value = "/api/v1/reset")
public class PasswordResetEndpoint {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/reset";

    public PasswordResetEndpoint(UserService userService) {
        this.userService = userService;
    }


    @PermitAll
    @PostMapping("/initiate")
    @ResponseStatus(HttpStatus.OK)
    public String initiateResetPassword(@RequestBody Emails value) {
        LOGGER.info("POST " + BASE_PATH + "/initiate");
        LOGGER.debug("initiateResetPassword({})", value);
        return this.userService.initiateResetPassword(value);
    }

    @Transactional
    @PermitAll
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public String resetPassword(@RequestBody NewPasswordDto newPassword) throws ValidationException {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("resetPassword({})", newPassword);
        return this.userService.resetPassword(newPassword);
    }

}
