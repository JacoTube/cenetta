package it.unical.cenetta.config;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.EventRepository;
import it.unical.cenetta.repository.UserRepository;

@Service
public class SeedService {

    private final UserRepository uRepo;
    private final EventRepository eRepo;
    private final PasswordEncoder encoder;

    public SeedService(UserRepository uRepo, EventRepository eRepo, PasswordEncoder encoder) {
        this.uRepo = uRepo;
        this.eRepo = eRepo;
        this.encoder = encoder;
    }

    @Transactional
    public void testing() {

        User mario = new User("mario", encoder.encode("password123"), "mario mario");
        User luigi = new User("luigi", encoder.encode("password123"), "luigi mario");

        uRepo.save(mario);
        uRepo.save(luigi);

        Event carb = new Event("Carbonara e Risiko", "Siete tutti froci", LocalDateTime.now().plusDays(8), LocalDateTime.now().plusDays(7), mario);
        carb.getParticipants().add(luigi);
        eRepo.save(carb);

        for( Event e : eRepo.findByOrganizerId(mario.getId())) {
            System.out.println("Nome evento: " + e.getTitle() + ", Organizzato da: " + e.getOrganizer().getUsername());
            System.out.print("Lista partecipanti: ");
            for(User u : e.getParticipants()) {
                System.out.print(u.getUsername());
            }
            System.out.print("\n");
        }
    }
}
