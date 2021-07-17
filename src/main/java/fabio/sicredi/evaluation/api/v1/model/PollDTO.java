package fabio.sicredi.evaluation.api.v1.model;

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
}
