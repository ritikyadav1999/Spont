package org.example.spont.feedback.repository;

import org.example.spont.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepo extends JpaRepository<Feedback, UUID> {
}
