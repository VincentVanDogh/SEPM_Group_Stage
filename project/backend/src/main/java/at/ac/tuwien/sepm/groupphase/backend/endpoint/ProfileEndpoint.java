package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api/v1/profile")
public class ProfileEndpoint {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/profile";

    public ProfileEndpoint(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @PutMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_USER")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public String edit(@RequestBody RegistrationDto registrationDto) throws ValidationException {
        LOGGER.info("PUT " + BASE_PATH + "/edit");
        LOGGER.debug("edit({})", registrationDto);

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findApplicationUserByEmail(username).getId();
        try {
            return userService.edit(registrationDto, userId);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_USER")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public String editPassword(@RequestBody PasswordDto passwordDto) throws ValidationException {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("editPassword({})", passwordDto);

        ArrayList<String> errors = new ArrayList<>();
        if (passwordDto.getNewPassword() == null
            || passwordDto.getOldPassword() == null
            || passwordDto.getConfirmation() == null) {
            errors.add("Password can not be null");
            throw new ValidationException("Erorr setting password!", errors);
        }
        if (!(passwordDto.getNewPassword().equals(passwordDto.getConfirmation()))) {
            errors.add("Passwords do not match");
            throw new ValidationException("Passwords do not match", errors);
        }
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findApplicationUserByEmail(username).getId();

        try {
            return userService.editPassword(passwordDto, userId);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @DeleteMapping()
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_USER")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public void delete() {
        LOGGER.info("DELETE " + BASE_PATH);
        LOGGER.debug("delete()");

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = userService.findApplicationUserByEmail(email);
        try {
            userService.delete(currentUser.getId());
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Transactional
    @GetMapping()
    @Secured("ROLE_USER")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public RegistrationDto get() {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("get()");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = userService.findApplicationUserByEmail(email);
        try {
            return userMapper.applicationUserToRegistrationDto(userService.getById(currentUser.getId()));
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("bonus_points")
    @Operation(summary = "Retrieve the bonus point balance of the logged-in user", security = @SecurityRequirement(name = "apiKey"))
    public Long getPoints() {
        LOGGER.info("GET " + BASE_PATH + "/bonus_points");
        LOGGER.debug("getPoints()");
        return userService.getBonusPoints();
    }

    @Secured("ROLE_USER")
    @GetMapping("bonus_points_cart")
    @Operation(summary = "Retrieve the bonus point balance within cart of the logged-in user", security = @SecurityRequirement(name = "apiKey"))
    public Long getCartPoints() {
        LOGGER.info("GET " + BASE_PATH + "/bonus_points_carts");
        LOGGER.debug("getCartPoints()");
        return userService.getCartBonusPoints();
    }
}
