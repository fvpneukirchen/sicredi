package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.PollMapper;
import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.Duration;
import fabio.sicredi.evaluation.domain.Poll;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.repositories.PollRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;


@Service
@Log4j2
@EnableAsync
@EnableScheduling
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

    @Override
    public PollDTO findPoll(final Long id) {
        Poll returnedPoll = pollRepository.findById(id).orElseThrow(PollNotFoundException::new);

        return pollMapper.pollToPollDTO(returnedPoll);
    }


    @Override
    public int openPoll(final Long id, final Duration duration) {
        int affectedPolls = pollRepository.updateStatus(id, PollStatus.OPEN.getStatus());
        if (affectedPolls == 1) closeOpenedPoll(id, duration);

        return affectedPolls;
    }

    private void closeOpenedPoll(final Long id, final Duration duration) {
        log.trace("Scheduling closure of Poll: " + id);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Duration checkedDuration = checkDuration(duration);

        Runnable task = () -> {
            log.debug("Closing Poll: " + id);

            int affectedPolls = pollRepository.updateStatus(id, PollStatus.CLOSED.getStatus());

            if (affectedPolls == 1) {
                log.debug(String.format("Poll %d is now CLOSED", id));
            } else if (affectedPolls > 1) {
                log.warn(String.format("Something went wrong when closing the Poll %d", id));
            } else {
                log.warn(String.format("Poll %d not closed", id));
            }
        };

        executor.schedule(task, checkedDuration.getDelay(), checkedDuration.getTimeUnit());
        executor.shutdown();
    }

    private Duration checkDuration(final Duration duration) {
        if (isNull(duration) || duration.getDelay() <= 0 || duration.getTimeUnit() == null)
            return new Duration(1, TimeUnit.MINUTES);

        return duration;
    }
}
