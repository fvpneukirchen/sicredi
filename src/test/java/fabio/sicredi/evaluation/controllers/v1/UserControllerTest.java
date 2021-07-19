package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.InvalidCPFFormatException;
import fabio.sicredi.evaluation.services.PollService;
import fabio.sicredi.evaluation.services.UserService;
import fabio.sicredi.evaluation.services.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerTest extends AbstractRestControllerTest {

    public static final Long ID = 1L;
    public static final String NAME = "John";
    public static final Long CPF = 31260008002L;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void addUser() throws Exception {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setName(NAME);
        userDTO.setCpf(CPF);

        UserDTO returnUserDTO = new UserDTO();
        returnUserDTO.setId(ID);
        returnUserDTO.setName(NAME);
        returnUserDTO.setCpf(CPF);

        when(userService.addUser(userDTO)).thenReturn(returnUserDTO);

        //when/then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cpf", is(CPF)))
                .andExpect(jsonPath("$.name", is(NAME)));
    }

    @Test
    public void failToAddUserDueMissingCPF() throws Exception {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setName(NAME);

        //when/then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failToAddUserDueInvalidCPF() throws Exception {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setName(NAME);
        userDTO.setCpf(ID);

        when(userService.addUser(userDTO)).thenThrow(InvalidCPFFormatException.class);

        //when/then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isBadRequest());
    }
}
