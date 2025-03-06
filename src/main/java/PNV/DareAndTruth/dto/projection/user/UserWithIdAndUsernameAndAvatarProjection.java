package PNV.DareAndTruth.dto.projection.user;

import java.util.UUID;

public interface UserWithIdAndUsernameAndAvatarProjection {
    UUID getId();

    String getUsername();

    String getAvatarUrl();

}
