package it.unical.cenetta.service;

import org.springframework.stereotype.Component;

import it.unical.cenetta.dto.*;
import it.unical.cenetta.model.*;

@Component
public class DtoMapper {

    public UserDto toUserDto(User u) {
        if(u == null) { return null; }
        return new UserDto(u.getId(), u.getUsername(), u.getDisplayName());
    }

    public EventDetail toEventDetail(Event e) {
        return new EventDetail(e.getId(), e.getTitle(), e.getDescription(), e.getEventDateTime(), e.getDeadline(), toUserDto(e.getOrganizer()), e.getInviteCode());
    }

    public TaskDto toTaskDto(Task task) {
        return new TaskDto(task.getId(), task.getTitle(), task.getNote(), task.getStatus(), toUserDto(task.getAssignedUser()), toUserDto(task.getProposedBy()));
    }

    public EventSummary toEventSummary(Event event, User user) {
        return new EventSummary(event.getId(), event.getTitle(), event.getEventDateTime(), event.getDeadline(),
                                event.isClosed(), event.getOrganizer().getId().equals(user.getId()), event.getOrganizer().getUsername(), event.getParticipants().size());
    }
}
