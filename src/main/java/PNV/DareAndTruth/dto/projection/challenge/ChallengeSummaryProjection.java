package PNV.DareAndTruth.dto.projection.challenge;

import java.time.LocalDate;
import java.util.UUID;

public interface ChallengeSummaryProjection {
    UUID getId();

    String getHashtag();

    String getContent();

    String getMediaUrl();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Boolean getIsActive();

    interface User {
        UUID getId();

        String getUsername();
    }
}
