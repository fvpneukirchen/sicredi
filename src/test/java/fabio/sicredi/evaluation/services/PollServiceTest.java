package fabio.sicredi.evaluation.services;

import fabio.sicredi.evaluation.api.v1.mapper.PollMapper;
import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.Duration;
import fabio.sicredi.evaluation.domain.Poll;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.repositories.PollRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PollServiceTest {

    public static final Long ID = 1L;
    public static final String REASON = "Sell stocks";

    PollServiceImpl pollService;

    PollMapper pollMapper = PollMapper.INSTANCE;

    @Mock
    PollRepository pollRepository;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        pollService = new PollServiceImpl();
        pollService.setPollMapper(pollMapper);
        pollService.setPollRepository(pollRepository);
    }

    @Test
    public void createPoll() {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setReason(REASON);

        Poll savedPoll = new Poll();
        savedPoll.setId(ID);
        savedPoll.setReason(pollDTO.getReason());
        savedPoll.setStatus(PollStatus.CREATED.getStatus());

        when(pollRepository.save(any(Poll.class))).thenReturn(savedPoll);

        //when
        PollDTO saveDTO = pollService.createPoll(pollDTO);

        //then
        Assertions.assertEquals(pollDTO.getReason(), saveDTO.getReason());
    }

    @Test
    public void fetchPoll() {
        //given
        Poll savedPoll = new Poll();
        savedPoll.setId(ID);
        savedPoll.setReason(REASON);
        savedPoll.setStatus(PollStatus.CREATED.getStatus());

        when(pollRepository.findById(anyLong())).thenReturn(java.util.Optional.of(savedPoll));

        //when
        PollDTO saveDTO = pollService.findPoll(ID);

        //then
        Assertions.assertEquals(ID, saveDTO.getId());
        Assertions.assertEquals(REASON, saveDTO.getReason());
        Assertions.assertEquals(PollStatus.CREATED.getStatus(), saveDTO.getStatus());
    }

    @Test
    public void failsToFetchPoll() {
        //given
        when(pollRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        //when
        PollNotFoundException thrown = Assertions.assertThrows(PollNotFoundException.class, () -> pollService.findPoll(ID));

        //then
        Assertions.assertNotNull(thrown);
    }

    @Test
    public void opensPollWithOutDuration() {
        //given
        when(pollRepository.updateStatus(anyLong(), anyString())).thenReturn(1);

        //when
        int affected = pollService.openPoll(ID, null);

        //then
        Assertions.assertEquals(1, affected);
    }

    @Test
    public void opensPollWithDuration() {
        //given
        PollDTO pollDTO = new PollDTO();
        pollDTO.setDuration(new Duration(5, TimeUnit.SECONDS));

        when(pollRepository.updateStatus(anyLong(), anyString())).thenReturn(1);

        //when
        int affected = pollService.openPoll(ID, pollDTO);

        //then
        Assertions.assertEquals(1, affected);
    }
}
