package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.UserMapper;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.UserStatusDTO;
import fabio.sicredi.evaluation.domain.User;
import fabio.sicredi.evaluation.exception.InvalidCPFFormatException;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    public UserDTO addUser(final UserDTO userDTO) throws InvalidCPFFormatException {
        User user = userMapper.userDtoToUser(userDTO);
        if(!user.hasValidCpf()) throw new InvalidCPFFormatException();
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findUser(final Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.userToUserDTO(user);
    }

    @Override
    public UserStatusDTO ableToVote(final Long cpf) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(externalCpfUrl)
                .path(String.valueOf(cpf));
        return restTemplate.getForObject(builder.toUriString(), UserStatusDTO.class);
    }
}
