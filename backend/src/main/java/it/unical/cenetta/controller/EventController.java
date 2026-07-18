package it.unical.cenetta.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unical.cenetta.dto.CreateEventRequest;
import it.unical.cenetta.dto.EventDetail;
import it.unical.cenetta.dto.EventSummary;
import it.unical.cenetta.dto.JoinEventRequest;
import it.unical.cenetta.model.User;
import it.unical.cenetta.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eService;
    
    public EventController(EventService eService) {
        this.eService = eService;
    }

    @GetMapping
    public List<EventSummary> myEvents(@AuthenticationPrincipal User user) {
        return eService.listForUser(user);
    }

    @PostMapping
    public EventDetail createEvent(@RequestBody CreateEventRequest request, @AuthenticationPrincipal User user) {
        return eService.create(request, user);
    }

    @PostMapping("/join")
    public EventDetail joinEvent(@RequestBody JoinEventRequest request, @AuthenticationPrincipal User user) {
        return eService.join(request, user);
    }

    @GetMapping("/{id}")
    public EventDetail detail(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return eService.detail(id, user);
    }
}

