package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.PollMapper;
import fabio.sicredi.evaluation.api.v1.model.DurationDTO;
import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.Duration;
import fabio.sicredi.evaluation.domain.Poll;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.jms.sender.PollResultSender;
import fabio.sicredi.evaluation.repositories.PollRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    private PollResultSender pollResultSender;

    @Autowired
    public void setPollMapper(PollMapper pollMapper) {
        this.pollMapper = pollMapper;
    }

    @Autowired
    public void setPollRepository(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Autowired
    public void setPollResultSender(PollResultSender pollResultSender) {
        this.pollResultSender = pollResultSender;
    }

    @Override
    public PollDTO createPoll(final PollDTO pollDTO) {
        Poll poll = pollMapper.pollDTOtoPoll(pollDTO);
        poll.setStatus(PollStatus.CREATED.getStatus());

        Poll savePoll = pollRepository.save(poll);

        return pollMapper.pollToPollDTO(savePoll);
    }

    @Override
    public PollDTO findPoll(final Long id) throws PollNotFoundException {
        Poll returnedPoll = pollRepository.findById(id).orElseThrow(PollNotFoundException::new);
        return pollMapper.pollToPollDTO(returnedPoll);
    }


    @Override
    public int openPoll(final Long id, final DurationDTO pollDTO) {
        int affectedPolls = pollRepository.updateStatus(id, PollStatus.OPEN.getStatus());
        if (affectedPolls == 1) closeOpenedPoll(id, pollDTO);

        return affectedPolls;
    }

    private void closeOpenedPoll(final Long id, final DurationDTO pollDTO) {
        log.trace(String.format("Scheduling closure of Poll [%d]", id));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Duration checkedDuration = checkDuration(pollDTO);
        log.debug(String.format("Poll [%d] will close in %d %s", id, checkedDuration.getDelay(), checkedDuration.getTimeUnit().name()));

        Runnable task = () -> {
            log.debug(String.format("Closing Poll [%d]", id));

            int affectedPolls = pollRepository.updateStatus(id, PollStatus.CLOSED.getStatus());
            validatePollClosure(id, affectedPolls, checkedDuration);
        };

        executor.schedule(task, checkedDuration.getDelay(), checkedDuration.getTimeUnit());
        executor.shutdown();
    }

    private Duration checkDuration(final DurationDTO pollDTO) {
        if (isNull(pollDTO) || isNull(pollDTO.getDuration()) || pollDTO.getDuration().getDelay() <= 0 || pollDTO.getDuration().getTimeUnit() == null)
            return new Duration(1, TimeUnit.MINUTES);

        return pollDTO.getDuration();
    }

    private void validatePollClosure(final Long id, final int affectedPolls, final Duration duration) {
        if (affectedPolls == 1) {
            log.debug(String.format("Poll [%d] is now CLOSED", id));
            Optional<Poll> closedPoll = pollRepository.findById(id);
            if (closedPoll.isPresent()) {
                PollDTO closedPollDTO = pollMapper.pollToPollDTO(closedPoll.get());
                pollResultSender.sendMessage(closedPollDTO, duration);
            }

        } else if (affectedPolls > 1) {
            log.warn(String.format("Something went wrong when closing the Poll [%d]", id));
        } else {
            log.warn(String.format("Poll [%d] not closed", id));
        }
    }
}
