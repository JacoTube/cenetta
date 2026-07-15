package it.unical.cenetta.config;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.dto.CreateEventRequest;
import it.unical.cenetta.dto.EventDetail;
import it.unical.cenetta.dto.JoinEventRequest;
import it.unical.cenetta.model.*;
import it.unical.cenetta.repository.*;
import it.unical.cenetta.service.EventService;

@Service
public class SeedService {

    private final UserRepository uRepo;
    private final EventRepository eRepo;
    private final TaskRepository tRepo;
    private final PasswordEncoder encoder;
    private final EventService service;

    public SeedService(UserRepository uRepo, EventRepository eRepo, TaskRepository tRepo, PasswordEncoder encoder, EventService service) {
        this.uRepo = uRepo;
        this.eRepo = eRepo;
        this.encoder = encoder;
        this.tRepo = tRepo;
        this.service = service;
    }

    @Transactional
    public void testing() {

        User mario = new User("mario", encoder.encode("password123"), "mario mario");
        User luigi = new User("luigi", encoder.encode("password123"), "luigi mario");

        uRepo.save(mario);
        uRepo.save(luigi);

        CreateEventRequest req = new CreateEventRequest(
            "Carbonara e Risiko",
            "Siete tutti froci",
            LocalDateTime.now().plusDays(8),
            LocalDateTime.now().plusDays(7),
            mario,
            "sesso"
        );

        EventDetail details = service.create(req, mario);
        service.join(new JoinEventRequest(details.inviteCode(), "sesso"), luigi);

        System.out.println(">>> codice invito: " + details.inviteCode());
    }
}