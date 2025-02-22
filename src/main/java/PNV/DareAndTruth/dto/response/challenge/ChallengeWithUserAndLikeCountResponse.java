package PNV.DareAndTruth.dto.response.challenge;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChallengeWithUserAndLikeCountResponse {
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
    Boolean isLiked;
}
