package org.example.spont.event.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EventResponseDTO(
        UUID eventId,
        String inviteToken,
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        String locationName,
        BigDecimal latitude,
        BigDecimal longitude,
        String status,
        String joinMode,
        String visibility,
        int maxParticipants,
        UserSummaryDTO creator
) {}