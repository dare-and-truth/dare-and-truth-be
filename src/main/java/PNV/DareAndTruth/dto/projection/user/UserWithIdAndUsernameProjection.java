package PNV.DareAndTruth.dto.projection.user;

import java.util.UUID;

public interface UserWithIdAndUsernameProjection {
    UUID getId();

    String getUsername();
}
