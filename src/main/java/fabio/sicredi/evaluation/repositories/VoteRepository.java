package fabio.sicredi.evaluation.repositories;

import fabio.sicredi.evaluation.domain.Vote;
import fabio.sicredi.evaluation.domain.VoteKey;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, VoteKey> {
}
