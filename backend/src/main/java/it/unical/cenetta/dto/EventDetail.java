package it.unical.cenetta.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventDetail(Long id, String title, String description, LocalDateTime eventDateTime, LocalDateTime deadline, boolean closed, boolean isOrganizer,  String inviteCode, UserDto organizer, List<UserDto> participants, List<TaskDto> tasks) {}