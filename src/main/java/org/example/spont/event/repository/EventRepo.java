package org.example.spont.event.repository;

import org.example.spont.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepo extends JpaRepository<Event, UUID> {

    Optional<Event> findByInviteToken(String inviteToken);

    @Query(
            value = """
        SELECT * FROM events
        WHERE start_time > NOW()
        ORDER BY start_time ASC
    """,
            countQuery = """
        SELECT COUNT(*) FROM events
        WHERE start_time > NOW()
    """,
            nativeQuery = true
    )
    Page<Event> getUpcomingEvents(Pageable pageable);
}
