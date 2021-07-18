package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.exception.PollNotFoundException;

public interface PollService {

    PollDTO createPoll(final PollDTO pollDTO);

    PollDTO findPoll(final Long id) throws PollNotFoundException;

    int openPoll(final Long id, PollDTO pollDTO);

}
