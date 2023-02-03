package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoginEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;



    @BeforeEach
    @Transactional
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void loginReturnsOk() throws Exception {
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
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"password"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void loginWithInvalidInputReturnsForbidden() throws Exception {
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
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"oassword"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void loginToLockedAccountReturnsForbidden() throws Exception {
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
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(LOGIN_BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"oassword"
                                }
                                """)
                    .accept(MediaType.APPLICATION_JSON));
        }
        mockMvc.perform(post(LOGIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"password"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
            .andReturn().getResponse();
    }
}
