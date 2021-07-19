package fabio.sicredi.evaluation.api.v1.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PollDTO {

    private Long id;

    private String reason;

    private String status;
}
