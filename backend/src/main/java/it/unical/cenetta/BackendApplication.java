package it.unical.cenetta;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.UserRepository;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner seed(UserRepository repo) {
		return args -> {
			repo.save(new User("mario", "password123", "Mario Mario"));
			repo.save(new User("luigi", "password123", "Luigi Mario"));
			System.out.println(">>> Utenti nel DB: " + repo.count());
			System.out.println(">>> Cerco Mario: " + repo.findByUsername("mario"));
		};
	}
}