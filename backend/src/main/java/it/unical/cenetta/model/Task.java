package it.unical.cenetta.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 300)
    private String note;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @ManyToOne()
    private Event event;

    @ManyToOne()
    private User proposedBy;

    @ManyToOne()
    private User assignedUser;

    protected Task() {}

    public Task(String title, String note, TaskStatus status, Event event, User proposedBy) {
        this.title = title;
        this.note = note;
        this.status = status;
        this.event = event;
        this.proposedBy = proposedBy;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getNote() { return note; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public User getProposedBy() { return proposedBy; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus statusToSet) { this.status = statusToSet; }
    public User getAssignedUser() { return assignedUser; }
    public void assignUser(User user) { this.assignedUser = user; }
    public void deAssignUser() {this.assignedUser = null;}

}


