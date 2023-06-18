package mini.CodeWizards.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Person person;

    private String code;
    private String result;
}
