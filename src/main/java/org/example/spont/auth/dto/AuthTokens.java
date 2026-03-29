package org.example.spont.auth.dto;

import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.user.entity.User;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        User user
) {
}
