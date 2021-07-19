package fabio.sicredi.evaluation.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteEntryDTO {

    private Long userId;

    private boolean inAccordance;
}
