package org.example.spont.feedback.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="Contact&Feedback")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Feedback {

    @Id
    private UUID id;

    @Column(name = "name",nullable = true)
    private String name;

    @Column(name = "email",nullable = true)
    private String email;

    @Column(name = "phone",nullable = true)
    private String phone;

    @Column(name = "message",columnDefinition = "TEXT",nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type",nullable = false)
    private InquiryType inquiryType;

    @Column(name = "created_at",nullable = false)
    @CreationTimestamp
    private Instant createdAt;





}
