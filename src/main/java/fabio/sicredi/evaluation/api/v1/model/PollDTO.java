package fabio.sicredi.evaluation.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fabio.sicredi.evaluation.domain.Duration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollDTO {

    private Long id;
    private String reason;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration duration;

}
