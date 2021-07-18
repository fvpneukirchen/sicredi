package fabio.sicredi.evaluation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
