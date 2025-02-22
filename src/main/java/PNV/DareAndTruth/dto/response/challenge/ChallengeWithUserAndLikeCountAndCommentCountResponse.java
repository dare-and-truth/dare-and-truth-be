package PNV.DareAndTruth.dto.response.challenge;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChallengeWithUserAndLikeCountAndCommentCountResponse {
    UUID id;
    String hashtag;
    String content;
    String mediaUrl;
    LocalDate startDate;
    LocalDate endDate;
    LocalDateTime createdAt;
    UUID userId;
    String username;
    Long likeCount;
    Long commentCount;
    Boolean isLiked;
}
