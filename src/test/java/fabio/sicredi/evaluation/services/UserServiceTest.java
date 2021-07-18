package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.UserMapper;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.UserStatusDTO;
import fabio.sicredi.evaluation.domain.User;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    public static final Long ID = 1L;
    public static final String NAME = "John";
    public static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";
    public static final String UNABLE_TO_VOTE = "UNABLE_TO_VOTE";

    UserServiceImpl userService;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Value("${external.cpf.url}")
    String externalCpfUrl;

    @Mock
    UserRepository userRepository;

    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl();
        userService.setUserMapper(userMapper);
        userService.setUserRepository(userRepository);
        userService.setUserRestTemplate(restTemplate);
        userService.setUserExternalCpfUrl(externalCpfUrl);
    }

    @Test
    public void fetchUser() {
        //given
        User savedUser = new User();
        savedUser.setId(ID);
        savedUser.setName(NAME);

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(savedUser));

        //when
        UserDTO userDTO = userService.findUser(ID);

        //then
        Assertions.assertEquals(ID, userDTO.getId());
        Assertions.assertEquals(NAME, userDTO.getName());
    }

    @Test
    public void failsToFetchUser() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        //when
        NoSuchElementException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findUser(ID));

        //then
        Assertions.assertNotNull(thrown);
    }

    @Test
    public void fetchUserIsAbleToVote() {
        //given
        UserStatusDTO userStatusDTO = new UserStatusDTO(ABLE_TO_VOTE);


        when(restTemplate.getForObject(anyString(), any())).thenReturn(userStatusDTO);

        //when
        UserStatusDTO returnedUserStatusDTO = userService.ableToVote(ID);

        //then
        Assertions.assertEquals(ABLE_TO_VOTE, returnedUserStatusDTO.getStatus());
    }

    @Test
    public void fetchUserIsUnableToVote() {
        //given
        UserStatusDTO userStatusDTO = new UserStatusDTO(UNABLE_TO_VOTE);


        when(restTemplate.getForObject(anyString(), any())).thenReturn(userStatusDTO);

        //when
        UserStatusDTO returnedUserStatusDTO = userService.ableToVote(ID);

        //then
        Assertions.assertEquals(UNABLE_TO_VOTE, returnedUserStatusDTO.getStatus());
    }

}
