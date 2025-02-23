package PNV.DareAndTruth.dto.projection.request;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FriendDetailProjection {
    UUID getId();

    LocalDateTime getFollowedAt();

    Boolean getIsAccepted();

    LocalDateTime getAcceptedAt();

    Follower getFollower();

    User getUser();

    interface Follower {
        UUID getId();

        String getUsername();
    }

    interface User {
        UUID getId();

        String getUsername();
    }
}
