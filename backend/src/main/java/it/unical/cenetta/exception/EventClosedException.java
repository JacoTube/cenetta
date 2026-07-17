package it.unical.cenetta.exception;

public class EventClosedException extends RuntimeException {
    public EventClosedException(String message) { super(message); }
}