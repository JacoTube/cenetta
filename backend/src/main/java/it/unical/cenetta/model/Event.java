package it.unical.cenetta.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "eventi")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @ManyToMany()
    @JoinTable(name = "evento_partecipanti", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> participants = new HashSet<>();
    
    protected Event() {}

    public Event(String title, String description, LocalDateTime eventDateTime, LocalDateTime deadline, User organizer) {
        this.title = title;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.deadline = deadline;
        this.organizer = organizer;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() {return description; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public LocalDateTime getDeadline() { return deadline; }
    public User getOrganizer() { return organizer; }
    public Set<User> getParticipants() { return participants; }

}
