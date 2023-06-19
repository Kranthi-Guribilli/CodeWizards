package mini.CodeWizards.repository;


import mini.CodeWizards.model.Challenges;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengesRepository extends JpaRepository<Challenges, Long> {
}
