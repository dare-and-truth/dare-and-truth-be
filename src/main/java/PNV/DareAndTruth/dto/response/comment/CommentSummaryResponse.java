package PNV.DareAndTruth.dto.response.comment;

import PNV.DareAndTruth.dto.response.user.UserInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentSummaryResponse {
    UUID id;
    String content;
    String mediaUrl;
    LocalDateTime createdAt;
    UserInfo user;
    String parentCommentId;
    Integer numberOfReplies;
    int level;
}
