package fabio.sicredi.evaluation.jms.sender;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.config.JmsConfig;
import fabio.sicredi.evaluation.jms.model.PollResultMessage;
import fabio.sicredi.evaluation.services.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@Component
public class PollResultSender {

    private final JmsTemplate jmsTemplate;
    @Autowired
    private VoteService voteService;

    public void sendMessage(final PollDTO pollDTO) {

        log.trace(String.format("Received Poll [%d] closure notification", pollDTO.getId()));

        PollDTO voteResultDTO = voteService.countVotes(pollDTO);

        log.debug(String.format("Poll [%d] closure results collected", pollDTO.getId()));

        PollResultMessage message = PollResultMessage
                .builder()
                .id(UUID.randomUUID())
                .voteResultDTO(voteResultDTO)
                .build();

        log.debug(String.format("Sending Poll [%d] closure results", pollDTO.getId()));

        jmsTemplate.convertAndSend(JmsConfig.QUEUE, message);
    }
}
