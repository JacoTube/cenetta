package it.unical.cenetta.controller;

import org.springframework.web.bind.annotation.RestController;

import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.UserRepository;
import it.unical.cenetta.security.JwtService;
import it.unical.cenetta.dto.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth") 
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository repo, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwtService ) {
        this.repo = repo;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterRequest request) {
        User user = new User(request.username(), encoder.encode(request.password()), request.displayName());
        repo.save(user);
        return new UserDto(user.getId(), user.getUsername(), user.getDisplayName());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = repo.findByUsername(request.username()).orElseThrow();

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(token, new UserDto(user.getId(), user.getUsername(), user.getDisplayName()));
    }
}