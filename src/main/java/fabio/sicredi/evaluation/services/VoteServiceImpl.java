package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.VoteMapper;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.domain.Vote;
import fabio.sicredi.evaluation.domain.VoteKey;
import fabio.sicredi.evaluation.repositories.VoteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class VoteServiceImpl implements VoteService {

    private VoteMapper voteMapper;

    private VoteRepository voteRepository;

    @Autowired
    public void setVoteMapper(final VoteMapper voteMapper) {
        this.voteMapper = voteMapper;
    }

    @Autowired
    public void setVoteRepository(final VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public VoteDTO registerVote(final VoteDTO voteDTO) {
        Vote vote = voteMapper.voteDTOtoVote(voteDTO);

        Vote registeredVote = voteRepository.save(vote);

        return voteMapper.voteToVoteDTO(registeredVote);
    }

    @Override
    public boolean hasVoted(final VoteDTO voteDTO) {
        VoteKey voteKey = new VoteKey(voteDTO.getPollId(), voteDTO.getUserId());
        return voteRepository.findById(voteKey).isPresent();
    }
}
