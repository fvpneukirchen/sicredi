package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.UserStatusDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.domain.UserStatus;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class VoteControllerTest extends AbstractRestControllerTest {

    public static final Long ID = 1L;
    public static final String NAME = "John";
    public static final String REASON = "Sell stocks";
    public static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";
    public static final String UNABLE_TO_VOTE = "UNABLE_TO_VOTE";

    @Mock
    PollService pollService;

    @Mock
    UserService userService;

    @Mock
    VoteService voteService;

    @InjectMocks
    VoteController voteController;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(voteController).build();
    }

    @Test
    public void registerNewVote() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.OPEN.getStatus());

        UserDTO userDTO = new UserDTO(ID, 31260008002L, NAME);
        UserStatusDTO userStatusDTO = new UserStatusDTO(ABLE_TO_VOTE);
        when(userService.ableToVote(anyLong())).thenReturn(userStatusDTO);

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);
        when(userService.findUser(anyLong())).thenReturn(userDTO);
        when(voteService.hasVoted(any())).thenReturn(false);
        when(voteService.registerVote(any())).thenReturn(voteDTO);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pollId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.inAccordance", is(true)));
    }

    @Test
    public void failsToRegisterNewVoteDueMissingPoll() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.OPEN.getStatus());

        when(pollService.findPoll(anyLong())).thenThrow(PollNotFoundException.class);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void failsToRegisterNewVoteDueMissingUser() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.OPEN.getStatus());

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);
        when(userService.findUser(anyLong())).thenThrow(UserNotFoundException.class);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void failsToRegisterNewVoteDuePollAlreadyClosed() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.CLOSED.getStatus());

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void failsToRegisterNewVoteDuePollNotOpened() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.CREATED.getStatus());

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void failsToRegisterNewVoteDuePolAlreadyVoted() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.OPEN.getStatus());

        UserDTO userDTO = new UserDTO(ID, 31260008002L, NAME);
        UserStatusDTO userStatusDTO = new UserStatusDTO(ABLE_TO_VOTE);

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);
        when(userService.findUser(anyLong())).thenReturn(userDTO);
        when(userService.ableToVote(anyLong())).thenReturn(userStatusDTO);
        when(voteService.hasVoted(any())).thenReturn(true);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void failsToRegisterNewVoteDueVoteNotBeenTriggered() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.OPEN.getStatus());

        UserDTO userDTO = new UserDTO(ID, 31260008002L, NAME);
        UserStatusDTO userStatusDTO = new UserStatusDTO(ABLE_TO_VOTE);

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);
        when(userService.findUser(anyLong())).thenReturn(userDTO);
        when(userService.ableToVote(anyLong())).thenReturn(userStatusDTO);
        when(voteService.hasVoted(any())).thenReturn(false);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void failsToRegisterNewVoteDueVoteUserVoteStatusIsUnable() throws Exception {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.OPEN.getStatus());

        UserDTO userDTO = new UserDTO(ID, 31260008002L, NAME);
        UserStatusDTO userStatusDTO = new UserStatusDTO(UNABLE_TO_VOTE);

        when(pollService.findPoll(anyLong())).thenReturn(returnPollDTO);
        when(userService.findUser(anyLong())).thenReturn(userDTO);
        when(userService.ableToVote(anyLong())).thenReturn(userStatusDTO);

        //when/then
        mockMvc.perform(post("/api/v1/votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(voteDTO)))
                .andExpect(status().isPreconditionFailed());
    }
}
