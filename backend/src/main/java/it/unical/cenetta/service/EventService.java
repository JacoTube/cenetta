package it.unical.cenetta.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.dto.CreateEventRequest;
import it.unical.cenetta.dto.EventDetail;
import it.unical.cenetta.dto.EventSummary;
import it.unical.cenetta.dto.JoinEventRequest;
import it.unical.cenetta.exception.*;
import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.EventRepository;
import it.unical.cenetta.repository.TaskRepository;

@Service
@Transactional
public class EventService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    private final EventRepository eRepo;
    private final TaskRepository tRepo;
    private final PasswordEncoder encoder;
    private final DtoMapper dtoMapper;

    public EventService(EventRepository eRepo, TaskRepository tRepo, PasswordEncoder encoder, DtoMapper dtoMapper) {
        this.eRepo = eRepo;
        this.tRepo = tRepo;
        this.encoder = encoder;
        this.dtoMapper = dtoMapper;
    }

    public EventDetail create(CreateEventRequest request, User organizer) {

        if(request.deadline().isAfter(request.eventDateTime())) {
            throw new IllegalArgumentException("La deadline non può essere dopo l'evento");
        }

        String code = generateUniqueCode();
        String passwordHash = encoder.encode(request.eventPassword());


        Event event = new Event(request.title(), request.description(), request.eventDateTime(), request.deadline(), organizer, code, passwordHash);
        eRepo.save(event);
        return dtoMapper.toEventDetail(event, organizer, List.of());
    }

    public EventDetail join(JoinEventRequest join, User user) {
        Event event = eRepo.findByInviteCode(join.inviteCode()).orElseThrow(() -> new NotFoundException("Codice invito non valido"));
        event.stillOpen();
        if(!encoder.matches(join.eventPassword(), event.getPasswordHash())) {
            throw new ForbiddenException("Password errata");
        }
        event.getParticipants().add(user);
        eRepo.save(event);
        return dtoMapper.toEventDetail(event, user, tRepo.findByEventId(event.getId()));
    }

    private String generateUniqueCode() {
        String code;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            code = sb.toString();
        } while (eRepo.existsByInviteCode(code));

        return code;
    }

    @Transactional(readOnly = true)
    public List<EventSummary> listForUser(User user) {
        return eRepo.findAllForUser(user).stream().map(e -> dtoMapper.toEventSummary(e, user)).toList();
    }

    @Transactional(readOnly = true)
    public EventDetail detail(Long eventId, User user) {
        Event event = eRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Evento non trovato"));
        
        if (!event.isMember(user)) throw new ForbiddenException("Non fai parte di questo evento");

        return dtoMapper.toEventDetail(event, user, tRepo.findByEventId(eventId));
    }
}
