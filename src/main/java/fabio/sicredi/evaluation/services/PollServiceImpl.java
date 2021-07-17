package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.PollMapper;
import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.Poll;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.repositories.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PollServiceImpl implements PollService {

    private PollMapper pollMapper;
    private PollRepository pollRepository;

    @Autowired
    public void setPollMapper(PollMapper pollMapper) {
        this.pollMapper = pollMapper;
    }

    @Autowired
    public void setCustomerRepository(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }


    @Override
    public PollDTO createPoll(final PollDTO pollDTO) {

        Poll poll = pollMapper.pollDTOtoPoll(pollDTO);
        poll.setStatus(PollStatus.CREATED.getStatus());

        Poll savePoll = pollRepository.save(poll);

        return pollMapper.pollToPollDTO(savePoll);
    }
}
