package mini.CodeWizards.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="challenges")
public class Challenges extends BaseEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "challenge_id")
    private int challengeId;
    private String name;
    private String description;

    @ElementCollection
    private List<String> inputs;

    @ElementCollection
    private List<String> expectedOutputs;

    /*@OneToMany(mappedBy = "challenge", fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    private Set<Submission> submissions = new HashSet<>();*/
}