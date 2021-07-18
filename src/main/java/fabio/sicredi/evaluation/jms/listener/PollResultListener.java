package fabio.sicredi.evaluation.jms.listener;

import fabio.sicredi.evaluation.config.JmsConfig;
import fabio.sicredi.evaluation.jms.model.PollResultMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
@Log4j2
public class PollResultListener {

    @JmsListener(destination = JmsConfig.QUEUE)
    public void listen(@Payload PollResultMessage pollResultMessage,
                       @Headers MessageHeaders headers, Message message) {

        log.debug("Poll closure received");
        log.debug(pollResultMessage);
    }
}
