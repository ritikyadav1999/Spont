package org.example.spont.notification.repository;

import org.example.spont.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, UUID> {

    @Query(value = """
    SELECT * from notifications
    where user_id = :userId
    AND is_read = false
    order by created_at DESC
""",
            countQuery = """
    SELECT count(*) from notifications
    where user_id = :userId
    AND is_read = false
""",
            nativeQuery = true)
    Page<Notification> findUnreadByUserId(UUID userId, Pageable pageable);

}
