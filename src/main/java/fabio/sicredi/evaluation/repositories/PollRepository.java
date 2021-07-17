package fabio.sicredi.evaluation.repositories;

import fabio.sicredi.evaluation.domain.Poll;
import org.springframework.data.repository.CrudRepository;

public interface PollRepository extends CrudRepository<Poll, Long> {
}
