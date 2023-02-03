package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StageMapper {

    Stage stageDtoToStage(StageDto stageDto);

    StageDto stageToStageDto(Stage stage);

    @Mapping(target = "stageTemplateId", source = "stage.stageTemplate.id")
    CreateNewStageDto stageToCreateNewStageDto(Stage stage);
}
