package PNV.DareAndTruth.dto.projection.challenge;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ChallengeSummaryProjection {
    UUID getId();

    UUID getUserId();

    String getHashtag();

    String getContent();

    String getMediaUrl();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Boolean getIsActive();

    LocalDateTime getCreatedAt();

    interface User {
        UUID getId();

        String getUsername();
    }
}
