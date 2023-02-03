package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
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

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventSearchEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ActRepository actRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private StageTemplateRepository stageTemplateRepository;

    @Autowired
    StageRepository stageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private final Artist artist1 = new Artist(FIRST_NAME, LAST_NAME, BAND_NAME, ARTIST_STAGE_NAME);
    private final Artist artist2 = new Artist(FIRST_NAME, LAST_NAME, BAND_NAME, ARTIST_STAGE_NAME);
    private final Address address = new Address(STREET, CITY, COUNTRY, POSTAL_CODE);

    private final Event event = new Event(TEST_EVENT_NAME, TEST_EVENT_DESCRIPTION, TEST_EVENT_TYPE, TEST_EVENT_DURATION);
    @BeforeEach
    @Transactional
    public void beforeEach() {
        actRepository.deleteAll();
        eventRepository.deleteAll();
        addressRepository.deleteAll();
        stageRepository.deleteAll();
        stageTemplateRepository.deleteAll();
        locationRepository.deleteAll();
        artistRepository.deleteAll();


        Artist savedArtist1 = artistRepository.save(artist1);
        Artist savedArtist2 = artistRepository.save(artist2);
        List<Artist> artistList = new ArrayList<>();
        artistList.add(savedArtist1);
        artistList.add(savedArtist2);

        Address savedAddress = addressRepository.save(address);
        Location location1 = new Location(VENUE_NAME, savedAddress);

        Location savedLocation = locationRepository.save(location1);

        StageTemplate stageTemplate = StageTemplate.StageTemplateBuilder.aStage()
            .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
            .withId(20L)
            .build();
        stageTemplateRepository.save(stageTemplate);

        Stage stage1 = Stage.StageBuilder.aStage()
            .withName(STAGE_NAME)
            .withStageTemplate(stageTemplate)
            .withLocation(location1).build();

        Stage savedStage1 = stageRepository.save(stage1);

        event.setName(TEST_EVENT_NAME);
        event.setLocation(savedLocation);
        event.setFeaturedArtists(artistList);
        List<Act> actList = new ArrayList<>();
        Act.ActBuilder actBuilder = Act.ActBuilder.anAct()
            .withEvent(event)
            .withStage(stageRepository.getReferenceById(savedStage1.getId()))
            .withStart(LocalDateTime.now().plusMinutes(10L))
            .withNrTicketsSold(0)
            .withNrTicketsReserved(0);
        actList.add(actBuilder.build());
        event.setActs(actList);

        eventRepository.save(event);
    }


    @Test
    @Transactional
    public void givenOne_whenLoadingAllEvents_thenOneEventWithAllProperties() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/search/all/1"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        EventPageDto eventPageDto = objectMapper.readValue(response.getContentAsString(), EventPageDto.class);

        assertEquals(eventMapper.eventToEventDetailsDto(event), eventPageDto.getEvents().get(0));
    }

    @Test
    @Transactional
    public void givenOne_whenSearchingAnEventWithMatchingQuery_thenGettingValidEventWithAllProperties() throws Exception {

        eventRepository.save(event);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/search/page/1")
                .param("search", "Test")
                .param("dateFrom", "1970-01-01"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        EventPageDto eventPageDto = objectMapper.readValue(response.getContentAsString(), EventPageDto.class);

        assertEquals(eventMapper.eventToEventDetailsDto(event), eventPageDto.getEvents().get(0));
    }

    @Test
    @Transactional
    public void givenOne_whenSearchingAnEventWithNotMatchingQuery_thenGettingEmptyList() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/search/page/1")
                .param("search", "invalid"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        EventPageDto eventPageDto = objectMapper.readValue(response.getContentAsString(), EventPageDto.class);

        assertEquals(0, eventPageDto.getEvents().size());
    }

    @Test
    @Transactional
    public void whenSearchingWithInvalidQueryParams_thenGet422() throws Exception {

        mockMvc.perform(get(EVENT_BASE_URI + "/search/page/1")
                .param("dateFrom", "2000-01-01")
                .param("dateTo", "1970-01-01"))
            .andExpect(status().isUnprocessableEntity());
    }
}

