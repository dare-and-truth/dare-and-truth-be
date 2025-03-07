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
public class LikeNotificationResponse {
    String type;
    UUID senderId;
    String senderName;
    UUID postId;
    String hashtag;
    UUID challengeId;
    LocalDateTime createdAt;
}
