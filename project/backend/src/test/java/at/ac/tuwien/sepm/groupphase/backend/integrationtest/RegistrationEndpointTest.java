package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RegistrationEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;



    @BeforeEach
    @Transactional
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void registerReturnsCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"password",
                                   "confirmation":"password",
                                   "firstName": "first_name",
                                   "lastName":"last_name"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn().getResponse();
        }

    @Test
    @Transactional
    public void registerWithMismatchingPasswordsReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test @ test.com",
                                   "password":"12341234",
                                   "confirmation":"password",
                                   "firstName": "f",
                                   "lastName":"l"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
            .andReturn().getResponse();
    }
}
