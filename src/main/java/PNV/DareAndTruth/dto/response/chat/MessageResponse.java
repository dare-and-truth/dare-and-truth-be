package PNV.DareAndTruth.dto.response.chat;

import java.time.Instant;
import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    String id;
    String content;
    String mediaUrl;
    UUID senderId;
    Instant sentAt;
}
