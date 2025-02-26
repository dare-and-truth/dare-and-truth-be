package PNV.DareAndTruth.dto.projection.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface CalendarSummaryProjection {
    UUID getId();
    String getTitle();
    LocalDate getStartDate();
    LocalDate getEndDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    int getRepeatType();
    Boolean getIsChallenge();
    UUID getUserId();

}
