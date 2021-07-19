package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;

public interface VoteService {

    VoteDTO registerVote(final Long id, final VoteDTO voteDTO);

    boolean hasVoted(final Long id, final VoteDTO voteDTO);

    PollDTO countVotes(final PollDTO pollDTO);
}
