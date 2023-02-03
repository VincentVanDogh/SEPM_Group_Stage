package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ActEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

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
    private final Address address = new Address(STREET, CITY, COUNTRY, POSTAL_CODE);
    private final Event event = new Event(TEST_EVENT_NAME, TEST_EVENT_DESCRIPTION, TEST_EVENT_TYPE, TEST_EVENT_DURATION);

    @BeforeEach
    @Transactional
    public void beforeEach() {

        eventRepository.deleteAll();
        addressRepository.deleteAll();
        locationRepository.deleteAll();
        artistRepository.deleteAll();
        stageTemplateRepository.deleteAll();
        stageRepository.deleteAll();


        Artist savedArtist1 = artistRepository.save(artist1);
        List<Artist> artistList = new ArrayList<>();
        artistList.add(savedArtist1);

        Address savedAddress = addressRepository.save(address);
        Location location1 = new Location(VENUE_NAME, savedAddress);

        Location savedLocation = locationRepository.save(location1);

        StageTemplate stageTemplateNew = StageTemplate.StageTemplateBuilder.aStage()
            .withId(1L)
            .withName(STAGE_NAME)
            .build();

        StageTemplate savedStageTemplate = stageTemplateRepository.save(stageTemplateNew);

        Optional<StageTemplate> stageTemplate = stageTemplateRepository.findById(savedStageTemplate.getId());
        if (stageTemplate.isPresent()) {
            Stage stage1 = Stage.StageBuilder.aStage()
                .withName(STAGE_NAME)
                .withStageTemplate(stageTemplate.get())
                .withLocation(location1)
                .build();
            Stage stage2 = Stage.StageBuilder.aStage()
                .withName(STAGE_NAME)
                .withStageTemplate(stageTemplate.get())
                .withLocation(location1)
                .build();

            Stage savedStage1 = stageRepository.save(stage1);
            stageRepository.save(stage2);

            event.setLocation(savedLocation);
            event.setFeaturedArtists(artistList);
            List<Act> actList = new ArrayList<>();
            Act.ActBuilder actBuilder = Act.ActBuilder.anAct()
                .withEvent(event)
                .withStage(stageRepository.getReferenceById(savedStage1.getId()))
                .withStart(LocalDateTime.now());
            actList.add(actBuilder.build());
            event.setActs(actList);

            eventRepository.save(event);
        }
    }

    @Test
    @Transactional
    public void whenSearchingWithNoQueryParams_thenGet200() throws Exception {

        mockMvc.perform(get( ACT_BASE_URI + "/search/1"))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void whenSearchingWithInvalidQueryParams_thenGet422() throws Exception {


        mockMvc.perform(get( ACT_BASE_URI + "/search/1")
                .param("dateFrom", "1970-01-01")
                .param("dateTo", "3000-01-01")
                .param("minPrice", "1000")
                .param("maxPrice", "0"))
            .andExpect(status().isUnprocessableEntity());
    }
}
