package fabio.sicredi.evaluation.bootstrap;

import fabio.sicredi.evaluation.domain.Poll;
import fabio.sicredi.evaluation.repositories.PollRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BootStrapData implements CommandLineRunner {

    private final PollRepository pollRepository;

    @Override
    public void run(String... args) throws Exception {
        Poll p = new Poll("Sell stocks");

        pollRepository.save(p);

        System.out.println("Started app");
        System.out.println("Poll count: " + pollRepository.count());
    }
}
