package org.example.spont.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private boolean isRead;

    private Instant createdAt;

    @Column(name = "event_token",nullable = false)
    private String eventToken;
}