package it.unical.cenetta.config;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.dto.CreateEventRequest;
import it.unical.cenetta.dto.CreateTaskRequest;
import it.unical.cenetta.dto.EventDetail;
import it.unical.cenetta.dto.JoinEventRequest;
import it.unical.cenetta.dto.TaskDto;
import it.unical.cenetta.model.*;
import it.unical.cenetta.repository.*;
import it.unical.cenetta.service.EventService;
import it.unical.cenetta.service.TaskService;

@Service
public class SeedService {
    private final UserRepository uRepo;
    private final EventRepository eRepo;
    private final TaskRepository tRepo;
    private final PasswordEncoder encoder;
    private final EventService service;
    private final TaskService tService;

    public SeedService(UserRepository uRepo, EventRepository eRepo, TaskRepository tRepo, PasswordEncoder encoder, EventService service, TaskService tService) {
        this.uRepo = uRepo;
        this.eRepo = eRepo;
        this.encoder = encoder;
        this.tRepo = tRepo;
        this.service = service;
        this.tService = tService;
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
        
        CreateTaskRequest ctr1 = new CreateTaskRequest("funghi", "li porta quel ricchione di sinutaro");
        CreateTaskRequest ctr2 = new CreateTaskRequest("marijuana", "ringraziamo nostro signore gesù cristo");
        Event evento = eRepo.findByInviteCode(details.inviteCode()).orElseThrow(() -> new IllegalArgumentException("l'evento non esiste"));
        
        TaskDto ctr1_dto = tService.create(ctr1, evento.getId(), mario);
        TaskDto ctr2_dto = tService.create(ctr2, evento.getId(), luigi);

        tService.claim(ctr1_dto.id(), luigi);
        tService.approve(ctr2_dto.id(), mario);

        tService.claim(ctr2_dto.id(), luigi);

        tService.complete(ctr1_dto.id(), luigi);


        System.out.println(">>> codice invito: " + details.inviteCode());
        
        for(Task t : evento.getTasks()) {
            System.out.println("Titolo: " + t.getTitle() + 
                                ", STATUS: " + t.getStatus().toString() + 
                                ", Assegnato a: " + t.getAssignedUser().getUsername()
                            );
        }
    }
}