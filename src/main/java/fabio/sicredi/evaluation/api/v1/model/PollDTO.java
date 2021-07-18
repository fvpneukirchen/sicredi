package fabio.sicredi.evaluation.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fabio.sicredi.evaluation.domain.Duration;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PollDTO {

    private Long id;

    private String reason;

    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration duration;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResultDTO> result;
}
