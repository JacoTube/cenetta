package it.unical.cenetta.dto;

public record AuthResponse(String token, UserDto user) { }