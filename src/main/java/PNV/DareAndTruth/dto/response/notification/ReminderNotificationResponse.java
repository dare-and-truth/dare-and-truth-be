package PNV.DareAndTruth.dto.response.notification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderNotificationResponse {
    UUID id;
    String type;
    String content;
    UUID reminderId;
    Boolean isRead;
    LocalDateTime createdAt;
}
