package org.example.spont.feedback.controller;

import lombok.RequiredArgsConstructor;
import org.example.spont.common.response.ApiResponse;
import org.example.spont.common.response.ResponseUtil;
import org.example.spont.feedback.dto.FeedbackRequest;
import org.example.spont.feedback.entity.Feedback;
import org.example.spont.feedback.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Object>> submitFeedback(@RequestBody FeedbackRequest request) {
        feedbackService.submit(request);
        String message = "Feedback submitted";
        return ResponseUtil.ok(null, message);

    }



}
