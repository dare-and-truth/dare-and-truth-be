package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.response.notification.NotificationResponse;
import PNV.DareAndTruth.entity.Notification;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.NotificationMapper;
import PNV.DareAndTruth.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;

    public Page<NotificationResponse> getNotificationsForUser(String receiverId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverId(UUID.fromString(receiverId), pageable);

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
    public Long countUnreadNotifications(String receiverId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(UUID.fromString(receiverId));
    }

    // Đánh dấu thông báo là đã đọc
    @Transactional
    public void markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(UUID.fromString(notificationId))
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST));
        notification.setIsRead(true);
    }
}
