package it.unical.cenetta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unical.cenetta.model.*;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByEventId(Long eventId);
}
