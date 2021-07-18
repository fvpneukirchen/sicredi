package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.exception.PollNotOpenException;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.exception.VoteAlreadyRegisteredException;
import fabio.sicredi.evaluation.services.PollService;
import fabio.sicredi.evaluation.services.UserService;
import fabio.sicredi.evaluation.services.VoteService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/votes")
@Log4j2
public class VoteController {

    @Autowired
    private PollService pollService;

    @Autowired
    private UserService userService;

    @Autowired
    private VoteService voteService;

    @PostMapping()
    public ResponseEntity registerVote(@RequestBody final VoteDTO voteDTO) {
        try {
            PollDTO returnedPollDTO = pollService.findPoll(voteDTO.getPollId());

            if(!returnedPollDTO.getStatus().equals(PollStatus.OPEN.getStatus())) throw new PollNotOpenException();

            UserDTO returnedUserDTO = userService.findUser(voteDTO.getUserId());

            boolean hasVoted = voteService.hasVoted(voteDTO);

            if (hasVoted) throw new VoteAlreadyRegisteredException();

            VoteDTO registeredVote = voteService.registerVote(voteDTO);

            log.debug(String.format("User ID [%d] has Voted [%s] for Poll Id [%d]", returnedUserDTO.getId(), registeredVote.isInAccordance() ? "YES" : "NO", registeredVote.getPollId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredVote);

        } catch (PollNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollNotOpenException | VoteAlreadyRegisteredException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
