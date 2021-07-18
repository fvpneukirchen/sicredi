package fabio.sicredi.evaluation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

}
