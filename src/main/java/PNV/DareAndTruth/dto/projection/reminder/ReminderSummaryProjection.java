package PNV.DareAndTruth.dto.projection.reminder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ReminderSummaryProjection {
    UUID getId();
    String getTitle();
    String getHashtag();
    LocalDate getStartDate();
    LocalDate getEndDate();
    String getReminderContent();
    LocalTime getReminderTime();
    LocalTime getStartTime();
    LocalTime getEndTime();
    UUID getUserId();

}
