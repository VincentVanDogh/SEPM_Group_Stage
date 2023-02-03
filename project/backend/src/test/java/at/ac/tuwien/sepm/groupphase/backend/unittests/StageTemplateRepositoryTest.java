package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class StageTemplateRepositoryTest implements TestData {

    @Autowired
    private StageTemplateRepository stageTemplateRepository;

    @BeforeEach
    public void beforeEach() {
        stageTemplateRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenSaveStageTemplate_thenFindListWithOneElementAndFindStageTemplateById() {
        StageTemplate stageTemplate = StageTemplate.StageTemplateBuilder.aStage()
            .withId(FIRST_TEST_STAGE_TEMPLATE_ID)
            .withName(FIRST_TEST_STAGE_TEMPLATE_NAME)
            .withSectorMaps(new ArrayList<>())
            .build();

        stageTemplateRepository.save(stageTemplate);

        assertAll(
            () -> assertEquals(1, stageTemplateRepository.findAll().size()),
            () -> assertNotNull(stageTemplateRepository.findById(stageTemplate.getId()))
        );
    }

}
