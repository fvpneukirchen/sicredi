package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.UserDTO;

public interface UserService {

    UserDTO findUser (final Long id);
}
