package org.example.spont.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.spont.notification.entity.Notification;
import org.example.spont.notification.entity.NotificationType;
import org.example.spont.notification.repository.NotificationRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepository;
    private Object Instant;

    public void createNotification(UUID userId, String eventToken,String notificationType, String message) {

        NotificationType type ;
        if(notificationType.equalsIgnoreCase("approved"))
            type = NotificationType.APPROVED;
        else if(notificationType.equalsIgnoreCase("rejected"))
            type = NotificationType.REJECTED;
        else if (notificationType.equalsIgnoreCase("join_request")) {
            type = NotificationType.JOIN_REQUEST;
        } else
            type = NotificationType.CO_HOST;

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .eventToken(eventToken)
                .userId(userId)
                .type(type)
                .message(message)
                .isRead(false)
                .createdAt(java.time.Instant.now())
                .build();

        notificationRepository.save(notification);
    }


    public Page<Notification> getUserNotifications(UUID userId) {
        Pageable pageable = PageRequest.of(0, 10);
        return notificationRepository.findUnreadByUserId(userId,pageable);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
