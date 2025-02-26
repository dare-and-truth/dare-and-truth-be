package PNV.DareAndTruth.dto.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFeedResponse {
    UUID id;
    String type;
    String hashtag;
    String content;
    String mediaUrl;
    String startDate;
    String endDate;
    LocalDateTime createdAt;
    UUID userId;
    String username;
    long likeCount;
    long commentCount;
    boolean isLiked;
}