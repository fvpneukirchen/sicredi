package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.UserMapper;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.UserStatusDTO;
import fabio.sicredi.evaluation.domain.User;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    private UserRepository userRepository;

    private RestTemplate restTemplate;

    @Value("${external.cpf.url}")
    private String externalCpfUrl;

    @Autowired
    public void setUserMapper(final UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUserRepository(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setUserExternalCpfUrl(final String externalCpfUrl) {
        this.externalCpfUrl = externalCpfUrl;
    }

    @Override
    public UserDTO findUser(final Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.userToUserDTO(user);
    }

    @Override
    public UserStatusDTO ableToVote(final Long cpf) {
        String url = String.format(externalCpfUrl, cpf);
        return restTemplate.getForObject(url, UserStatusDTO.class);
    }
}
