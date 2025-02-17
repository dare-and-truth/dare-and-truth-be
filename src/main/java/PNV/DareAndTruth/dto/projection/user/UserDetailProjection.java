package PNV.DareAndTruth.dto.projection.user;

import java.util.Date;
import java.util.UUID;

public interface UserDetailProjection {
    UUID getId();

    String getUsername();

    String getEmail();

    String getIsActive();

    Date getCreatedAt();

    Date getUpdatedAt();
}
