package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.api.v1.model.UserStatusDTO;
import fabio.sicredi.evaluation.api.v1.model.VoteDTO;
import fabio.sicredi.evaluation.domain.PollStatus;
import fabio.sicredi.evaluation.domain.UserStatus;
import fabio.sicredi.evaluation.exception.PollAlreadyOpenException;
import fabio.sicredi.evaluation.exception.PollNotFoundException;
import fabio.sicredi.evaluation.exception.PollNotOpenException;
import fabio.sicredi.evaluation.exception.UserNotFoundException;
import fabio.sicredi.evaluation.exception.UserUnableToVoteException;
import fabio.sicredi.evaluation.exception.VoteAlreadyRegisteredException;
import fabio.sicredi.evaluation.services.PollService;
import fabio.sicredi.evaluation.services.UserService;
import fabio.sicredi.evaluation.services.VoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.util.Objects.isNull;

@Api(produces = "This the controller responsible for the Polls")
@Controller
@Log4j2
@RequestMapping("api/v1/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private UserService userService;

    @Autowired
    private VoteService voteService;

    @PostMapping
    @ApiOperation(value = "This will create a new Poll in the database",
            notes = "You need to specify the Reason of the Poll.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created a new Poll successfully"),
            @ApiResponse(code = 400, message = "A bad request will be thrown in case there is no reason property informed")
    })
    public ResponseEntity createNewPoll(@RequestBody final PollDTO pollDTO) {

        if (StringUtils.isEmpty(pollDTO.getReason())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(pollService.createPoll(pollDTO));
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "This will set a specific Poll as able to receive votes",
            notes = "Only Polls with created status can be started/opened.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Poll is opened successfully to receive votes"),
            @ApiResponse(code = 404, message = "A Not Found will be thrown in case there is no found Poll with the given id"),
            @ApiResponse(code = 412, message = "A Precondition Failed will be thrown if the Poll is already opened for votes"),
            @ApiResponse(code = 500, message = "A Internal Server Error will be thrown if the system fails to open the Poll"),
    })
    public ResponseEntity openPoll(@PathVariable("id") final Long id, @RequestBody(required = false) final PollDTO pollDTO) {

        try {
            PollDTO returnedPoll = pollService.findPoll(id);

            if(!PollStatus.CREATED.getStatus().equals(returnedPoll.getStatus())) {
                log.error(String.format("Poll [%d] not can not be opened, current status [%s]",
                        returnedPoll.getId(), returnedPoll.getStatus()));
                throw new PollAlreadyOpenException();
            }

            int affectedPol = pollService.openPoll(id, pollDTO);

            if (affectedPol != 1) throw new Exception();

            returnedPoll.setStatus(PollStatus.OPEN.getStatus());

            return ResponseEntity.status(HttpStatus.OK).body(returnedPoll);

        } catch (PollNotFoundException e) {
            log.error(String.format("Poll [%d] not found", id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollAlreadyOpenException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/{id}/votes")
    @ApiOperation(value = "This will count the votes from a specific Poll",
            notes = "Only Polls with Open/Closed status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Poll votes were retrieved successfully"),
            @ApiResponse(code = 404, message = "A Not Found will be thrown in case there is no found Poll with the given id"),
            @ApiResponse(code = 412, message = "A Precondition Failed will be thrown if the Poll was not opened for votes"),
            @ApiResponse(code = 500, message = "A Internal Server Error will be thrown if the system fails to obtains the Poll"),
    })
    public ResponseEntity countPollVotes(@PathVariable("id") final Long id) {
        try {
            PollDTO returnedPoll = pollService.findPoll(id);

            if (PollStatus.CREATED.getStatus().equals(returnedPoll.getStatus())) throw new PollNotOpenException();

            PollDTO voteResultDTO = voteService.countVotes(returnedPoll);

            if (isNull(voteResultDTO)) throw new Exception();

            return ResponseEntity.status(HttpStatus.OK).body(voteResultDTO);

        } catch (PollNotFoundException e) {
            log.error(String.format("Poll [%d] not found", id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollNotOpenException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(path = "/{id}/votes")
    @ApiOperation(value = "This will register an vote to a specific Poll",
            notes = "Only Polls with Open status can be voted.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "New Poll vote registered successfully"),
            @ApiResponse(code = 404, message = "A Not Found will be thrown in case there is no found Poll or User with the given ids"),
            @ApiResponse(code = 412, message = "A Precondition Failed will be thrown if the Poll was not opened for votes, or there is an existent vote, or the User is unable to vote"),
            @ApiResponse(code = 500, message = "A Internal Server Error will be thrown if the system fails to register the Poll"),
    })
    public ResponseEntity registerVote(@PathVariable("id") final Long id, @RequestBody final VoteDTO voteDTO) {
        try {
            PollDTO returnedPollDTO = pollService.findPoll(id);

            if (!PollStatus.OPEN.getStatus().equals(returnedPollDTO.getStatus())) throw new PollNotOpenException();

            UserDTO returnedUserDTO = userService.findUser(voteDTO.getUserId());

            UserStatusDTO userStatusDTO = userService.ableToVote(returnedUserDTO.getCpf());

            if (isNull(userStatusDTO) || UserStatus.UNABLE.getStatus().equals(userStatusDTO.getStatus())) {
                log.warn(String.format("User [%d] vote status is [%s]", returnedUserDTO.getId(), userStatusDTO.getStatus()));
                throw new UserUnableToVoteException();
            }

            boolean hasVoted = voteService.hasVoted(id, voteDTO);

            if (hasVoted) throw new VoteAlreadyRegisteredException();

            VoteDTO registeredVote = voteService.registerVote(id, voteDTO);

            if (isNull(registeredVote)) throw new Exception();

            log.debug(String.format("User ID [%d] has Voted [%s] for Poll Id [%d]", returnedUserDTO.getId(), registeredVote.isInAccordance() ? "YES" : "NO", registeredVote.getPollId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredVote);

        } catch (PollNotFoundException e) {
            log.error(String.format("Poll [%d] not found", id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PollNotOpenException | VoteAlreadyRegisteredException | UserUnableToVoteException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
