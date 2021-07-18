package fabio.sicredi.evaluation.api.v1.mapper;

import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.domain.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    VoteMapper INSTANCE = Mappers.getMapper(VoteMapper.class);

    VoteDTO voteToVoteDTO(final Vote vote);

    Vote voteDTOtoVote(final VoteDTO voteDTO);
}
