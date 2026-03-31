package org.example.spont.auth.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResponse(
        String token,
        String name,
        String phone,
        String email,
        String gender,
        UUID userId

) {
}
