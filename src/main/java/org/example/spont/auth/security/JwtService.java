package org.example.spont.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-minutes:15}")
    private int accessTokenMinutes;

    @PostConstruct
    public void validateSecret() {
        int length = secret != null ? secret.length() : 0;
        String hash = sha256Hex(secret);
        log.info("JWT config: secretHash={} secretLength={} accessTokenMinutes={}", hash, length, accessTokenMinutes);
        if (length < 32) {
            log.warn("JWT secret length is less than 32 characters; HS256 requires >= 256-bit key");
        }
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * accessTokenMinutes))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private String sha256Hex(String value) {
        if (value == null) {
            return "null";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "sha256-unavailable";
        }
    }
}
