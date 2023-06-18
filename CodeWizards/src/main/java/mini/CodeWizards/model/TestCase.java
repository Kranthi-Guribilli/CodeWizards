package mini.CodeWizards.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private String input;
    private String expectedOutput;

    public void setPassed(boolean b) {
    }

    public boolean isPassed() {
        return true;
    }
}
