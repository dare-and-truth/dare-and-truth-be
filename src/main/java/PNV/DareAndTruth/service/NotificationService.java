package PNV.DareAndTruth.service;

import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.response.notification.NotificationResponse;
import PNV.DareAndTruth.dto.response.notification.UnreadNotificationCountResponse;
import PNV.DareAndTruth.entity.Notification;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.NotificationMapper;
import PNV.DareAndTruth.repository.NotificationRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;

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
            } else if (notification.getComment() != null) {
                dto.setRelatedEntity(notificationMapper.mapCommentToCommentDTO(notification.getComment()));
            }

            return dto;
        });
    }

    // Đếm số lượng thông báo chưa đọc
    public UnreadNotificationCountResponse countUnreadNotifications(UUID userId) {
        long unreadNotificationCount = notificationRepository.countByReceiverIdAndIsReadFalse(userId);
        return UnreadNotificationCountResponse.builder()
                .totalUnreadNotificationCount(unreadNotificationCount)
                .build();
    }

    // Đánh dấu thông báo là đã đọc
    @Transactional
    public void markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository
                .findById(UUID.fromString(notificationId))
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST));
        notification.setIsRead(true);
    }

    public void updateFcmToken(UUID userId, String token) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (user.getFcmToken() == null) user.setFcmToken(token);
        userRepository.save(user);
    }
}
