package org.example.spont.participant.dto;

import org.example.spont.participant.entity.ParticipantRole;

import java.time.Instant;
import java.util.UUID;

public record ParticipantResponse(
        String name,
        UUID participantId,
        UUID userId,
        String role,
        Instant joinedAt

) {
}
