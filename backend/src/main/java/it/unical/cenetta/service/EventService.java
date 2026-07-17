package it.unical.cenetta.service;

import java.security.SecureRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.dto.CreateEventRequest;
import it.unical.cenetta.dto.EventDetail;
import it.unical.cenetta.dto.JoinEventRequest;
import it.unical.cenetta.exception.*;
import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.EventRepository;

@Service
@Transactional
public class EventService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    private final EventRepository eRepo;
    private final PasswordEncoder encoder;
    private final DtoMapper dtoMapper;

    public EventService(EventRepository eRepo, PasswordEncoder encoder, DtoMapper dtoMapper) {
        this.eRepo = eRepo;
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
        return dtoMapper.toEventDetail(event);
    }

    public EventDetail join(JoinEventRequest join, User user) {
        Event event = eRepo.findByInviteCode(join.inviteCode()).orElseThrow(() -> new NotFoundException("Codice invito non valido"));
        event.isOpen();
        if(!encoder.matches(join.eventPassword(), event.getPasswordHash())) {
            throw new ForbiddenException("Password errata");
        }
        event.getParticipants().add(user);
        eRepo.save(event);
        return dtoMapper.toEventDetail(event);
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
}
