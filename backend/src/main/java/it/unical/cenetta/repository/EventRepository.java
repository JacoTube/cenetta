package it.unical.cenetta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long id);
    Optional<Event> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN e.participants p " + "WHERE e.organizer = :user OR p = :user ORDER BY e.eventDateTime")
    List<Event> findAllForUser(@Param("user") User user);
}
