package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.VoteMapper;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class VoteServiceTest {

    public static final Long ID = 1L;

    VoteServiceImpl voteService;

    VoteMapper voteMapper = VoteMapper.INSTANCE;

    @Mock
    VoteRepository voteRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        voteService = new VoteServiceImpl();
        voteService.setVoteMapper(voteMapper);
        voteService.setVoteRepository(voteRepository);
    }

    @Test
    public void registeredVote() {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        Vote saveVote = new Vote(ID, ID, true);

        when(voteRepository.save(any())).thenReturn(saveVote);

        //when
        VoteDTO saveDTO = voteService.registerVote(voteDTO);

        //then
        Assertions.assertEquals(voteDTO.getPollId(), saveDTO.getPollId());
        Assertions.assertEquals(voteDTO.getUserId(), saveDTO.getUserId());
        Assertions.assertEquals(voteDTO.isInAccordance(), saveDTO.isInAccordance());
    }

    @Test
    public void hasVotedAlreadyExistentVote() {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        Vote saveVote = new Vote(ID, ID, true);

        when(voteRepository.findById(any())).thenReturn(java.util.Optional.of(saveVote));

        //when
        boolean hasVoted = voteService.hasVoted(voteDTO);

        //then
        Assertions.assertTrue(hasVoted);
    }

    @Test
    public void hasVotedNonExistentVote() {
        //given
        VoteDTO voteDTO = new VoteDTO(ID, ID, true);

        when(voteRepository.findById(any())).thenReturn(java.util.Optional.empty());

        //when
        boolean hasVoted = voteService.hasVoted(voteDTO);

        //then
        Assertions.assertFalse(hasVoted);
    }
}
