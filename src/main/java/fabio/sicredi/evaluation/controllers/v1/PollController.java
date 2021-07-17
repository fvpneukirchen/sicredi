package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.PollDTO;
import fabio.sicredi.evaluation.services.PollService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @PostMapping()
    public ResponseEntity createNewPoll(@RequestBody PollDTO pollDTO){

        if (StringUtils.isEmpty(pollDTO.getReason()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required field: Reason");

        return ResponseEntity.status(HttpStatus.CREATED).body(pollService.createPoll(pollDTO));
    }
}
