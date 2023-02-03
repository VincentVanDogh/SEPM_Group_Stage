package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PasswordResetEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;



    @BeforeEach
    @Transactional
    public void beforeEach() {
        userRepository.deleteAll();
        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail("test@test.com")
            .withFirstName("first_name")
            .withLastName("last_name")
            .withTimesWrongPwEntered(0)
            .withPassword("$2a$10$9.8gjySdhCTy1yfkhIAHvuWZzwYXIY4lmGMKo1RhjCOVlz1738GFW") //encoded "password"
            .build();
        user.setAdmin(false);
        user.setLockedOut(false);
        userRepository.save(user);
    }

    @Test
    @Transactional
    public void resettingPwForExistingAccountReturnsOK() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(RESET_PW_BASE_URI + "/initiate")  //requesting mail with reset token
            .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com"
                                }
                                """)
            .accept(MediaType.APPLICATION_JSON)).andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String token = response.getContentAsString();

        NewPasswordDto newPasswordDto = new NewPasswordDto("12341234", "12341234", token);
        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.post(RESET_PW_BASE_URI) //setting new password using token
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newPasswordDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertEquals(HttpStatus.OK.value(), response2.getStatus());

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI) //Login with new password
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"test@test.com",
                                   "password":"12341234"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void resettingPwForNonexistingAccountReturns404() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(RESET_PW_BASE_URI + "/initiate")  //requesting mail with reset token
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                   "email":"nonExistent@test.com"
                                }
                                """)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @Transactional
    public void resettingPwWithNonExistingTokenReturns404() throws Exception {
        NewPasswordDto newPasswordDto = new NewPasswordDto("12341234", "12341234", "nonExistingToken");
        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.post(RESET_PW_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newPasswordDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response2.getStatus());
    }

    @Test
    @Transactional
    public void resettingPwWithNotMatchingPwConfirmationReturns403() throws Exception {
        NewPasswordDto newPasswordDto = new NewPasswordDto("password1", "password2", "");
        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.post(RESET_PW_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newPasswordDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response2.getStatus());
    }

}
