package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
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
public class LocationEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StageTemplateRepository stageTemplateRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        locationRepository.deleteAll();
        stageTemplateRepository.deleteAll();

        StageTemplate stageTemplate2 = StageTemplate.StageTemplateBuilder.aStage()
            .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
            .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
            .build();
        stageTemplateRepository.save(stageTemplate2);

        StageTemplate stageTemplate3 = StageTemplate.StageTemplateBuilder.aStage()
            .withName(SECOND_TEST_STAGE_TEMPLATE_NAME)
            .withId(SECOND_TEST_STAGE_TEMPLATE_ID)
            .build();
        stageTemplateRepository.save(stageTemplate3);
    }

    @Test
    @Transactional
    public void createWithValidInputReturnsOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(LOCATION_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "venueName": "Arena Wien",
                       "street": "Baumgasse 80",
                       "city": "Wien",
                       "country": "Austria",
                       "postalCode": 1030,
                       "stages": [{
                            "name": "stage 1",
                            "stageTemplateId": "1"
                       }, {
                            "name": "stage 2",
                            "stageTemplateId": "2"
                       }, {
                            "name": "stage 3",
                            "stageTemplateId": "1"
                       }, {
                            "name": "stage 4",
                            "stageTemplateId": "1"
                       }]
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn().getResponse();
    }

    @Test
    @Transactional
    public void createWithInValidInputReturnsBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(LOCATION_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "venueName": "Arena Wien",
                       "street": "Baumgasse 80",
                       "city": "Wien",
                       "country": "Austria",
                       "postalCode": 1030,
                       "stages": [{
                            "name": "",
                            "stageTemplateId": "1"
                       }, {
                            "name": "stage 2",
                            "stageTemplateId": "2"
                       }, {
                            "name": "stage 3",
                            "stageTemplateId": "1"
                       }, {
                            "name": "stage 4",
                            "stageTemplateId": "1"
                       }]
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
            .andReturn().getResponse();

        mockMvc.perform(MockMvcRequestBuilders.post(LOCATION_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                       "venueName": "Arena Wien",
                       "street": "",
                       "city": "Wien",
                       "country": "Austria",
                       "postalCode": 1030,
                       "stages": [{
                            "name": "stage 1",
                            "stageTemplateId": "1"
                       }, {
                            "name": "stage 2",
                            "stageTemplateId": "2"
                       }, {
                            "name": "stage 3",
                            "stageTemplateId": "1"
                       }, {
                            "name": "stage 4",
                            "stageTemplateId": "1"
                       }]
                    }
                    """)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
            .andReturn().getResponse();
    }
}
