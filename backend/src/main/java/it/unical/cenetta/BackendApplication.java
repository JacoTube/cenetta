package it.unical.cenetta;

import it.unical.cenetta.config.SeedService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.unical.cenetta.repository.EventRepository;
import it.unical.cenetta.repository.UserRepository;
import jakarta.transaction.Transactional;

@SpringBootApplication
public class BackendApplication {

	private final SeedService seedService;

    BackendApplication(SeedService seedService) {
        this.seedService = seedService;
    }

    public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner seed(UserRepository uRepo, EventRepository eRepo, PasswordEncoder encoder) {
		return args -> seedService.testing();
	}
}