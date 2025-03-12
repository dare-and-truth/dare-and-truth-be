package PNV.DareAndTruth.dto.response.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendRequestNotificationResponse {
    String type;
    UUID senderId;
    String senderName;
    String senderAvatarUrl;
    UUID requestId;
    LocalDateTime createdAt;
}
