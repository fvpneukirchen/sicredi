package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.PollMapper;
import fabio.sicredi.evaluation.api.v1.mapper.VoteMapper;
import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.ResultDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.domain.Vote;
import fabio.sicredi.evaluation.domain.VoteKey;
import fabio.sicredi.evaluation.repositories.VoteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class VoteServiceImpl implements VoteService {

    private PollMapper pollMapper;

    private VoteMapper voteMapper;

    private VoteRepository voteRepository;

    @Autowired
    public void setPollMapper(final PollMapper pollMapper) {
        this.pollMapper = pollMapper;
    }

    @Autowired
    public void setVoteMapper(final VoteMapper voteMapper) {
        this.voteMapper = voteMapper;
    }

    @Autowired
    public void setVoteRepository(final VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public VoteDTO registerVote(final Long id, final VoteDTO voteDTO) {
        Vote vote = voteMapper.voteDTOtoVote(voteDTO);
        vote.setPollId(id);

        Vote registeredVote = voteRepository.save(vote);

        return voteMapper.voteToVoteDTO(registeredVote);
    }

    @Override
    public boolean hasVoted(final Long id, final VoteDTO voteDTO) {
        VoteKey voteKey = new VoteKey(id, voteDTO.getUserId());
        return voteRepository.findById(voteKey).isPresent();
    }

    @Override
    public PollDTO countVotes(final PollDTO pollDTO) {
        List<Object[]> votes = voteRepository.countVotes(pollDTO.getId());

        pollDTO.setResult(convertVotesIntoResultDTO(votes));

        return pollDTO;
    }

    private List<ResultDTO> convertVotesIntoResultDTO(final List<Object[]> votes) {
        return votes.stream().map(vote -> new ResultDTO(fetchVoteType((boolean) vote[0]), (Long) vote[1])).collect(Collectors.toList());
    }

    private String fetchVoteType(final boolean vote) {
        return vote ? "YES" : "NO";
    }
}
