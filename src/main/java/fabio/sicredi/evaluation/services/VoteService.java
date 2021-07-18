package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteResultDTO;

public interface VoteService {

    VoteDTO registerVote(final VoteDTO voteDTO);

    boolean hasVoted(final VoteDTO voteDTO);

    VoteResultDTO countVotes(final PollDTO pollDTO);
}
