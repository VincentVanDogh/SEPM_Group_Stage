package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Emails;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Stream;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exist
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return an application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto) throws BadCredentialsException;

    /**
     * Register a new user.
     *
     * @param applicationUser register credentials
     * @return the JWT, if successful
     * @throws ValidationException if credentials are bad
     */
    String register(ApplicationUser applicationUser) throws ValidationException, ConflictException;

    /**
     * Get all users.
     *
     * @return a stream of all users
     */
    Stream<UserDto> getAll();

    /**
     * Get one page of users (contains 20 users).
     *
     * @param page the page that is returned.
     * @return Dto containing total number of pages and the requested page of users.
     */
    UserPageDto getAllPagination(int page);

    /**
     * Toggle the locked status of a user. Locks user if it's unlocked and vice versa.
     *
     * @param email email of the user who's locked status is to be changed
     *
     */
    void changedLockedStatus(String email) throws NotFoundException, ValidationException;

    /**
     * Get all users matching the search criteria.
     *
     * @param searchParams The given search critia
     * @return a stream of all users matching the search criteria
     */
    Stream<UserDto> search(UserDto searchParams);

    /**
     * Get a page of users matching the search criteria.
     *
     * @param searchParams The given search criteria
     * @param page the page that is returned
     * @return a Dto of a user page matching the search criteria
     */
    UserPageDto searchPagination(UserDto searchParams, int page);


    /**
     * Edit an existing user.
     *
     * @param updatedUser user information
     * @return the JWT, if successful
     * @throws ValidationException if information is bad
     */
    String edit(RegistrationDto updatedUser, Long id) throws ValidationException;


    /**
     * Delete an existing user.
     *
     * @param userId  id of account that should be deleted
     * @throws NotFoundException if user with given id doesn't exist
     */
    void delete(Long userId) throws NotFoundException;

    /**
     * Get a user with a certain id.
     *
     * @param id  id of account that is looked for
     * @throws NotFoundException if user with given id doesn't exist
     */
    ApplicationUser getById(Long id) throws NotFoundException;

    /**
     * Edit the password of an existing user.
     *
     * @param passwordDto new password information
     * @return the JWT, if successful
     * @throws ValidationException if information is bad
     */
    String editPassword(PasswordDto passwordDto, Long userId) throws ValidationException;

    /**
     * Initiates resetting the password of a user by creating a token and sending it to the user by email.
     *
     * @param value email of the user
     * @return token if successful
     */
    String initiateResetPassword(Emails value);

    /**
     * Sets the password of a user to a new value if the reset token is in the database.
     *
     * @param newPassword the new password, confirmation and reset token
     * @return success message if successful
     */
    String resetPassword(NewPasswordDto newPassword) throws ValidationException;

    /**
     * Get bonus point balance of logged-in user.
     *
     * @return bonus point balance
     */
    Long getBonusPoints();

    /**
     * Get bonus point balance within cart of logged-in user.
     *
     * @return bonus point balance within cart
     */
    Long getCartBonusPoints();

    /**
     * Returns all users linked to the provided ticket ids.
     *
     * @param ticketIds the ticket ids
     * @return list of users
     * */
    List<ApplicationUser> getAllUsersForTicketIds(List<Long> ticketIds);
}
