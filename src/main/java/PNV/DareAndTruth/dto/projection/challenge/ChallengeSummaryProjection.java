package PNV.DareAndTruth.dto.projection.challenge;

import java.time.LocalDate;
import java.util.Date;
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

    Date getCreatedAt();

    interface User {
        UUID getId();

        String getUsername();
    }
}
