package it.unical.cenetta.dto;

import java.time.LocalDateTime;

import it.unical.cenetta.model.User;

public record CreateEventRequest(String title, String description, LocalDateTime eventDateTime, LocalDateTime deadline, User organizer, String eventPassword) {}