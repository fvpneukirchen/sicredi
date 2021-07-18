package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollAlreadyOpenException;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.services.PollService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/v1/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @PostMapping()
    public ResponseEntity createNewPoll(@RequestBody final PollDTO pollDTO) {

        if (StringUtils.isEmpty(pollDTO.getReason())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(pollService.createPoll(pollDTO));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity openPoll(@PathVariable("id") final Long id, @RequestBody final PollDTO pollDTO) {

        try {
            PollDTO returnedPoll = pollService.findPoll(id);

            if (!returnedPoll.getStatus().equals(PollStatus.CREATED.getStatus())) throw new PollAlreadyOpenException();

            int affectedPol = pollService.openPoll(id, pollDTO.getDuration());

            if (affectedPol != 1) throw new Exception();

            return ResponseEntity.status(HttpStatus.OK).body("Poll is OPEN");

        } catch (PollNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollAlreadyOpenException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
