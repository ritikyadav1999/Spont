package org.example.spont.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.spont.auth.security.CustomUserDetails;
import org.example.spont.notification.entity.Notification;
import org.example.spont.notification.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<Notification> getNotifications(@AuthenticationPrincipal CustomUserDetails user) {
        return notificationService.getUserNotifications(user.getUserId());
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
    }
}