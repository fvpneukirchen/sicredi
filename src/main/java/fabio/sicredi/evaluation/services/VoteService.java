package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.PollResultDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteEntryDTO;

public interface VoteService {

    VoteDTO registerVote(final Long id, final VoteEntryDTO voteEntryDTO);

    boolean hasVoted(final Long id, final VoteEntryDTO voteEntryDTO);

    PollResultDTO countVotes(final PollDTO pollDTO);
}
