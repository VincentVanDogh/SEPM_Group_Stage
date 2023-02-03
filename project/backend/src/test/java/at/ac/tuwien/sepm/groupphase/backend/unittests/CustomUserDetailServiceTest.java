package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomUserDetailServiceTest implements TestData {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @BeforeEach
    @Transactional
    public void beforeEach() throws Exception {
        this.userRepository.deleteAll();
        ApplicationUser user = null;
        user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("test@test.com")
            .withFirstName("first_name")
            .withLastName("last_name")
            .withTimesWrongPwEntered(0)
            .withPassword("$2a$10$9.8gjySdhCTy1yfkhIAHvuWZzwYXIY4lmGMKo1RhjCOVlz1738GFW") //encoded "password"
            .build();
        user.setAdmin(false);
        user.setLockedOut(false);
        this.userRepository.save(user);
        ApplicationUser user2 = null;
        user2 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("test2@test.com")
            .withFirstName("first_name2")
            .withLastName("last_name2")
            .withTimesWrongPwEntered(2)
            .withPassword("$2a$10$9.8gjySdhCTy1yfkhIAHvuWZzwYXIY4lmGMKo1RhjCOVlz1738GFW") //encoded "password"
            .build();
        user2.setAdmin(false);
        user2.setLockedOut(false);
        this.userRepository.save(user2);
    }

    @Test
    @Transactional
    public void getAllReturnsAllStoredUsers() {
        List<UserDto> userDtos = userService.getAll()
            .toList();
        assertThat(userDtos.size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(userDtos.get(0).getEmail()).isEqualTo("test@test.com");
        AssertionsForClassTypes.assertThat(userDtos.get(0).getFirstName()).isEqualTo("first_name");
        AssertionsForClassTypes.assertThat(userDtos.get(0).getLastName()).isEqualTo("last_name");
        AssertionsForClassTypes.assertThat(userDtos.get(1).getEmail()).isEqualTo("test2@test.com");
        AssertionsForClassTypes.assertThat(userDtos.get(1).getFirstName()).isEqualTo("first_name2");
        AssertionsForClassTypes.assertThat(userDtos.get(1).getLastName()).isEqualTo("last_name2");
    }

    @Test
    @Transactional
    public void searchUserGetsMatchingUser() {
        UserDto params = new UserDto("2", "2", "2", Boolean.FALSE);
        List<UserDto> userDtos = userService.search(params)
            .toList();
        assertThat(userDtos.size()).isEqualTo(1);
        assertThat(userDtos.get(0).getEmail().equals("test2@test.com"));
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        userService.delete(userRepository.findApplicationUserByEmail("test@test.com").getId());
    }

    @Test
    @Transactional
    public void deleteNonexistingUserThrowsNotFoundException() throws Exception{
        assertThrows(NotFoundException.class, () ->
        userService.delete(999L)
        );
    }
}
