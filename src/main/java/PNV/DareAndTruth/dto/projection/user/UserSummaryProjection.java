package PNV.DareAndTruth.dto.projection.user;

import java.util.UUID;

public interface UserSummaryProjection {
    UUID getId();

    String getUsername();

    String getEmail();

    String getIsActive();
}
