package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;

public interface PollService {

    PollDTO createPoll(final PollDTO pollDTO);
}
