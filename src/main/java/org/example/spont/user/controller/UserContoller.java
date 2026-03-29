package org.example.spont.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.spont.common.response.ApiResponse;
import org.example.spont.common.response.ResponseUtil;
import org.example.spont.user.entity.User;
import org.example.spont.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserContoller {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable UUID userId) {
        User userById = userService.findUserById(userId).orElseThrow();
         return ResponseUtil.ok(userById);
    }






}
