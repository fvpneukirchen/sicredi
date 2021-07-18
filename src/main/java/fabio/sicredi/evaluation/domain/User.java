package fabio.sicredi.evaluation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long cpf;

    private String name;

    @OneToMany(mappedBy = "user")
    private Set<Vote> polls;

    public User(final String name, final Long cpf) {
        this.name = name;
        this.cpf = cpf;
    }
}
