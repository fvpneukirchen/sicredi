package fabio.sicredi.evaluation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String reason;

    private String status;

    public Poll(final String reason) {
        this.reason = reason;
        this.status = PollStatus.CLOSED.getStatus();
    }
}
