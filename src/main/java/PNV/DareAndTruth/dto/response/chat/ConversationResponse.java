package PNV.DareAndTruth.dto.response.chat;

import java.time.Instant;
import java.util.Set;

import PNV.DareAndTruth.dto.response.user.UserInfo;
import PNV.DareAndTruth.entity.Conversation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    Set<UserInfo> participants;
    Conversation.MessagePreview lastMessage;
    int unreadMessages;
    Instant updatedAt;
}
