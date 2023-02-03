package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

public record CreateNewStageDto(

    @Null
    Long id,

    @NotNull(message = "Stage Template ID name must not be empty")
    Long stageTemplateId,

    @NotEmpty(message = "Stage name must not be empty")
    @Size(max = 255, message = "Stage name must not exceed 255 characters")
    String name
) {}
