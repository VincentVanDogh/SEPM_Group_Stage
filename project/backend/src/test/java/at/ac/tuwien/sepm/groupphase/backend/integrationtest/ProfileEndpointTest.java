package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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
public class ProfileEndpointTest implements TestData {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void beforeEach() throws Exception {
        userRepository.deleteAll();
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
            .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    public void editPasswordWithInvalidInputReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PROFILE_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "oldPassword":"password",
                                   "newPassword":"222222222",
                                   "confirmation":"333333333"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    }

}
