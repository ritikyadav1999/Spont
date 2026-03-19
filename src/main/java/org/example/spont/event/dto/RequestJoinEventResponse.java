package org.example.spont.event.dto;

import org.example.spont.participant.entity.ParticipantRole;

public record RequestJoinEventResponse(
        String token,
        ParticipantRole role
) {
}
