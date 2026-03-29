package org.example.spont.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.common.response.ApiResponse;
import org.example.spont.common.response.ResponseUtil;
import org.example.spont.notification.entity.Notification;
import org.example.spont.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Notification>>> getNotifications(@AuthenticationPrincipal CustomUserDetails user) {
        Page<Notification> userNotifications = notificationService.getUserNotifications(user.getUserId());
        return ResponseUtil.ok(userNotifications);
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
    }
}
