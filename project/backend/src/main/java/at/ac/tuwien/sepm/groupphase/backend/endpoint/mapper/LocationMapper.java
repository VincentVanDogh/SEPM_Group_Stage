package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface LocationMapper {
    Location createLocationDtoToLocation(CreateLocationDto createLocationDto);

    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "venueName", source = "location.venueName")
    @Mapping(target = "street", source = "location.address.street")
    @Mapping(target = "city", source = "location.address.city")
    @Mapping(target = "country", source = "location.address.country")
    @Mapping(target = "postalCode", source = "location.address.postalCode")
    @Mapping(target = "stages", source = "stages")
    CreateLocationDto locationAndStagesToCreateLocationDto(Location location, List<CreateNewStageDto> stages);

    @Mapping(target = "addressId", source = "address.id")
    LocationDto locationToLocationDto(Location location);

}
