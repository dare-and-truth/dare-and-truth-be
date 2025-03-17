package PNV.DareAndTruth.dto.projection.comment;

import PNV.DareAndTruth.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentSummaryProjection {
    UUID getId();

    String getContent();

    String getMediaUrl();

    LocalDateTime getCreatedAt();

    User getUser();

    Comment getParentComment();

    interface User {
        String getUsername();

        UUID getId();

        String getAvatarUrl();
    }
    int getLevel();

    interface ParentComment{
        UUID getId();
    }
}
