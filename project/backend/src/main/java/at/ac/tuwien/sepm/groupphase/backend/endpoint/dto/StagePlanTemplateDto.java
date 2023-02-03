package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record StagePlanTemplateDto(
    Long stageId,

    String name,

    Integer totalSeatsNr,

    SpecificSectorDto[] sectorArray
) {
}
