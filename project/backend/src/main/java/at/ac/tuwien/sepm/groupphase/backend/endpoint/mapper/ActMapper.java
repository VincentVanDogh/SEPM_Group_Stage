package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ActMapper {

    Act actDtoToAct(ActDto actDto);

    @Mapping(target = "stageId", source = "stage.id")
    ActDto actToActDto(Act act);

    @Mapping(target = "event", source = "event")
    ActDetailsDto actToActDetailsDto(Act act);
}
