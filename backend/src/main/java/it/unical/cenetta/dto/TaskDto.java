package it.unical.cenetta.dto;

import it.unical.cenetta.model.*;

public record TaskDto(Long id, String title, String note, TaskStatus status, Long version, UserDto assignee, UserDto proposedBy) {}