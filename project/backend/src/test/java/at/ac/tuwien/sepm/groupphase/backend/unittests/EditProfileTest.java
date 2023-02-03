package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EditProfileTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() throws Exception {
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    public void editPasswordWithValidInput() throws Exception{
        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(EMAIL)
            .withFirstName(FIRST_NAME)
            .withLastName(LAST_NAME)
            .withTimesWrongPwEntered(0)
            .withPassword(passwordEncoder.encode(PASSWORD))
            .build();
        user.setAdmin(false);
        user.setLockedOut(false);
        userRepository.save(user);
        PasswordDto pass = new PasswordDto(PASSWORD, "newPassword", "newPassword");
        userService.editPassword(pass, userRepository.findApplicationUserByEmail(EMAIL).getId());
        UserLoginDto login = new UserLoginDto(EMAIL, "newPassword");
        try {
            userService.login(login);
            Assertions.assertTrue(true);
        } catch (Exception e){
            throw new TestInstantiationException("Test failed");
        }
    }

    @Transactional
    @Test
    public void editPasswordWithInvalidInputThrowsValidationException() throws Exception{
        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(EMAIL)
            .withFirstName(FIRST_NAME)
            .withLastName(LAST_NAME)
            .withTimesWrongPwEntered(0)
            .withPassword(passwordEncoder.encode(PASSWORD))
            .build();
        user.setAdmin(false);
        user.setLockedOut(false);
        userRepository.save(user);
        PasswordDto pass = new PasswordDto("p2158852ß96", "new1Password", "new1Password");
        assertThrows(ValidationException.class, () ->
            userService.editPassword(pass, userRepository.findApplicationUserByEmail(EMAIL).getId())
        );
    }

    @Transactional
    @Test
    public void editProfileWithInvalidInputThrowsValidationException() throws Exception{
        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(EMAIL)
            .withFirstName(FIRST_NAME)
            .withLastName(LAST_NAME)
            .withTimesWrongPwEntered(0)
            .withPassword(passwordEncoder.encode(PASSWORD))
            .build();
        user.setAdmin(false);
        user.setLockedOut(false);
        userRepository.save(user);
        RegistrationDto edit = new RegistrationDto("", "", "", "", "");
        assertThrows(ValidationException.class, () ->
            userService.edit(edit, userRepository.findApplicationUserByEmail(EMAIL).getId())
        );
    }

    @Transactional
    @Test
    public void editProfileWithValidInput() throws Exception{
        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(EMAIL)
            .withFirstName(FIRST_NAME)
            .withLastName(LAST_NAME)
            .withTimesWrongPwEntered(0)
            .withPassword(passwordEncoder.encode(PASSWORD))
            .build();
        user.setAdmin(false);
        user.setLockedOut(false);
        userRepository.save(user);
        RegistrationDto edit = new RegistrationDto("edit_first", "edit_last", "edit@email.com", PASSWORD, PASSWORD);
        userService.edit(edit, userRepository.findApplicationUserByEmail(EMAIL).getId());
    }



}

