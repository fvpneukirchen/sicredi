package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.UserMapper;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.domain.User;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    public static final Long ID = 1L;
    public static final String NAME = "John";

    UserServiceImpl userService;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl();
        userService.setUserMapper(userMapper);
        userService.setUserRepository(userRepository);
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

}
