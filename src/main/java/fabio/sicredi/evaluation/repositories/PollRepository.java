package fabio.sicredi.evaluation.repositories;

import fabio.sicredi.evaluation.domain.Poll;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PollRepository extends CrudRepository<Poll, Long> {

    @Modifying
    @Query("update Poll p set p.status = :status where p.id = :id")
    @Transactional
    int updateStatus(@Param(value = "id") final Long id, @Param(value = "status") final String status);
}
