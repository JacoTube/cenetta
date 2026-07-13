package it.unical.cenetta.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

import it.unical.cenetta.dto.UserDto;
import it.unical.cenetta.repository.*;


@RestController
public class UserController {

    private final UserRepository repo;
    
    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/users")
    public List<UserDto> usersList() {
        return repo.findAll().stream().map(u -> new UserDto(u.getId(), u.getUsername(), u.getDisplayName())).toList();
    }
}