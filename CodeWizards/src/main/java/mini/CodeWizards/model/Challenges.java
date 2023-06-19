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
public class Challenges extends BaseEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    private int challengeId;
    private String name;
    private String desc;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<TestCase> testCases = new ArrayList<>();
    @OneToMany(mappedBy = "challenge", fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    private Set<Submission> submissions = new HashSet<>();
}