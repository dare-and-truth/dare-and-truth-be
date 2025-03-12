package PNV.DareAndTruth.dto.projection.comment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentSummaryProjection {
    UUID getId();

    String getContent();

    String getMediaUrl();

    LocalDateTime getCreatedAt();

    User getUser();

    interface User {
        String getUsername();

        UUID getId();

        String getAvatarUrl();
    }
}
