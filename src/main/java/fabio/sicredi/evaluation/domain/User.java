package fabio.sicredi.evaluation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
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

    @Column(unique = true)
    private Long cpf;

    private String name;

    @OneToMany(mappedBy = "user")
    private Set<Vote> polls;

    public boolean hasValidCpf() {
        String regex = "\\d{11}";
        return  String.valueOf(this.cpf).matches(regex);
    }
}
