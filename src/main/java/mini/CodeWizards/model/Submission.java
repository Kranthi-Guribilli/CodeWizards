package mini.CodeWizards.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.net.ssl.SSLSession;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="submissions")
public class Submission extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private int submissionId;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenges challenge;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    private String code;
    private String result;
}
