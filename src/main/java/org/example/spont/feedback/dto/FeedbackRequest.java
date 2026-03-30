package org.example.spont.feedback.dto;

import jakarta.validation.constraints.*;
import org.example.spont.feedback.entity.InquiryType;

public record FeedbackRequest(
        @Size(max = 100)
        String name,

        @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
        String phone,

        @Email(message = "Invalid email format")
        String email,

        @NotBlank
        @Size(max = 5000)
        String message,

        @NotNull
        InquiryType inquiryType
) {
}
