package PNV.DareAndTruth.dto.response.feed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface FeedResponse {
    UUID getId();

    String getType();

    String getHashtag();

    String getContent();

    String getMediaUrl();

    LocalDate getStartDate();

    LocalDate getEndDate();

    LocalDateTime getCreatedAt();

    UUID getUserId();

    String getUsername();

    String getAvatarUrl();

    int getLikeCount();

    int getCommentCount();

    boolean getLiked();

    boolean getJoined();

}
