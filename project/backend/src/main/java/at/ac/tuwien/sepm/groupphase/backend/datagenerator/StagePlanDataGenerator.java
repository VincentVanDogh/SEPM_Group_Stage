package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StagePlanDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StageTemplateRepository stageTemplateRepository;
    private final SectorRepository sectorRepository;
    private final SectorMapRepository sectorMapRepository;

    public StagePlanDataGenerator(StageTemplateRepository stageTemplateRepository,
                                  SectorRepository sectorRepository,
                                  SectorMapRepository sectorMapRepository) {
        this.stageTemplateRepository = stageTemplateRepository;
        this.sectorRepository = sectorRepository;
        this.sectorMapRepository = sectorMapRepository;
    }

    public void generateStagePlans() {
        if (stageTemplateRepository.findAll().size() > 0) {
            LOGGER.info("Stage templates already generated");
            return;
        }
        LOGGER.info("Generating Stage Plans");

        generateSeatedSector(1L, 4, 10);
        generateSeatedSector(2L, 2, 20);
        generateSeatedSector(3L, 6, 10);
        generateSeatedSector(4L, 3, 20);
        generateSeatedSector(5L, 3, 16);
        generateSeatedSector(6L, 4, 20);
        generateSeatedSector(7L, 7, 20);
        generateSeatedSector(8L, 1, 20);
        generateStandingSector(9L, 20);
        generateStandingSector(10L, 40);
        generateStandingSector(11L, 60);
        generateStandingSector(12L, 100);
        generateSeatedSector(13L, 10, 10);
        generateSeatedSector(23L, 5, 20);
        generateSeatedSector(33L, 6, 6);
        generateSeatedSector(43L, 8, 8);
        generateSeatedSector(53L, 6, 16);
        generateSeatedSector(63L, 6, 20);
        generateSeatedSector(73L, 3, 15);
        generateSeatedSector(83L, 5, 15);

        generateStagePlan1();
        generateStagePlan2();
        generateStagePlan3();
        generateStagePlan4();
        generateStagePlan5();
        generateStagePlan6();
        generateStagePlan7();
        generateStagePlan8();
        generateStagePlan9();
        generateStagePlan10();
        generateStagePlan11();
        generateStagePlan12();
        generateStagePlan13();
        generateStagePlan14();
        generateStagePlan15();
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   40N 40N 40N 40N 40N E
     * E   40N 60N 60N 60N 40N E
     * E   E   E   E   E   E   E
     * */
    public void generateStagePlan1() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(1L)
            .withName("Template Stage 1")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 1, Orientation.NORTH, 0, 1, 81);

        linkSeatedSector(stage, (long) 2, Orientation.NORTH, -1, 1, 41);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, 1, 1, 121);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, -2, 1, 1);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, 2, 1, 161);

        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 0, 2, 301);

        linkSeatedSector(stage, (long) 4, Orientation.NORTH, -1, 2, 241);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 1, 2, 361);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, -2, 2, 201);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, 2, 2, 421);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   40N 40N 40N E   E
     * E   E   60N 60N 60N E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan2() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(2L)
            .withName("Template Stage 2")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 1, Orientation.NORTH, 0, 1, 41);

        linkSeatedSector(stage, (long) 2, Orientation.NORTH, -1, 1, 1);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, 1, 1, 81);

        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 0, 2, 181);

        linkSeatedSector(stage, (long) 4, Orientation.NORTH, -1, 2, 121);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 1, 2, 241);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   48S E   E   E
     * E   E   E   60S E   E   E
     * E   E   E   S   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan3() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(3L)
            .withName("Basic Stage facing South")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 5, Orientation.SOUTH, 0, -2, 1);

        linkStandingSector(stage, (long) 11, Orientation.SOUTH, 0, -1, 49);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   E   60N E   E   E
     * E   E   E   48N E   E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan4() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(4L)
            .withName("Basic Stage facing North")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 5, Orientation.NORTH, 0, 2, 1);

        linkStandingSector(stage, (long) 11, Orientation.NORTH, 0, 1, 49);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   60W 48W E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan5() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(5L)
            .withName("Basic Stage facing West")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 5, Orientation.WEST, 2, 0, 1);

        linkStandingSector(stage, (long) 11, Orientation.WEST, 1, 0, 49);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   48E 60E S   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan6() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(6L)
            .withName("Basic Stage facing East")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 5, Orientation.EAST, -2, 0, 1);

        linkStandingSector(stage, (long) 11, Orientation.EAST, -1, 0, 49);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   20S 60S 20S E   E
     * E   E   60E S   60W E   E
     * E   E   20N 60N 20N E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan7() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(7L)
            .withName("Basic Stage standing only")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkStandingSector(stage, (long) 11, Orientation.SOUTH, 0, -1, 21);
        linkStandingSector(stage, (long) 11, Orientation.NORTH, 0, 1, 241);
        linkStandingSector(stage, (long) 11, Orientation.EAST, -1, 0, 101);
        linkStandingSector(stage, (long) 11, Orientation.WEST, 1, 0, 161);
        linkStandingSector(stage, (long) 9, Orientation.SOUTH, -1, -1, 1);
        linkStandingSector(stage, (long) 9, Orientation.SOUTH, 1, -1, 81);
        linkStandingSector(stage, (long) 9, Orientation.NORTH, -1, 1, 221);
        linkStandingSector(stage, (long) 9, Orientation.NORTH, 1, 1, 301);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   40N 40N 40N E   E
     * E   E   80N 80N 80N E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan8() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(8L)
            .withName("Template Stage 8")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 1, Orientation.NORTH, -1, 1, 1);
        linkSeatedSector(stage, (long) 1, Orientation.NORTH, 0, 1, 41);
        linkSeatedSector(stage, (long) 1, Orientation.NORTH, 1, 1, 81);

        linkSeatedSector(stage, (long) 6, Orientation.NORTH, -1, 2, 121);
        linkSeatedSector(stage, (long) 6, Orientation.NORTH, 0, 2, 201);
        linkSeatedSector(stage, (long) 6, Orientation.NORTH, 1, 2, 281);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   60N 60N 60N E   E
     * E   E   48N 48N 48N E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan9() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(9L)
            .withName("Template Stage 9")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 4, Orientation.NORTH, -1, 1, 1);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 0, 1, 61);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 1, 1, 121);

        linkSeatedSector(stage, (long) 5, Orientation.NORTH, -1, 2, 181);
        linkSeatedSector(stage, (long) 5, Orientation.NORTH, 0, 2, 229);
        linkSeatedSector(stage, (long) 5, Orientation.NORTH, 1, 2, 277);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   40N 40N 40N E   E
     * E   E   60N 60N 60N E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan10() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(10L)
            .withName("Template Stage 10")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 1, Orientation.NORTH, -1, 1, 1);
        linkSeatedSector(stage, (long) 1, Orientation.NORTH, 0, 1, 41);
        linkSeatedSector(stage, (long) 1, Orientation.NORTH, 1, 1, 81);

        linkSeatedSector(stage, (long) 3, Orientation.NORTH, -1, 2, 121);
        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 0, 2, 181);
        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 1, 2, 241);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   20N 20N 20N E   E
     * E   E   40N 40N 40N E   E
     * E   E   60N 60N 60N E   E
     * */
    private void generateStagePlan11() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(11L)
            .withName("Template Stage 11")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 8, Orientation.NORTH, -1, 1, 1);
        linkSeatedSector(stage, (long) 8, Orientation.NORTH, 0, 1, 21);
        linkSeatedSector(stage, (long) 8, Orientation.NORTH, 1, 1, 41);

        linkSeatedSector(stage, (long) 2, Orientation.NORTH, -1, 2, 61);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, 0, 2, 101);
        linkSeatedSector(stage, (long) 2, Orientation.NORTH, 1, 2, 141);

        linkSeatedSector(stage, (long) 4, Orientation.NORTH, -1, 3, 181);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 0, 3, 241);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 1, 3, 301);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   60E 40E E   E   E   E
     * E   60E 40E S   E   E   E
     * E   60E 40E E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan12() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(12L)
            .withName("Template Stage 12")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkSeatedSector(stage, (long) 1, Orientation.EAST, -1, -1, 1);
        linkSeatedSector(stage, (long) 1, Orientation.EAST, -1, 0, 41);
        linkSeatedSector(stage, (long) 1, Orientation.EAST, -1, 1, 81);

        linkSeatedSector(stage, (long) 3, Orientation.EAST, -2, -1, 121);
        linkSeatedSector(stage, (long) 3, Orientation.EAST, -2, 0, 181);
        linkSeatedSector(stage, (long) 3, Orientation.EAST, -2, 1, 241);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   E  100N E   E   E
     * E   E   60N 60N 60N E   E
     * E   48N 48N 48N 48N 48N E
     * */
    private void generateStagePlan13() {
        StageTemplate stage = StageTemplate.StageTemplateBuilder.aStage()
            .withId(13L)
            .withName("Template Stage 13")
            .build();
        LOGGER.debug("saving stage {}", stage);
        stageTemplateRepository.save(stage);

        linkStandingSector(stage, (long) 12, Orientation.NORTH, 0, 1, 421);

        linkSeatedSector(stage, (long) 4, Orientation.NORTH, -1, 2, 1);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 0, 2, 61);
        linkSeatedSector(stage, (long) 4, Orientation.NORTH, 1, 2, 121);

        linkSeatedSector(stage, (long) 3, Orientation.NORTH, -2, 3, 181);
        linkSeatedSector(stage, (long) 3, Orientation.NORTH, -1, 3, 229);
        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 0, 3, 277);
        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 1, 3, 325);
        linkSeatedSector(stage, (long) 3, Orientation.NORTH, 2, 3, 373);
    }

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   40N 40N 40N E   E
     * E   E   60N 60N 60N E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan14() {}

    /** Visual representation.
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   E   E   E   E
     * E   E   E   S   E   E   E
     * E   E   40N 40N 40N E   E
     * E   E   60N 60N 60N E   E
     * E   E   E   E   E   E   E
     * */
    private void generateStagePlan15() {}

    private void generateSeatedSector(Long id, Integer numRows, Integer numColumns) {
        Sector sector = Sector.SectorBuilder.aSector()
            .withId(id)
            .withNumberOfSeats(numRows * numColumns)
            .withNumberRows(numRows)
            .withNumberColumns(numColumns)
            .withStanding(false)
            .build();
        LOGGER.debug("saving seated sector {}", sector);
        sectorRepository.save(sector);
    }

    private void generateStandingSector(Long id, Integer numSeats) {
        Sector sector = Sector.SectorBuilder.aSector()
            .withId(id)
            .withNumberOfSeats(numSeats)
            .withStanding(true)
            .build();
        LOGGER.debug("saving standing sector {}", sector);
        sectorRepository.save(sector);
    }

    private void linkSeatedSector(StageTemplate stageTemplate, Long sectorId, Orientation o, Integer x, Integer y, Integer firstSeatNr) {
        Sector sector;
        Optional<Sector> maybeSector = sectorRepository.findById(sectorId);
        if (maybeSector.isPresent()) {
            sector = maybeSector.get();
        } else {
            throw new RuntimeException(String.format("Failed to link seated sector. Could not find stage with id %s", 2));
        }
        SectorMap sectorMap = SectorMap.SectorMapBuilder.aSectorMap()
            .withSectorX(x)
            .withSectorY(y)
            .withOrientation(o)
            .withFirstSeatNr(firstSeatNr)
            .withStageTemplate(stageTemplate)
            .withSector(sector)
            .build();
        sectorMapRepository.save(sectorMap);

        List<SectorMap> sectorMaps = stageTemplate.getSectorMaps();
        if (sectorMaps == null) {
            sectorMaps = new ArrayList<>();
        }
        sectorMaps.add(sectorMap);
        stageTemplate.setSectorMaps(sectorMaps);
    }

    private void linkStandingSector(StageTemplate stageTemplate, Long sectorId, Orientation o, Integer x, Integer y, Integer sectorIdentifier) {
        Sector sector;
        Optional<Sector> maybeSector = sectorRepository.findById(sectorId);
        if (maybeSector.isPresent()) {
            sector = maybeSector.get();
        } else {
            throw new RuntimeException(String.format("Failed to link standing sector. Could not find stage with id %s", 2));
        }
        SectorMap sectorMap = SectorMap.SectorMapBuilder.aSectorMap()
            .withSectorX(x)
            .withSectorY(y)
            .withOrientation(o)
            .withFirstSeatNr(sectorIdentifier)
            .withStageTemplate(stageTemplate)
            .withSector(sector)
            .build();
        sectorMapRepository.save(sectorMap);

        List<SectorMap> sectorMaps = stageTemplate.getSectorMaps();
        if (sectorMaps == null) {
            sectorMaps = new ArrayList<>();
        }
        sectorMaps.add(sectorMap);
        stageTemplate.setSectorMaps(sectorMaps);
    }



}
