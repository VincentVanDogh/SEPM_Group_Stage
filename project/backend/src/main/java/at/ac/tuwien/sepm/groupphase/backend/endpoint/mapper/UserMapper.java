package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
public interface UserMapper {
    ApplicationUser registrationDtoToApplicationUser(RegistrationDto registrationDto);

    RegistrationDto applicationUserToRegistrationDto(ApplicationUser applicationUser);

    ApplicationUser userCreateDtoToApplicationUser(UserCreateDto userCreateDto);

    List<UserDto> applicationUserToUserDto(List<ApplicationUser> all);
}
