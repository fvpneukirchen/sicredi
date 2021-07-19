package fabio.sicredi.evaluation.api.v1.mapper;

import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(final User user);

    User userDtoToUser(final UserDTO userDTO);
}
