package org.example.spont.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spont.auth.dto.LoginRequest;
import org.example.spont.auth.dto.LoginResponse;
import org.example.spont.auth.dto.RegisterRequest;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.auth.security.JwtService;
import org.example.spont.user.entity.User;
import org.example.spont.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User register(RegisterRequest request){
        return userService.register(request.name(),request.phone(),request.email(), request.gender(),request.password());
    }


    public LoginResponse login(@Valid @RequestBody  LoginRequest request) {
        System.out.println("checkpoint :Login Service 1");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        System.out.println("checkpoint :Login Service 2");

        return new LoginResponse(jwtService.generateToken(userDetails.getUsername()));
    }

}
