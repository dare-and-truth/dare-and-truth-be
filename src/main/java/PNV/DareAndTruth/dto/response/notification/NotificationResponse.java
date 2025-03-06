package PNV.DareAndTruth.dto.response.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    UUID id;
    String type;
    String content;
    UserDTO sender;
    Boolean isRead;
    LocalDateTime createdAt;
    Object relatedEntity; // cab be Post, Challenge, friend request, or Reminder

    @Getter
    @Setter
    public static class UserDTO {
        UUID id;
        String username;
    }
    @Getter
    @Setter
    public static class PostDTO {
        UUID id;
        String hashtag;
    }
    @Getter
    @Setter
    public static class ChallengeDTO {
        UUID id;
        String hashtag;
    }
    @Getter
    @Setter
    public static class ReminderDTO {
        UUID id;
        String title;
        String content;
    }

    @Getter
    @Setter
    public static class RequestDTO {
        UUID id;
    }
}
