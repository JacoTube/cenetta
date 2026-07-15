package it.unical.cenetta.service;

import org.springframework.stereotype.Component;

import it.unical.cenetta.dto.*;
import it.unical.cenetta.model.*;

@Component
public class DtoMapper {

    public UserDto toUserDto(User u) {
        return new UserDto(u.getId(), u.getUsername(), u.getDisplayName());
    }

    public EventDetail toEventDetail(Event e) {
        return new EventDetail(e.getTitle(), e.getDescription(), e.getEventDateTime(), e.getDeadline(), toUserDto(e.getOrganizer()), e.getInviteCode());
    }

}
