package org.example.spont.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email or phone must be present")
        String identifier,
        @NotBlank
        String password
) {
}
