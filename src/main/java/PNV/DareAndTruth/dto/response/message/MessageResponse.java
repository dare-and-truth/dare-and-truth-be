package PNV.DareAndTruth.dto.response.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //exclude null fields
public class MessageResponse {
    String id;
    String content;
    UUID senderId;
    String conversationId;
    Instant sentAt;
    String senderUsername;
    String senderAvatarUrl;
}
