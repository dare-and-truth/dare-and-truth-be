package PNV.DareAndTruth.dto.projection.request;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RequestDetailProjection {
    UUID getId();

    LocalDateTime getFollowedAt();

    Boolean getIsAccepted();

    Follower getFollower();

    interface Follower {
        UUID getId();

        String getUsername();
    }
}
