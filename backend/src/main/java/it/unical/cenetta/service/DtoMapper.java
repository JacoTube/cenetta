package it.unical.cenetta.service;

import java.util.List;

import org.springframework.stereotype.Component;

import it.unical.cenetta.dto.*;
import it.unical.cenetta.model.*;

@Component
public class DtoMapper {

    public UserDto toUserDto(User u) {
        if(u == null) { return null; }
        return new UserDto(u.getId(), u.getUsername(), u.getDisplayName());
    }

    public EventDetail toEventDetail(Event event, User currentUser, List<Task> tasks) {
        return new EventDetail(event.getId(), event.getTitle(), event.getDescription(),event.getEventDateTime(), 
                            event.getDeadline(),event.isClosed(), event.isOrganizer(currentUser), event.getInviteCode(), toUserDto(event.getOrganizer()),
                            event.getParticipants().stream().map(this::toUserDto).toList(), tasks.stream().map(this::toTaskDto).toList()
        );
    }

    public TaskDto toTaskDto(Task task) {
        return new TaskDto(task.getId(), task.getTitle(), task.getNote(), task.getStatus(), task.getVersion(), toUserDto(task.getAssignedUser()), toUserDto(task.getProposedBy()));
    }

    public EventSummary toEventSummary(Event event, User user) {
        return new EventSummary(event.getId(), event.getTitle(), event.getEventDateTime(), event.getDeadline(),
                                event.isClosed(), event.getOrganizer().getId().equals(user.getId()), event.getOrganizer().getUsername(), event.getParticipants().size());
    }
}
