package fabio.sicredi.evaluation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PollStatus {

    CREATED("CREATED"),
    OPEN("OPEN"),
    CLOSED("CLOSED");

    private final String status;
}
