package fabio.sicredi.evaluation.repositories;

import fabio.sicredi.evaluation.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
