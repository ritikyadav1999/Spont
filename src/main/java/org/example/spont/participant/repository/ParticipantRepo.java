package org.example.spont.participant.repository;

import org.example.spont.participant.dto.ParticipantResponse;
import org.example.spont.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParticipantRepo extends JpaRepository<Participant, UUID> {

    @Query(value = """
    SELECT
        u.name AS name,
        p.participant_id AS participantId,
        p.user_id AS userId,
        p.role AS role,
        p.joined_at AS joinedAt
    FROM participants p
    JOIN users u ON p.user_id = u.user_id
    WHERE p.event_id = :eventId
    AND p.role NOT IN ('PENDING', 'REJECTED')
""", nativeQuery = true)
    List<ParticipantResponse> findByEventId(@Param("eventId") UUID eventId);


    @Query(value = """
    SELECT
            u.name AS name,
            p.participant_id as participantId,
            p.user_id as userId,
            p.role AS role,
            p.joined_at AS joinedAt
        FROM participants p
        JOIN users u ON p.user_id = u.user_id
        WHERE p.event_id = :eventId
        AND p.role = 'PENDING'
""", nativeQuery = true)
    List<ParticipantResponse> findWaitingParticipantsByEventId(@Param("eventId") UUID eventId);

    @Query(value = """
    SELECT COUNT(*) 
    FROM participants p
    WHERE p.event_id = :eventId 
    AND p.role = :role
""", nativeQuery = true)
    long countByEventIdAndRole(@Param("eventId") UUID eventId,
                               @Param("role") String role);


    @Query(value = """
    SELECT p.role FROM participants p
    WHERE p.event_id = :eventId 
    AND p.user_id = :userId
""", nativeQuery = true)
    String getRole(@Param("eventId") UUID eventId,
                   @Param("userId") UUID userId);

}
