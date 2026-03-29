package org.example.spont.event.repository;

import org.example.spont.event.dto.MyPastEvents;
import org.example.spont.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    @Query(value = """
    SELECT e.* FROM events e
    JOIN participants p ON p.event_id = e.event_id
    WHERE p.user_id = :userId
    AND p.role in ('HOST','CO_HOST') 
    AND e.start_time > NOW()
    ORDER BY e.start_time ASC
""",
            countQuery = """
    SELECT COUNT(*) FROM events e
    JOIN participants p ON p.event_id = e.event_id
    WHERE p.user_id = :userId
    AND e.start_time > NOW()
""",
            nativeQuery = true)
    Page<Event> getHostingEvents(@Param("userId") UUID userId, Pageable pageable);


    @Query(value = """
    SELECT e.* FROM events e
    JOIN participants p ON p.event_id = e.event_id
    WHERE p.user_id = :userId
    AND p.role = 'MEMBER'
    AND e.start_time > NOW()
    ORDER BY e.start_time ASC
""",
            countQuery = """
    SELECT COUNT(*) FROM events e
    JOIN participants p ON p.event_id = e.event_id
    WHERE p.user_id = :userId
    AND e.start_time > NOW()
""",
            nativeQuery = true)
    Page<Event> getAttendingEvents(@Param("userId") UUID userId, Pageable pageable);


    @Query(value = """
    SELECT 
        e.title,
        e.invite_token,
        e.location_name,
        e.start_time
    FROM events e
    WHERE (
        e.creator_id = :userId 
        OR EXISTS (
            SELECT 1 FROM participants p
            WHERE p.event_id = e.event_id
              AND p.user_id = :userId
        )
    )
    AND e.start_time < NOW()
    ORDER BY e.start_time DESC
""",
            countQuery = """
    SELECT COUNT(*)
    FROM events e
    WHERE (
        e.creator_id = :userId 
        OR EXISTS (
            SELECT 1 FROM participants p
            WHERE p.event_id = e.event_id
              AND p.user_id = :userId
        )
    )
    AND e.start_time < NOW()
""",
            nativeQuery = true)
    Page<MyPastEvents> getPastEvents(@Param("userId") UUID userId, Pageable pageable);


}
