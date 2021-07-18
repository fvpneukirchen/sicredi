package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.ResultDTO;
import fabio.sicredi.evaluation.domain.Duration;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.services.PollService;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PollControllerTest extends AbstractRestControllerTest {

    public static final Long ID = 1L;
    public static final String REASON = "Sell stocks";
    public static final String NO = "NO";
    public static final String YES = "YES";

    @Mock
    PollService pollService;

    @Mock
    VoteService voteService;

    @InjectMocks
    PollController pollController;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(pollController).build();
    }

    @Test
    public void createNewPoll() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setReason(REASON);

        PollDTO returnPollDTO = new PollDTO();
        returnPollDTO.setId(ID);
        returnPollDTO.setReason(REASON);
        returnPollDTO.setStatus(PollStatus.CREATED.getStatus());

        when(pollService.createPoll(pollDTO)).thenReturn(returnPollDTO);

        //when/then
        mockMvc.perform(post("/api/v1/polls/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reason", is(REASON)))
                .andExpect(jsonPath("$.status", is(PollStatus.CREATED.getStatus())));
    }

    @Test
    public void failsToCreateNewPollDueMissingReason() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();

        //when/then
        mockMvc.perform(post("/api/v1/polls/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void openPoll() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setDuration(new Duration(5, SECONDS));

        PollDTO returnedDTO = new PollDTO();
        returnedDTO.setId(ID);
        returnedDTO.setReason(REASON);
        returnedDTO.setStatus(PollStatus.CREATED.getStatus());

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedDTO);
        when(pollService.openPoll(anyLong(), any())).thenReturn(1);

        //then
        mockMvc.perform(patch("/api/v1/polls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reason", is(REASON)))
                .andExpect(jsonPath("$.status", is(PollStatus.OPEN.getStatus())));
    }

    @Test
    public void failsNotFindingPollToOpen() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setDuration(new Duration(5, SECONDS));

        //when
        when(pollService.findPoll(anyLong())).thenThrow(PollNotFoundException.class);

        //then
        mockMvc.perform(patch("/api/v1/polls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void failsTryingToOpenAlreadyOpenedPoll() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setDuration(new Duration(5, SECONDS));

        PollDTO returnedDTO = new PollDTO();
        returnedDTO.setId(ID);
        returnedDTO.setReason(REASON);
        returnedDTO.setStatus(PollStatus.OPEN.getStatus());

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedDTO);

        //then
        mockMvc.perform(patch("/api/v1/polls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void failsTryingToOpenAlreadyClosedPoll() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setDuration(new Duration(5, SECONDS));

        PollDTO returnedDTO = new PollDTO();
        returnedDTO.setId(ID);
        returnedDTO.setReason(REASON);
        returnedDTO.setStatus(PollStatus.CLOSED.getStatus());

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedDTO);

        //then
        mockMvc.perform(patch("/api/v1/polls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void failsNotOpeningPoll() throws Exception {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setDuration(new Duration(5, SECONDS));

        PollDTO returnedDTO = new PollDTO();
        returnedDTO.setId(ID);
        returnedDTO.setReason(REASON);
        returnedDTO.setStatus(PollStatus.CREATED.getStatus());

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedDTO);
        when(pollService.openPoll(anyLong(), any())).thenReturn(0);

        //then
        mockMvc.perform(patch("/api/v1/polls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pollDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void countPollVotes() throws Exception {
        //given
        PollDTO returnedPollDTO = new PollDTO();
        returnedPollDTO.setId(ID);
        returnedPollDTO.setReason(REASON);
        returnedPollDTO.setStatus(PollStatus.CLOSED.getStatus());

        List<ResultDTO> resultDTOS = new ArrayList<>();
        resultDTOS.add(new ResultDTO(YES, 1L));
        resultDTOS.add(new ResultDTO(NO, 1L));

        PollDTO voteResultDTO = new PollDTO();
        voteResultDTO.setId(returnedPollDTO.getId());
        voteResultDTO.setReason(returnedPollDTO.getReason());
        voteResultDTO.setStatus(returnedPollDTO.getStatus());
        voteResultDTO.setResult(resultDTOS);

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedPollDTO);
        when(voteService.countVotes(any())).thenReturn(voteResultDTO);

        //then
        mockMvc.perform(get("/api/v1/polls/1/votes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reason", is(REASON)))
                .andExpect(jsonPath("$.status", is(PollStatus.CLOSED.getStatus())))
                .andExpect(jsonPath("$.result[0].answer", is(YES)))
                .andExpect(jsonPath("$.result[0].count", is(1)))
                .andExpect(jsonPath("$.result[1].answer", is(NO)))
                .andExpect(jsonPath("$.result[1].count", is(1)));
    }

    @Test
    public void failsToCountPollVotesDueNotOpenedPoll() throws Exception {
        //given
        PollDTO returnedPollDTO = new PollDTO();
        returnedPollDTO.setId(ID);
        returnedPollDTO.setReason(REASON);
        returnedPollDTO.setStatus(PollStatus.CREATED.getStatus());

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedPollDTO);

        //then
        mockMvc.perform(get("/api/v1/polls/1/votes"))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void failsToCountPollVotesDueNotFoundPoll() throws Exception {
        //when
        when(pollService.findPoll(anyLong())).thenThrow(PollNotFoundException.class);

        //then
        mockMvc.perform(get("/api/v1/polls/1/votes"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void failsToCountPollVotesDueNotExecutedCount() throws Exception {
        //given
        PollDTO returnedPollDTO = new PollDTO();
        returnedPollDTO.setId(ID);
        returnedPollDTO.setReason(REASON);
        returnedPollDTO.setStatus(PollStatus.CLOSED.getStatus());

        //when
        when(pollService.findPoll(anyLong())).thenReturn(returnedPollDTO);

        //then
        mockMvc.perform(get("/api/v1/polls/1/votes"))
                .andExpect(status().isInternalServerError());
    }

}
