package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
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

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ArtistEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createWithValidInputReturnsOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(ARTIST_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "firstName": "Axel",
                       "lastName": "Rose",
                       "bandName": "Guns'n Roses",
                       "stageName": "Guns'n Roses"
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(ARTIST_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "firstName": "",
                       "lastName": "",
                       "bandName": "Wiener Philharmoniker",
                       "stageName": "Wiener Philharmoniker"
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void createWithInvalidInputReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ARTIST_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "firstName": "Axel",
                       "lastName": "Rose",
                       "bandName": "Guns'n Roses",
                       "stageName": ""
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void createArtistAsUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ARTIST_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "firstName": "Axel",
                       "lastName": "Rose",
                       "bandName": "Guns'n Roses",
                       "stageName": "Guns'n Roses"
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
            .andReturn().getResponse();
    }
}
