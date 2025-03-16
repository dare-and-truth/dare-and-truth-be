package PNV.DareAndTruth.dto.response.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UnreadMessagesOfAllChatCountResponse {
    long totalUnreadMessagesCount;
}
