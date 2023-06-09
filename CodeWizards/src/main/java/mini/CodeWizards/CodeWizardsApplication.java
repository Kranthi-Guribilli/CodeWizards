package mini.CodeWizards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories("mini.CodeWizards.repository")
@EntityScan("mini.CodeWizards.model")
public class CodeWizardsApplication {
	public static void main(String[] args) {
		SpringApplication.run(CodeWizardsApplication.class, args);
	}
}
