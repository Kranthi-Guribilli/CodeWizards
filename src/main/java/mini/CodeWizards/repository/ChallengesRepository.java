package mini.CodeWizards.repository;


import mini.CodeWizards.model.Challenges;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengesRepository extends JpaRepository<Challenges, Integer> {
}
