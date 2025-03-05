package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByReceiverId(UUID receiverId, Pageable pageable);

    // Đếm số lượng thông báo chưa đọc
    Long countByReceiverIdAndIsReadFalse(UUID receiverId);
}