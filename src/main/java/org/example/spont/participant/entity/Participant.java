package org.example.spont.participant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spont.event.entity.Event;
import org.example.spont.user.entity.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "participants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_participant_event", columnList = "event_id"),
                @Index(name = "idx_participant_user", columnList = "user_id")
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "participant_id", nullable = false, updatable = false)
    private UUID participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Enumerated(EnumType.STRING)
    private ParticipantRole role;
}