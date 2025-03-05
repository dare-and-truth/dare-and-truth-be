package PNV.DareAndTruth.dto.projection.user;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserDetailProjection {
    UUID getId();

    String getUsername();

    String getEmail();

    String getIsActive();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getAvatarUrl();
}
