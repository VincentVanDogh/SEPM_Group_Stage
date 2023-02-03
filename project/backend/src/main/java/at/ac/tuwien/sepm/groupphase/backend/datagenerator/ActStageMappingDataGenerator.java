package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActSectorMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ActStageMappingDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ActRepository actRepository;
    private final SectorMapRepository sectorMapRepository;
    private final ActSectorMappingRepository actSectorMappingRepository;
    private final Faker faker = new Faker(new Random(1));

    public ActStageMappingDataGenerator(ActRepository actRepository,
                                        SectorMapRepository sectorMapRepository,
                                        ActSectorMappingRepository actSectorMappingRepository) {
        this.actRepository = actRepository;
        this.sectorMapRepository = sectorMapRepository;
        this.actSectorMappingRepository = actSectorMappingRepository;
    }

    public void generateActSectorMappings() {
        if (!actSectorMappingRepository.findAll().isEmpty()) {
            LOGGER.info("ActSectorMappings are already generated");
            return;
        }
        LOGGER.info("Generating ActSectorMappings");
        generateAllActSectorMappings();

    }

    @Transactional
    void generateAllActSectorMappings() {
        LOGGER.debug("generateAllActSectorMappings()");

        List<ActSectorMapping> actSectorMappings = new ArrayList<>();
        List<Act> acts = actRepository.findAll();

        for (Act act : acts) {
            List<SectorMap> sectorMaps = sectorMapRepository.loadSectorMaps(act.getStage().getId());

            for (SectorMap sectorMap : sectorMaps) {
                ActSectorMapping actSectorMapping = ActSectorMapping.ActSectorMappingBuilder.aActSectorMap()
                    .withPrice(1000 + faker.number().positive() % 20000).withSectorMap(sectorMap).withAct(act).build();
                actSectorMappings.add(actSectorMapping);
            }
        }

        actSectorMappingRepository.saveAll(actSectorMappings);
    }
}
