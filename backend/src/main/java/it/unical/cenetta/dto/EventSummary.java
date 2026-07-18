package it.unical.cenetta.dto;

import java.time.LocalDateTime;

public record EventSummary (Long id, String title, LocalDateTime eventDateTime, LocalDateTime deadline, boolean closed, boolean organizer, String organizerName, int participantCount) {}