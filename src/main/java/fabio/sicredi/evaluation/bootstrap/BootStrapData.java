package fabio.sicredi.evaluation.bootstrap;

import fabio.sicredi.evaluation.domain.User;
import fabio.sicredi.evaluation.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class BootStrapData implements CommandLineRunner {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String ...args) throws Exception {

        log.info("Started app");

        User u = new User("John");

        u = userRepository.save(u);

        log.info(String.format("User %s created", u.getName()));
    }
}