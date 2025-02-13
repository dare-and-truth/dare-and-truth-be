package PNV.DareAndTruth.dto.projection.post;

import java.util.UUID;

public interface PostSummaryProjection {
    UUID getId();

    String getHashtag();

    String getContent();

    String getMediaUrl();

    Boolean getIsActive();

    interface User {
        UUID getId();

        String getUsername();
    }
}
