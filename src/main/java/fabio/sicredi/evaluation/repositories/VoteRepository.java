package fabio.sicredi.evaluation.repositories;

import fabio.sicredi.evaluation.domain.Vote;
import fabio.sicredi.evaluation.domain.VoteKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends CrudRepository<Vote, VoteKey> {

    @Query("select inAccordance, count(*) from Vote v where v.pollId = :id group by inAccordance")
    List<Object[]> countVotes(@Param(value = "id") final Long id);
}
