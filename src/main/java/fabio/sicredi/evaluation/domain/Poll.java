package fabio.sicredi.evaluation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String reason;

    private String status;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration duration;

    @OneToMany(mappedBy = "poll")
    private Set<Vote> users;

}
