package fabio.sicredi.evaluation.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteResultDTO extends PollDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResultDTO> result;
}
