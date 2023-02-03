package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Emails;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ResetPasswordToken;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchPurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ResetPasswordTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;


import at.ac.tuwien.sepm.groupphase.backend.service.validation.CredentialValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final MerchPurchaseRepository merchRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    private final UserMapper userMapper;
    private final CredentialValidator validator;
    private final MerchPurchaseServiceImpl merchPurchaseService;

    private final ResetPasswordService resetPasswordService;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final EntityManager entityManager;
    private final MessageRepository messageRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository,
                                   MerchPurchaseRepository merchRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer,
                                   CredentialValidator validator,
                                   UserMapper userMapper,
                                   MerchPurchaseServiceImpl merchPurchaseService,
                                   ResetPasswordService resetPasswordService,
                                   ResetPasswordTokenRepository resetPasswordTokenRepository,
                                   EntityManager entityManager, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.merchRepository = merchRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
        this.userMapper = userMapper;
        this.merchPurchaseService = merchPurchaseService;
        this.resetPasswordService = resetPasswordService;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.entityManager = entityManager;
        this.messageRepository = messageRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            Boolean isLocked = applicationUser.getLockedOut();
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            return new User(applicationUser.getEmail(), applicationUser.getPassword(), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, !isLocked, grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findApplicationUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address '%s'", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.debug("Login User {}", userLoginDto);
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
        ) {
            ApplicationUser applicationUser = userRepository.findApplicationUserByEmail(userLoginDto.getEmail());
            if (passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())) {
                applicationUser.setTimesWrongPwEntered(0);
                userRepository.save(applicationUser);
                List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
                return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
            } else {
                applicationUser.setTimesWrongPwEntered(applicationUser.getTimesWrongPwEntered() + 1);
                int remainingTries = 5 - applicationUser.getTimesWrongPwEntered();
                if (remainingTries <= 0) {
                    applicationUser.setLockedOut(Boolean.TRUE);
                    userRepository.save(applicationUser);
                    throw new BadCredentialsException("Password is incorrect. Your account has been locked for entering an incorrect password too many times");
                } else {
                    userRepository.save(applicationUser);
                    throw new BadCredentialsException("Password is incorrect. " + remainingTries + (remainingTries == 1 ? " try " : " tries ") + "remaining before account is locked");
                }
            }
        }
        throw new BadCredentialsException("Account is currently locked");
    }

    @Override
    public String register(ApplicationUser applicationUser) throws ValidationException, ConflictException {
        LOGGER.debug("Create new user {}", applicationUser);
        try {
            findApplicationUserByEmail(applicationUser.getEmail());
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("Email is already registered");
            throw new ConflictException("Email is already registered", conflictErrors);
        } catch (NotFoundException e) {
            validator.validateForRegistration(applicationUser);
            if (applicationUser.getAdmin() == null) {
                applicationUser.setAdmin(false);
            }
            applicationUser.setLockedOut(false);
            applicationUser.setTimesWrongPwEntered(0);
            userRepository.save(applicationUser);
            UserDetails userDetails = loadUserByUsername(applicationUser.getEmail());
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
    }

    @Override
    public String edit(RegistrationDto updatedUser, Long id) throws ValidationException {
        LOGGER.debug("Update user {}", id);
        validator.validateForRegistration(userMapper.registrationDtoToApplicationUser(updatedUser));
        ApplicationUser userToUpdate = userRepository.getOne(id);
        if (!(passwordEncoder.matches(updatedUser.getPassword(), userToUpdate.getPassword()))) {
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add("Current password is wrong");
            throw new ValidationException("Error", validationErrors);
        }
        userToUpdate.setFirstName(updatedUser.getFirstName());
        userToUpdate.setLastName(updatedUser.getLastName());
        userToUpdate.setEmail(updatedUser.getEmail());
        userRepository.save(userToUpdate);
        UserDetails userDetails = loadUserByUsername(userToUpdate.getEmail());
        List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);

    }

    @Override
    public String editPassword(PasswordDto updatedPassword, Long id) throws ValidationException {
        LOGGER.debug("Update user {}", id);
        ApplicationUser userToUpdate = userRepository.getReferenceById(id);
        String encodedPassword = passwordEncoder.encode(updatedPassword.getNewPassword());
        updatedPassword.setNewPassword(encodedPassword);
        if (!(passwordEncoder.matches(updatedPassword.getOldPassword(), userToUpdate.getPassword()))) {
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add("Current password is wrong");
            throw new ValidationException("Error", validationErrors);
        }
        userToUpdate.setPassword(updatedPassword.getNewPassword());
        userRepository.save(userToUpdate);
        UserDetails userDetails = loadUserByUsername(userToUpdate.getEmail());
        List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);

    }

    @Override
    @Transactional
    public void delete(Long userId) throws NotFoundException {
        LOGGER.debug("Delete user {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with given Id doesnt exist");
        } else {
            ApplicationUser user = userRepository.getReferenceById(userId);
            Set<at.ac.tuwien.sepm.groupphase.backend.entity.Message> messagesReadByUser = user.getReadNews();
            if (messagesReadByUser != null) {
                for (at.ac.tuwien.sepm.groupphase.backend.entity.Message message :
                    messagesReadByUser) {
                    message.removeUser(user);
                    messageRepository.save(message);
                }
            }
            userRepository.delete(user);
        }
    }

    @Override
    public ApplicationUser getById(Long userId) throws NotFoundException {
        LOGGER.debug("Get user {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with given Id doesnt exist");
        } else {
            return userRepository.getReferenceById(userId);
        }
    }

    @Override
    public Stream<UserDto> getAll() {
        LOGGER.debug("Getting all users");
        var users = userMapper.applicationUserToUserDto(userRepository.findAll());
        return users.stream();
    }

    @Override
    public UserPageDto getAllPagination(int page) {
        LOGGER.debug("Getting all users");

        return new UserPageDto(
            userRepository.findAll().size() % 20 == 0 ? userRepository.findAll().size() / 20 : userRepository.findAll().size() / 20 + 1,
            userMapper.applicationUserToUserDto(userRepository.findAll(PageRequest.of(page - 1, 20)).stream().toList())
        );
    }

    @Override
    public Stream<UserDto> search(UserDto searchParams) {
        LOGGER.debug("Searching for users");
        if (searchParams.getEmail() == null) {
            searchParams.setEmail("");
        }
        if (searchParams.getFirstName() == null) {
            searchParams.setFirstName("");
        }
        if (searchParams.getLastName() == null) {
            searchParams.setLastName("");
        }
        List<UserDto> users;
        if (searchParams.getLockedOut() == null) {
            users = userMapper.applicationUserToUserDto(
                userRepository.findBySearchWithoutLockedValue(
                    searchParams.getEmail().toUpperCase(),
                    searchParams.getFirstName().toUpperCase(),
                    searchParams.getLastName().toUpperCase()
                )
            );
        } else {
            users = userMapper.applicationUserToUserDto(
                userRepository.findBySearchWithLockedValue(
                    searchParams.getEmail().toUpperCase(),
                    searchParams.getFirstName().toUpperCase(),
                    searchParams.getLastName().toUpperCase(),
                    searchParams.getLockedOut()
                )
            );
        }
        return users.stream();
    }

    @Override
    public UserPageDto searchPagination(UserDto searchParams, int page) {

        int numberOfPages = 0;
        List<UserDto> users;

        LOGGER.debug("Searching for users");
        if (searchParams.getEmail() == null) {
            searchParams.setEmail("");
        }
        if (searchParams.getFirstName() == null) {
            searchParams.setFirstName("");
        }
        if (searchParams.getLastName() == null) {
            searchParams.setLastName("");
        }

        if (searchParams.getLockedOut() == null) {
            users = userMapper.applicationUserToUserDto(
                userRepository.findBySearchWithLockedValuePagination(
                    searchParams.getEmail().toUpperCase(),
                    searchParams.getFirstName().toUpperCase(),
                    searchParams.getLastName().toUpperCase(),
                    PageRequest.of(page - 1, 20)
                )
            );
            numberOfPages = (int) userRepository.findSizeBySearchWithoutLocked(
                    searchParams.getEmail().toUpperCase(),
                    searchParams.getFirstName().toUpperCase(),
                    searchParams.getLastName().toUpperCase()
                );
        } else {
            users = userMapper.applicationUserToUserDto(
                userRepository.findBySearchWithoutLockedValuePagination(
                    searchParams.getEmail().toUpperCase(),
                    searchParams.getFirstName().toUpperCase(),
                    searchParams.getLastName().toUpperCase(),
                    searchParams.getLockedOut(),
                    PageRequest.of(page - 1, 20)
                )
            );
            numberOfPages = (int) userRepository.findSizeBySearchWithLocked(
                searchParams.getEmail().toUpperCase(),
                searchParams.getFirstName().toUpperCase(),
                searchParams.getLastName().toUpperCase(),
                searchParams.getLockedOut()
            );
        }
        return new UserPageDto(
            numberOfPages % 20 == 0 ? numberOfPages / 20 : numberOfPages / 20 + 1,
            users
        );
    }


    @Override
    public void changedLockedStatus(String email) throws NotFoundException, ValidationException {
        LOGGER.debug("Changing locked status of user: {}", email);
        ApplicationUser userToToggleLocked = userRepository.findApplicationUserByEmail(email);
        if (userToToggleLocked == null) {
            throw new NotFoundException("User: " + email + "not found");
        }
        if (userToToggleLocked.getLockedOut()) {
            userToToggleLocked.setTimesWrongPwEntered(0);
        }
        userToToggleLocked.setLockedOut(!userToToggleLocked.getLockedOut());
        userRepository.save(userToToggleLocked);

    }

    @Override
    public Long getBonusPoints() {
        return merchPurchaseService.getBonusPoints();
    }

    @Override
    public Long getCartBonusPoints() {
        return merchPurchaseService.getBonusPointsInCart();
    }

    @Override
    public String initiateResetPassword(Emails userMail) {
        String email = userMail.getEmail();
        ApplicationUser user = userRepository.findApplicationUserByEmail(email);
        if (user == null) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, "No user with email: " + email);
        }
        String token = UUID.randomUUID().toString();
        ResetPasswordToken resetToken = new ResetPasswordToken(
            token,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15),
            user
        );
        this.resetPasswordService.saveResetPasswordToken(resetToken);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Set up authentication
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        "ticketlinepwreset@gmail.com", "zgijcksjnhxbbotd");
                }
            });
        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set sender
            message.setFrom(new InternetAddress("ticketlinepwreset@gmail.com"));

            // Set recipient
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email));

            // Set subject
            message.setSubject("Reset your password");

            // Set the content
            message.setText("To reset your password click the following link: "
                + "http://localhost:4200/#/set-new-password/" + token);

            // Send the email
            Transport.send(message);
            return token;

        } catch (MessagingException e) {
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Override
    public String resetPassword(NewPasswordDto newPassword) throws ValidationException {
        if (newPassword.getNewPassword() == null) {
            throw new BadCredentialsException("Passwords can not be null");
        }
        if (!(newPassword.getNewPassword().equals(newPassword.getConfirmation()))) {
            throw new BadCredentialsException("Passwords do not match");
        }
        String token = newPassword.getToken();
        Optional<ResetPasswordToken> optionalToken = this.resetPasswordTokenRepository.findByToken(token);
        if (optionalToken.isPresent()) {
            ResetPasswordToken resetToken = optionalToken.get();
            if (resetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
                List<String> validationErrors = new ArrayList<>();
                validationErrors.add("This link is not valid anymore. Please request a new one.");
                throw new ValidationException("Password reset failed", validationErrors);
            }
            String encodedPassword = passwordEncoder.encode(newPassword.getNewPassword());
            newPassword.setNewPassword(encodedPassword);
            ApplicationUser userToResetPw = resetToken.getUser();
            userToResetPw.setPassword(newPassword.getNewPassword());
            userRepository.save(userToResetPw);
        } else {
            throw new NotFoundException("Token is not in the database");
        }
        return "Successfully set new password";
    }


    public List<ApplicationUser> getAllUsersForTicketIds(List<Long> ticketIds) {
        return userRepository.getAllUsersForTicketIds(ticketIds);
    }
}
