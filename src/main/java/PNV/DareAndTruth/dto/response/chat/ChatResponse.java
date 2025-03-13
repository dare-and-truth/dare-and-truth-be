package PNV.DareAndTruth.dto.response.chat;

import java.util.List;

import PNV.DareAndTruth.dto.response.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatResponse {
    private String conversationId;
    private UserInfo otherUser;
    private List<MessageResponse> messages;
    private String nextMessageId;
}
