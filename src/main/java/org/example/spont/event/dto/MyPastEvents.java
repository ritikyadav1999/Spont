package org.example.spont.event.dto;

import java.time.Instant;
import java.util.UUID;

public record MyPastEvents(
        String title,
        String inviteToken,
        String location,
        Instant startTime
) {
}
