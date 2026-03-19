package org.example.spont.event.dto;

import org.example.spont.event.entity.JoinMode;
import org.example.spont.event.entity.Visibility;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateEventRequest(
        String title,
        String description,
        String locationName,
        BigDecimal latitude,
        BigDecimal longitude,
        Instant startTime,
        Instant endTime,
        Integer maxParticipants,
        Visibility visibility,
        JoinMode joinMode
) {
}
