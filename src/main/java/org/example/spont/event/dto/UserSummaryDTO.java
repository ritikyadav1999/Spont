package org.example.spont.event.dto;

import java.util.UUID;

public record UserSummaryDTO(
        UUID userId,
        String name
) {
}
