package fabio.sicredi.evaluation.api.v1.mapper;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.Poll;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PollMapper {

    PollMapper INSTANCE = Mappers.getMapper(PollMapper.class);

    PollDTO pollToPollDTO(final Poll poll);

    Poll pollDTOtoPoll(final PollDTO pollDTO);
}
