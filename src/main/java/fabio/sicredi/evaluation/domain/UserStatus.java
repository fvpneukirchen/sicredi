package fabio.sicredi.evaluation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    ABLE("ABLE_TO_VOTE"),
    UNABLE("UNABLE_TO_VOTE");

    private final String status;
}
