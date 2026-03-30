package org.example.spont.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.spont.auth.entity.RefreshToken;
import org.example.spont.auth.repository.RefreshTokenRepo;
import org.example.spont.auth.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        return refreshToken;
    }


    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = verifyToken(token);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    public String  refresh(String refreshToken) {
        RefreshToken token = verifyToken(refreshToken);
        String newAccessToken = jwtService.generateToken(token.getUserId().toString());

        return newAccessToken;
    }

}
