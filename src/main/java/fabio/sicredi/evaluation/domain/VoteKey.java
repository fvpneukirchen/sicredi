package fabio.sicredi.evaluation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteKey implements Serializable {

    private Long pollId;

    private Long userId;
}
