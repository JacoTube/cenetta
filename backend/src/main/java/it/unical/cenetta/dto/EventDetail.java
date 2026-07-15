package it.unical.cenetta.dto;

import java.time.LocalDateTime;

public record EventDetail(String title, String description, LocalDateTime eventDateTime, LocalDateTime deadline, UserDto organizer, String inviteCode) {}