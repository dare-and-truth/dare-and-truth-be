package PNV.DareAndTruth.dto.response.chat;

import PNV.DareAndTruth.dto.response.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatResponse {
    private String conversationId;
    private UserInfo otherUser;
    private List<MessageResponse> messages;
    private String lastMessageId;
}
