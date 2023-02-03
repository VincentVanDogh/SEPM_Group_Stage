package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Emails;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserForAdminEndpoint {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/users";


    public UserForAdminEndpoint(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public String createUser(@RequestBody UserCreateDto userCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("createUser({})", userCreateDto);
        if (!(userCreateDto.getPassword().equals(userCreateDto.getConfirmation()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
        }
        String encodedPassword = passwordEncoder.encode(userCreateDto.getPassword());
        userCreateDto.setPassword(encodedPassword);
        try {
            return userService.register(userMapper.userCreateDtoToApplicationUser((userCreateDto)));
        } catch (ConflictException e) {
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }


    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/{pageId}")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public UserPageDto getAll(@PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/{}", pageId);
        LOGGER.debug("getAll({})", pageId);
        return userService.getAllPagination(pageId);
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/search/{pageId}")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public UserPageDto search(UserDto searchParams, @PathVariable int pageId) {
        LOGGER.info("GET " + BASE_PATH + "/search/" + pageId);
        LOGGER.debug("search({}, {})", searchParams, pageId);
        return userService.searchPagination(searchParams, pageId);
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/current")
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public String getCurrentUserEmail() {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOGGER.info("GET " + BASE_PATH + "/current");
        LOGGER.debug("getCurrentUserEmail()");
        return user;
    }


    @Secured("ROLE_ADMIN")
    @Transactional
    @PutMapping()
    @Operation(summary = "SUMMARY HERE", security = @SecurityRequirement(name = "apiKey"))
    public void changeLockedStatus(@RequestBody Emails emails) throws NotFoundException, ValidationException {
        LOGGER.info("PUT " + BASE_PATH);
        LOGGER.debug("changeLockedStatus({})", emails);
        if (emails.getEmail().equals(emails.getCurrentEmail())) {
            throw new ValidationException("You can not lock yourself out", new ArrayList<>());
        }
        try {
            userService.changedLockedStatus(emails.getEmail());
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }
}
