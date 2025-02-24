package PNV.DareAndTruth.dto.projection.challenge;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ChallengeSummaryProjection {
    UUID getId();

    String getHashtag();

    String getContent();

    String getMediaUrl();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Boolean getIsActive();

    LocalDateTime getCreatedAt();

    User getUser();

    interface User {
        UUID getId();

        String getUsername();
    }
}
