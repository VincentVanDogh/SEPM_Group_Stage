package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanTemplateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StagePlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class StagePlanMappingTest implements TestData {

    private final Sector standingSector = Sector.SectorBuilder.aSector()
        .withId(TEST_STANDING_SECTOR_ID)
        .withNumberOfSeats(TEST_STANDING_SECTOR_SEAT_NR)
        .withStanding(true)
        .withSectorMaps(new ArrayList<>())
        .build();

    private final Sector seatedSector = Sector.SectorBuilder.aSector()
        .withId(TEST_SEATED_SECTOR_ID)
        .withNumberOfSeats(TEST_SEATED_SECTOR_ROWS * TEST_SEATED_SECTOR_COLUMNS)
        .withNumberRows(TEST_SEATED_SECTOR_ROWS)
        .withNumberColumns(TEST_SEATED_SECTOR_ROWS)
        .withStanding(false)
        .withSectorMaps(new ArrayList<>())
        .build();

    private final StageTemplate stageTemplate1 = StageTemplate.StageTemplateBuilder.aStage()
        .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
        .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
        .withSectorMaps(new ArrayList<>())
        .build();

    private final StageTemplate stageTemplate2 = StageTemplate.StageTemplateBuilder.aStage()
        .withId(SECOND_TEST_STAGE_TEMPLATE_ID)
        .withName(SECOND_TEST_STAGE_TEMPLATE_NAME)
        .withSectorMaps(new ArrayList<>())
        .build();

    private final Stage stage1 = Stage.StageBuilder.aStage()
        .withId(FIRST_TEST_STAGE_ID)
        .withName(FIRST_TEST_STAGE_NAME)
        .withStageTemplate(stageTemplate1)
        .build();

    private final SectorMap map1 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(seatedSector)
        .withFirstSeatNr(1)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(2)
        .withStageTemplate(stageTemplate1)
        .withId(1L)
        .build();

    private final SectorMap map2 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(standingSector)
        .withFirstSeatNr(-1)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(1)
        .withStageTemplate(stageTemplate1)
        .withId(2L)
        .build();

    private final SectorMap map3 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(standingSector)
        .withFirstSeatNr(-1)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(1)
        .withStageTemplate(stageTemplate2)
        .withId(3L)
        .build();

    private final SectorMap map4 = SectorMap.SectorMapBuilder.aSectorMap()
        .withSector(standingSector)
        .withFirstSeatNr(-2)
        .withOrientation(Orientation.NORTH)
        .withSectorX(0)
        .withSectorY(2)
        .withStageTemplate(stageTemplate2)
        .withId(4L)
        .build();

    @Autowired
    private StagePlanMapper stagePlanMapper;

    @Test
    @Transactional
    public void givenNothing_whenMapStagePlanDtoToEntity_thenEntityHasAllProperties() {
        stageTemplate1.setSectorMaps(List.of(map1, map2));
        StagePlanDto stagePlanDto = stagePlanMapper.toStagePlanDto(stage1);
        assertAll(
            () -> assertEquals(FIRST_TEST_STAGE_ID, stagePlanDto.getStageId()),
            () -> assertEquals(FIRST_TEST_STAGE_NAME, stagePlanDto.getName()),
            () -> assertEquals(FIRST_TEST_STAGE_TEMPLATE_TOTAL_SEATS_NR, stagePlanDto.getTotalSeatsNr()),
            () -> assertEquals(-1, (stagePlanDto.getSectorArray()[31]).getFirstSeatNr()),
            () -> assertEquals(1, (stagePlanDto.getSectorArray()[38]).getFirstSeatNr())
        );
    }

    @Test
    public void givenNothing_whenMapStagePlanTemplateDtoToEntity_thenEntityHasAllProperties() {
        stageTemplate2.setSectorMaps(List.of(map3, map4));
        StagePlanTemplateDto stagePlanTemplateDto = stagePlanMapper.toStagePlanTemplateDto(stageTemplate2);
        assertAll(
            () -> assertEquals(SECOND_TEST_STAGE_TEMPLATE_ID, stagePlanTemplateDto.stageId()),
            () -> assertEquals(SECOND_TEST_STAGE_TEMPLATE_NAME, stagePlanTemplateDto.name()),
            () -> assertEquals(SECOND_TEST_STAGE_TEMPLATE_TOTAL_SEATS_NR, stagePlanTemplateDto.totalSeatsNr()),
            () -> assertEquals(-1, (stagePlanTemplateDto.sectorArray()[31]).getFirstSeatNr()),
            () -> assertEquals(-2, (stagePlanTemplateDto.sectorArray()[38]).getFirstSeatNr())
        );
    }


}
