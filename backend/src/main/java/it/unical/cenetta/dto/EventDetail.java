package it.unical.cenetta.dto;

import java.time.LocalDateTime;

public record EventDetail(Long id, String title, String description, LocalDateTime eventDateTime, LocalDateTime deadline, UserDto organizer, String inviteCode) {}