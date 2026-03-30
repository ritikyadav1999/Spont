package org.example.spont.auth.contoller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spont.auth.dto.AuthTokens;
import org.example.spont.auth.dto.LoginRequest;
import org.example.spont.auth.dto.LoginResponse;
import org.example.spont.auth.dto.RegisterRequest;
import org.example.spont.auth.entity.RefreshToken;
import org.example.spont.auth.service.AuthService;
import org.example.spont.auth.service.RefreshTokenService;
import org.example.spont.common.response.ApiResponse;
import org.example.spont.common.response.ResponseUtil;
import org.example.spont.user.entity.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        AuthTokens tokens = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        LoginResponse response = new LoginResponse(
                tokens.accessToken(),
                tokens.user().getName(),
                tokens.user().getPhone(),
                tokens.user().getEmail(),
                tokens.user().getGender().toString()
        );


        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(
            @CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestHeader(value = "X-Refresh-Token", required = false) String headerToken
    ) {
        String token = cookieToken != null ? cookieToken : headerToken;
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        String refresh = refreshTokenService.refresh(token);
        return ResponseUtil.ok(refresh);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken") String token) {

        refreshTokenService.revokeToken(token);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth")
                .maxAge(0) // delete cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

}
