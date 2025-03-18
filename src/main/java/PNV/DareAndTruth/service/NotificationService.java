package PNV.DareAndTruth.service;

import java.util.UUID;

import PNV.DareAndTruth.dto.response.notification.ReminderNotificationResponse;
import PNV.DareAndTruth.dto.response.notification.UnreadNotificationCountResponse;
import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.entity.User;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.response.notification.NotificationResponse;
import PNV.DareAndTruth.entity.Notification;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.NotificationMapper;
import PNV.DareAndTruth.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    SimpMessagingTemplate messagingTemplate;


    public Page<NotificationResponse> getNotificationsForUser(String receiverId, Pageable pageable) {
        Page<Notification> notifications =
                notificationRepository.findByReceiverId(UUID.fromString(receiverId), pageable);

        return notifications.map(notification -> {
            NotificationResponse dto = notificationMapper.toNotificationResponse(notification);

            // Xác định related entity
            if (notification.getPost() != null) {
                dto.setRelatedEntity(notificationMapper.mapPostToPostDTO(notification.getPost()));
            } else if (notification.getChallenge() != null) {
                dto.setRelatedEntity(notificationMapper.mapChallengeToDTO(notification.getChallenge()));
            } else if (notification.getReminder() != null) {
                dto.setRelatedEntity(notificationMapper.mapReminderToDTO(notification.getReminder()));
            } else if (notification.getRequest() != null) {
                dto.setRelatedEntity(notificationMapper.mapRequestToDTO(notification.getRequest()));
            }

            return dto;
        });
    }

    // Đếm số lượng thông báo chưa đọc
    public UnreadNotificationCountResponse countUnreadNotifications(UUID userId) {
        long unreadNotificationCount = notificationRepository.countByReceiverIdAndIsReadFalse(userId);
        return UnreadNotificationCountResponse.builder().totalUnreadNotificationCount(unreadNotificationCount).build();
    }

    // Đánh dấu thông báo là đã đọc
    @Transactional
    public void markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository
                .findById(UUID.fromString(notificationId))
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST));
        notification.setIsRead(true);
    }

    @Transactional
    public void sendNotification(Reminder reminder, User receiver) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type("REMINDER")
                .content(reminder.getReminderContent())
                .reminder(reminder)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        log.info("✅ Notification đã lưu vào DB với ID: {}", savedNotification.getId());

        ReminderNotificationResponse notificationDTO = ReminderNotificationResponse.builder()
                .id(savedNotification.getId())
                .type(savedNotification.getType())
                .content(savedNotification.getContent())
                .reminderId(reminder.getId())
                .isRead(savedNotification.getIsRead())
                .createdAt(savedNotification.getCreatedAt())
                .build();

        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + receiver.getId(), notificationDTO);
            log.info("📨 Đã gửi thông báo cho User ID: {}", receiver.getId());
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi thông báo STOMP: {}", e.getMessage());
        }
    }
}
