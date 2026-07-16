package it.unical.cenetta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unical.cenetta.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long id);
    Optional<Event> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);
}
