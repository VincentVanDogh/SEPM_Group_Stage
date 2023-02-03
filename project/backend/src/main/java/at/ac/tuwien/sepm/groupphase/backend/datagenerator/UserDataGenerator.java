package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* this seed works for this configuration of 1000 users with no doubles, others might not */
    private final Faker faker = new Faker(new Random(1));

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    void genUsers() {
        if (userRepository.findAll().size() > 0) {
            LOGGER.debug("Users already generated");
            return;
        }
        LOGGER.info("Generating Users");
        genAdmin();
        genRegularUser();
        gen1000RegularUsers();
    }

    private void genAdmin() {
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("admin@email.com")
            .withPassword(passwordEncoder.encode("password"))
            .withFirstName("Armin")
            .withLastName("McAdmin")
            .withTimesWrongPwEntered(0)
            .isLockedOut(false)
            .isAdmin(true)
            .build());
    }

    private void gen1000RegularUsers() {
        List<ApplicationUser> users = new ArrayList<>();
        /* same password for everyone, otherwise the generation takes too long */
        String password = passwordEncoder.encode("password");

        for (int i = 0; i < 1000; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            users.add(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
                .withEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com")
                .withPassword(password)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimesWrongPwEntered(0)
                .isLockedOut(false)
                .isAdmin(false)
                .build());
        }

        userRepository.saveAll(users);
    }

    private void genRegularUser() {
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("test@testy.com")
            .withPassword(passwordEncoder.encode("12345678"))
            .withFirstName("Test")
            .withLastName("McTesty")
            .withTimesWrongPwEntered(0)
            .isLockedOut(false)
            .isAdmin(false)
            .build());

        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("max@mustermann.com")
            .withPassword(passwordEncoder.encode("123456789"))
            .withFirstName("Max")
            .withLastName("Mustermann")
            .withTimesWrongPwEntered(0)
            .isLockedOut(false)
            .isAdmin(false)
            .build());

        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("jane@doe.com")
            .withPassword(passwordEncoder.encode("12345678"))
            .withFirstName("Jane")
            .withLastName("Done")
            .withTimesWrongPwEntered(0)
            .isLockedOut(false)
            .isAdmin(false)
            .build());
    }
}
