package mini.CodeWizards.repository;

import mini.CodeWizards.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}

