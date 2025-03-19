package PNV.DareAndTruth.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import PNV.DareAndTruth.entity.Notification;
import PNV.DareAndTruth.entity.Reminder;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByReceiverId(UUID receiverId, Pageable pageable);

    // Đếm số lượng thông báo chưa đọc
    Long countByReceiverIdAndIsReadFalse(UUID receiverId);

    @Transactional
    @Modifying
    void deleteByRequestId(UUID requestId);

    boolean existsByReminderAndCreatedAtBetween(Reminder reminder, LocalDateTime start, LocalDateTime end);
}
