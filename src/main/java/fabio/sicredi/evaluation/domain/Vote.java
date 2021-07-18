package fabio.sicredi.evaluation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@Entity
@IdClass(VoteKey.class)
public class Vote {

    @Id
    private Long pollId;

    @Id
    private Long userId;

    private boolean inAccordance;

    @ManyToOne
    @JoinColumn(name = "pollId", updatable = false, insertable = false, referencedColumnName = "id")
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "userId", updatable = false, insertable = false, referencedColumnName = "id")
    private User user;

    public Vote(Long pollId, Long userId, boolean inAccordance) {
        this.pollId = pollId;
        this.userId = userId;
        this.inAccordance = inAccordance;
    }
}
