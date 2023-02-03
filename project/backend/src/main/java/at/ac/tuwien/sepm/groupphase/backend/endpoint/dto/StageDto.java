package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Size;

public record StageDto(

    Long id,
    @Size(max = 255, message = "Stage name must not exceed 255 characters")
    String name
) {
}
