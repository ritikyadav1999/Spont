package org.example.spont.participant.dto;

import org.example.spont.participant.entity.ParticipantRole;

import java.time.Instant;

public record ParticipantResponse(
        String name,
        String role,
        Instant joinedAt

) {
}
