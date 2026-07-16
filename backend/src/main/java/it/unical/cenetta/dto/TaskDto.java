package it.unical.cenetta.dto;

import it.unical.cenetta.model.*;

public record TaskDto(Long id, String title, String note, TaskStatus status, UserDto assignee, UserDto proposedBy) {}