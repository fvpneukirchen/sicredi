package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.PollMapper;
import fabio.sicredi.evaluation.api.v1.mapper.VoteMapper;
import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.PollResultDTO;
import fabio.sicredi.evaluation.api.v1.model.ResultDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteEntryDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.domain.Vote;
import fabio.sicredi.evaluation.repositories.VoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class VoteServiceTest {

    public static final Long ID = 1L;
    public static final String REASON = "Sell stocks";

    VoteServiceImpl voteService;

    PollMapper pollMapper =PollMapper.INSTANCE;

    VoteMapper voteMapper = VoteMapper.INSTANCE;

    @Mock
    VoteRepository voteRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        voteService = new VoteServiceImpl();
        voteService.setPollMapper(pollMapper);
        voteService.setVoteMapper(voteMapper);
        voteService.setVoteRepository(voteRepository);
    }

    @Test
    public void registeredVote() {
        //given
        VoteEntryDTO voteEntryDTO = new VoteEntryDTO(ID, true);

        Vote saveVote = new Vote(ID, ID, true);

        when(voteRepository.save(any())).thenReturn(saveVote);

        //when
        VoteDTO saveDTO = voteService.registerVote(ID, voteEntryDTO);

        //then
        Assertions.assertEquals(ID, saveDTO.getPollId());
        Assertions.assertEquals(voteEntryDTO.getUserId(), saveDTO.getUserId());
        Assertions.assertEquals(voteEntryDTO.isInAccordance(), saveDTO.isInAccordance());
    }

    @Test
    public void hasVotedAlreadyExistentVote() {
        //given
        VoteEntryDTO voteEntryDTO = new VoteEntryDTO(ID, true);

        Vote saveVote = new Vote(ID, ID, true);

        when(voteRepository.findById(any())).thenReturn(java.util.Optional.of(saveVote));

        //when
        boolean hasVoted = voteService.hasVoted(ID, voteEntryDTO);

        //then
        Assertions.assertTrue(hasVoted);
    }

    @Test
    public void hasVotedNonExistentVote() {
        //given
        VoteEntryDTO voteEntryDTO = new VoteEntryDTO(ID, true);

        when(voteRepository.findById(any())).thenReturn(java.util.Optional.empty());

        //when
        boolean hasVoted = voteService.hasVoted(ID, voteEntryDTO);

        //then
        Assertions.assertFalse(hasVoted);
    }

    @Test
    public void countVotes() {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setId(ID);
        pollDTO.setReason(REASON);
        pollDTO.setStatus(PollStatus.CLOSED.getStatus());

        List<Object[]> results = new ArrayList<>();

        Object[] yesVotes = new Object[2];
        yesVotes[0] = true;
        yesVotes[1] = 1L;

        List<ResultDTO> resultDTOS = new ArrayList<>();
        resultDTOS.add(new ResultDTO("YES", 1L));

        results.add(yesVotes);

        when(voteRepository.countVotes(any())).thenReturn(results);

        //when
        PollResultDTO pollDTOWithResult = voteService.countVotes(pollDTO);

        //then
        Assertions.assertEquals(pollDTO.getId(), pollDTOWithResult.getId());
        Assertions.assertEquals(pollDTO.getStatus(), pollDTOWithResult.getStatus());
        Assertions.assertEquals(pollDTO.getReason(), pollDTOWithResult.getReason());
        Assertions.assertEquals(resultDTOS, pollDTOWithResult.getResult());
    }
}
