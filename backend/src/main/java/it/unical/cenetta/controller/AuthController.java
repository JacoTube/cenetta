package it.unical.cenetta.controller;

import org.springframework.web.bind.annotation.RestController;

import it.unical.cenetta.dto.RegisterRequest;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.UserRepository;
import it.unical.cenetta.dto.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth") 
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterRequest request) {
        User user = new User(request.username(), encoder.encode(request.password()), request.displayName());
        repo.save(user);
        return new UserDto(user.getId(), user.getUsername(), user.getDisplayName());
    }
}