package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.Duration;

public interface PollService {

    PollDTO createPoll(final PollDTO pollDTO);

    PollDTO findPoll(final Long id);

    int openPoll(final Long id, final Duration duration);

}
