package it.unical.cenetta.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @Column(nullable = false, unique = true, length = 12)
    private String inviteCode;

    @Column(nullable = false)
    private String passwordHash;
    
    protected Event() {}

    public Event(String title, String description, LocalDateTime eventDateTime, LocalDateTime deadline, User organizer, String inviteCode, String passwordHash) {
        this.title = title;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.deadline = deadline;
        this.organizer = organizer;
        this.inviteCode = inviteCode;
        this.passwordHash = passwordHash;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() {return description; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public LocalDateTime getDeadline() { return deadline; }
    public User getOrganizer() { return organizer; }
    public Set<User> getParticipants() { return participants; }
    public List<Task> getTasks() { return tasks; }
    public String getInviteCode() { return inviteCode; }
    public String getPasswordHash() { return passwordHash; }

    public void addTask(Task task) {
        tasks.add(task);
        task.setEvent(this);
    }

    public boolean isOrganizer(User user) {
        return organizer != null && organizer.getId().equals(user.getId());
    }

    public boolean isMember(User user) {
        return isOrganizer(user) || participants.stream().anyMatch(p -> p.getId().equals(user.getId()));
    }
}
