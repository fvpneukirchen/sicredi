package fabio.sicredi.evaluation.controllers.v1;

import fabio.sicredi.evaluation.api.v1.model.UserDTO;
import fabio.sicredi.evaluation.exception.InvalidCPFFormatException;
import fabio.sicredi.evaluation.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Objects.isNull;

@Api(produces = "This the controller responsible for the Users")
@Controller
@Log4j2
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation(value = "This will create a new User in the database",
            notes = "You need to specify the CPF of the User and must be unique.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created a new Poll successfully"),
            @ApiResponse(code = 400, message = "A bad request will be thrown in case there is no cpf property informed"),
            @ApiResponse(code = 412, message = "A pre condition failed will be thrown if the cpf is not unique"),
            @ApiResponse(code = 500, message = "An internal server error will be thrown in case the save process fails")
    })
    public ResponseEntity addUser(@RequestBody final UserDTO userDTO) {

        if (isNull(userDTO) || isNull(userDTO.getCpf())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userDTO));
        } catch (InvalidCPFFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
