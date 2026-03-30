package org.example.spont.feedback.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.spont.feedback.dto.FeedbackRequest;
import org.example.spont.feedback.entity.Feedback;
import org.example.spont.feedback.repository.FeedbackRepo;
import org.example.spont.notification.entity.Notification;
import org.example.spont.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final NotificationService notificationService;

    @Transactional
    public void submit(FeedbackRequest request) {
        Feedback feedback = Feedback.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .message(request.message())
                .inquiryType(request.inquiryType())
                .build();

        feedbackRepo.save(feedback);

    }

}
