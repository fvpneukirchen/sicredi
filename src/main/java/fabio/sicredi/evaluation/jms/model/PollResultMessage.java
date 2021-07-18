package fabio.sicredi.evaluation.jms.model;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PollResultMessage implements Serializable {

    static final long serialVersionUID = 6582934048423061777L;

    private UUID id;
    private PollDTO voteResultDTO;
}
