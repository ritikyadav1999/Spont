package org.example.spont.auth.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spont.auth.dto.LoginRequest;
import org.example.spont.auth.dto.LoginResponse;
import org.example.spont.auth.dto.RegisterRequest;
import org.example.spont.auth.service.AuthService;
import org.example.spont.user.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        System.out.println("CheckPoint: Login Controller");
        return authService.login(request);
    }





}
