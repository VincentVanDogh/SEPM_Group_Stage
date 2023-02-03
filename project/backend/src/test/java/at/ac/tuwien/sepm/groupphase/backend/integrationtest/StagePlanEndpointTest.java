package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StagePlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StagePlanEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private SectorMapRepository sectorMapRepository;

    @Autowired
    private StageTemplateRepository stageTemplateRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StagePlanMapper stagePlanMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Sector standingSector = Sector.SectorBuilder.aSector()
        .withId(TEST_STANDING_SECTOR_ID)
        .withNumberOfSeats(TEST_STANDING_SECTOR_SEAT_NR)
        .withStanding(true)
        .withSectorMaps(new ArrayList<>())
        .build();

    private Sector seatedSector = Sector.SectorBuilder.aSector()
        .withId(TEST_SEATED_SECTOR_ID)
        .withNumberOfSeats(TEST_SEATED_SECTOR_ROWS * TEST_SEATED_SECTOR_COLUMNS)
        .withNumberRows(TEST_SEATED_SECTOR_ROWS)
        .withNumberColumns(TEST_SEATED_SECTOR_ROWS)
        .withStanding(false)
        .withSectorMaps(new ArrayList<>())
        .build();

    private StageTemplate stageTemplate1 = StageTemplate.StageTemplateBuilder.aStage()
        .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
        .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
        .withSectorMaps(new ArrayList<>())
        .build();

    private StageTemplate stageTemplate2 = StageTemplate.StageTemplateBuilder.aStage()
        .withId(SECOND_TEST_STAGE_TEMPLATE_ID)
        .withName(SECOND_TEST_STAGE_TEMPLATE_NAME)
        .withSectorMaps(new ArrayList<>())
        .build();

    private Stage stage1 = Stage.StageBuilder.aStage()
        .withId(FIRST_TEST_STAGE_ID)
        .withName(FIRST_TEST_STAGE_NAME)
        .withStageTemplate(stageTemplate1)
        .build();

    private SectorMap map1 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(seatedSector)
        .withFirstSeatNr(1)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(2)
        .withStageTemplate(stageTemplate1)
        .withId(1L)
        .build();

    private SectorMap map2 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(standingSector)
        .withFirstSeatNr(-1)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(1)
        .withStageTemplate(stageTemplate1)
        .withId(2L)
        .build();

    private SectorMap map3 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(standingSector)
        .withFirstSeatNr(-1)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(1)
        .withStageTemplate(stageTemplate2)
        .withId(3L)
        .build();

    private SectorMap map4 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(standingSector)
        .withFirstSeatNr(-2)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(2)
        .withStageTemplate(stageTemplate2)
        .withId(4L)
        .build();

    @BeforeEach
    @Transactional
    public void beforeEach() {
        stageRepository.deleteAll();
        stageTemplateRepository.deleteAll();
        sectorRepository.deleteAll();
        sectorMapRepository.deleteAll();
        standingSector = Sector.SectorBuilder.aSector()
            .withId(TEST_STANDING_SECTOR_ID)
            .withNumberOfSeats(TEST_STANDING_SECTOR_SEAT_NR)
            .withStanding(true)
            .withSectorMaps(new ArrayList<>())
            .build();

        seatedSector = Sector.SectorBuilder.aSector()
            .withId(TEST_SEATED_SECTOR_ID)
            .withNumberOfSeats(TEST_SEATED_SECTOR_ROWS * TEST_SEATED_SECTOR_COLUMNS)
            .withNumberRows(TEST_SEATED_SECTOR_ROWS)
            .withNumberColumns(TEST_SEATED_SECTOR_ROWS)
            .withStanding(false)
            .withSectorMaps(new ArrayList<>())
            .build();

        stageTemplate1 = StageTemplate.StageTemplateBuilder.aStage()
            .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
            .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
            .withSectorMaps(new ArrayList<>())
            .build();

        stageTemplate2 = StageTemplate.StageTemplateBuilder.aStage()
            .withId(SECOND_TEST_STAGE_TEMPLATE_ID)
            .withName(SECOND_TEST_STAGE_TEMPLATE_NAME)
            .withSectorMaps(new ArrayList<>())
            .build();

        stage1 = Stage.StageBuilder.aStage()
            .withId(FIRST_TEST_STAGE_ID)
            .withName(FIRST_TEST_STAGE_NAME)
            .withStageTemplate(stageTemplate1)
            .build();

        map1 = SectorMap.SectorMapBuilder.aSectorMap()
            .withSector(seatedSector)
            .withFirstSeatNr(1)
            .withOrientation(Orientation.NORTH)
            .withSectorX(0)
            .withSectorY(2)
            .withStageTemplate(stageTemplate1)
            .withId(1L)
            .build();

        map2 = SectorMap.SectorMapBuilder.aSectorMap()
            .withSector(standingSector)
            .withFirstSeatNr(-1)
            .withOrientation(Orientation.NORTH)
            .withSectorX(0)
            .withSectorY(1)
            .withStageTemplate(stageTemplate1)
            .withId(2L)
            .build();

        map3 = SectorMap.SectorMapBuilder.aSectorMap()
            .withSector(standingSector)
            .withFirstSeatNr(-1)
            .withOrientation(Orientation.NORTH)
            .withSectorX(0)
            .withSectorY(1)
            .withStageTemplate(stageTemplate2)
            .withId(3L)
            .build();

        map4 = SectorMap.SectorMapBuilder.aSectorMap()
            .withSector(standingSector)
            .withFirstSeatNr(-2)
            .withOrientation(Orientation.NORTH)
            .withSectorX(0)
            .withSectorY(2)
            .withStageTemplate(stageTemplate2)
            .withId(4L)
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(STAGE_PLAN_GENERIC_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<StagePlanDto> stagePlanDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            StagePlanDto[].class));

        assertEquals(0, stagePlanDtos.size());
    }

    @Test
    public void givenOneStagePlan_whenFindByNonExistingId_then404() throws Exception {
        stageTemplateRepository.save(stageTemplate1);
        stageRepository.save(stage1);

        MvcResult mvcResult = this.mockMvc.perform(get(STAGE_PLAN_GENERIC_URI + "/{id}", -1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
    //
    //    @Test
    //    public void givenOneMessage_whenFindAll_thenListWithSizeOneAndMessageWithAllPropertiesExceptSummary()
    //        throws Exception {
    //        messageRepository.save(message);
    //
    //        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI)
    //            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
    //            .andDo(print())
    //            .andReturn();
    //        MockHttpServletResponse response = mvcResult.getResponse();
    //
    //        assertEquals(HttpStatus.OK.value(), response.getStatus());
    //        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    //
    //        List<SimpleMessageDto> simpleMessageDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
    //            SimpleMessageDto[].class));
    //
    //        assertEquals(1, simpleMessageDtos.size());
    //        SimpleMessageDto simpleMessageDto = simpleMessageDtos.get(0);
    //        assertAll(
    //            () -> assertEquals(message.getId(), simpleMessageDto.getId()),
    //            () -> assertEquals(TEST_NEWS_TITLE, simpleMessageDto.getTitle()),
    //            () -> assertEquals(TEST_NEWS_SUMMARY, simpleMessageDto.getSummary()),
    //            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, simpleMessageDto.getPublishedAt())
    //        );
    //    }
    //
    //    @Test
    //    public void givenOneMessage_whenFindById_thenMessageWithAllProperties() throws Exception {
    //        messageRepository.save(message);
    //
    //        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", message.getId())
    //            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
    //            .andDo(print())
    //            .andReturn();
    //        MockHttpServletResponse response = mvcResult.getResponse();
    //
    //        assertAll(
    //            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
    //            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
    //        );
    //
    //        DetailedMessageDto detailedMessageDto = objectMapper.readValue(response.getContentAsString(),
    //            DetailedMessageDto.class);
    //
    //        assertEquals(message, messageMapper.detailedMessageDtoToMessage(detailedMessageDto));
    //    }
    //

    //
    //    @Test
    //    public void givenNothing_whenPost_thenMessageWithAllSetPropertiesPlusIdAndPublishedDate() throws Exception {
    //        message.setPublishedAt(null);
    //        MessageInquiryDto messageInquiryDto = messageMapper.messageToMessageInquiryDto(message);
    //        String body = objectMapper.writeValueAsString(messageInquiryDto);
    //
    //        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .content(body)
    //            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
    //            .andDo(print())
    //            .andReturn();
    //        MockHttpServletResponse response = mvcResult.getResponse();
    //
    //        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    //        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    //
    //        DetailedMessageDto messageResponse = objectMapper.readValue(response.getContentAsString(),
    //            DetailedMessageDto.class);
    //
    //        assertNotNull(messageResponse.getId());
    //        assertNotNull(messageResponse.getPublishedAt());
    //        assertTrue(isNow(messageResponse.getPublishedAt()));
    //        //Set generated properties to null to make the response comparable with the original input
    //        messageResponse.setId(null);
    //        messageResponse.setPublishedAt(null);
    //        assertEquals(message, messageMapper.detailedMessageDtoToMessage(messageResponse));
    //    }
    //
    //    @Test
    //    public void givenNothing_whenPostInvalid_then400() throws Exception {
    //        message.setTitle(null);
    //        message.setSummary(null);
    //        message.setText(null);
    //        MessageInquiryDto messageInquiryDto = messageMapper.messageToMessageInquiryDto(message);
    //        String body = objectMapper.writeValueAsString(messageInquiryDto);
    //
    //        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .content(body)
    //            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
    //            .andDo(print())
    //            .andReturn();
    //        MockHttpServletResponse response = mvcResult.getResponse();
    //
    //        assertAll(
    //            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
    //            () -> {
    //                //Reads the errors from the body
    //                String content = response.getContentAsString();
    //                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
    //                String[] errors = content.split(",");
    //                assertEquals(3, errors.length);
    //            }
    //        );
    //    }
    //
    //    private boolean isNow(LocalDateTime date) {
    //        LocalDateTime today = LocalDateTime.now();
    //        return date.getYear() == today.getYear() && date.getDayOfYear() == today.getDayOfYear() &&
    //            date.getHour() == today.getHour();
    //    }

}
