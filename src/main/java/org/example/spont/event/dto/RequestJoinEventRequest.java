package org.example.spont.event.dto;

import org.example.spont.user.entity.Gender;

public record RequestJoinEventRequest(
        String phone,
        String name,
        Gender gender
) {
}
