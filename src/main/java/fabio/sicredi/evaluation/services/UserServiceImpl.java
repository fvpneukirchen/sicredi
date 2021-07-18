package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.UserMapper;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.domain.User;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    private UserRepository userRepository;

    @Autowired
    public void setUserMapper(final UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUserRepository(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO findUser(final Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.userToUserDTO(user);
    }
}
