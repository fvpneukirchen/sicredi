package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollAlreadyOpenException;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.exception.PollNotOpenException;
import fabio.sicredi.evaluation.services.PollService;
import fabio.sicredi.evaluation.services.VoteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("api/v1/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private VoteService voteService;


    @PostMapping()
    public ResponseEntity createNewPoll(@RequestBody final PollDTO pollDTO) {

        if (StringUtils.isEmpty(pollDTO.getReason())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(pollService.createPoll(pollDTO));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity openPoll(@PathVariable("id") final Long id, @RequestBody(required = false) final PollDTO pollDTO) {

        try {
            PollDTO returnedPoll = pollService.findPoll(id);

            if (isNull(returnedPoll) || !PollStatus.CREATED.getStatus().equals(returnedPoll.getStatus()))
                throw new PollAlreadyOpenException();

            int affectedPol = pollService.openPoll(id, pollDTO);

            if (affectedPol != 1) throw new Exception();

            returnedPoll.setStatus(PollStatus.OPEN.getStatus());

            return ResponseEntity.status(HttpStatus.OK).body(returnedPoll);

        } catch (PollNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollAlreadyOpenException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/{id}/votes")
    public ResponseEntity countPollVotes(@PathVariable("id") final Long id) {
        try {
            PollDTO returnedPoll = pollService.findPoll(id);

            if (PollStatus.CREATED.getStatus().equals(returnedPoll.getStatus())) throw new PollNotOpenException();

            PollDTO voteResultDTO = voteService.countVotes(returnedPoll);

            if (isNull(voteResultDTO)) throw new Exception();

            return ResponseEntity.status(HttpStatus.OK).body(voteResultDTO);

        } catch (PollNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollNotOpenException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
