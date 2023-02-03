package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPageDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketAcquisitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserForAdminEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketAcquisitionRepository ticketAcquisitionRepository;

    @Transactional
    @BeforeEach
    public void beforeEach() {
        ticketAcquisitionRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @Transactional
    public void createWithNotMatchingPasswordReturnsIsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
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

    @Test
    @Transactional
    public void createReturnsCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
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
    public void changeLockedStatusWithValidUserReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
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
        mockMvc.perform(MockMvcRequestBuilders.put(USER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "currentEmail":"admin@ticketline.com"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void changeLockedStatusWithNonexistentUserReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(USER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"NonExistenUser@test.com",
                                   "currentEmail":"admin@ticketline.com"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void changeLockedStatusOnYourselfReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(USER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"admin@ticketline.com",
                                   "currentEmail":"admin@ticketline.com"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserPageDto userPageDto = (objectMapper.readValue(response.getContentAsString(),
            UserPageDto.class));

        assertEquals(0, userPageDto.getUsers().size());
    }

    @Test
    @Transactional
    public void givenOne_whenFindAll_thenListSizeOne() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
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
        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserPageDto userPageDto = (objectMapper.readValue(response.getContentAsString(),
            UserPageDto.class));

        assertEquals(1, userPageDto.getUsers().size());
    }

    @Test
    @Transactional
    public void givenOne_whenSearchNotMatching_thenListEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
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
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(USER_BASE_URI + "/search/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
                .param("email", "test@example.com")
                .param("firstName", "")
                .param("lastName", "")
                .param("lockedOut", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserPageDto userPageDto = (objectMapper.readValue(response.getContentAsString(),
            UserPageDto.class));

        assertEquals(0, userPageDto.getUsers().size());
    }

    @Test
    @Transactional
    public void givenOne_whenSearchMatching_thenListSizeOne() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
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
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(USER_BASE_URI + "/search/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TestData.ADMIN_USER, TestData.ADMIN_ROLES))
                .param("email", "test@test.com")
                .param("firstName", "")
                .param("lastName", "")
                .param("lockedOut", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserPageDto userPageDto = (objectMapper.readValue(response.getContentAsString(),
            UserPageDto.class));

        assertEquals(1, userPageDto.getUsers().size());
    }

}
