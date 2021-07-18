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

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {

        log.info("Started app");

        User u1 = new User("John", 31260008002L);
        User u2 = new User("Paul", 79228342099L);
        User u3 = new User("Jack", 44468646020L);

        u1 = userRepository.save(u1);
        u2 = userRepository.save(u2);
        u3 = userRepository.save(u3);

        log.info(String.format("User %s created", u1.getName()));
        log.info(String.format("User %s created", u2.getName()));
        log.info(String.format("User %s created", u3.getName()));
    }
}