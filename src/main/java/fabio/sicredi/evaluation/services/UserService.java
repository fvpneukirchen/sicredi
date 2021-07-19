package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.UserStatusDTO;

public interface UserService {

    UserDTO addUser(final UserDTO userDTO);

    UserDTO findUser(final Long id);

    UserStatusDTO ableToVote(final Long cpf);
}
