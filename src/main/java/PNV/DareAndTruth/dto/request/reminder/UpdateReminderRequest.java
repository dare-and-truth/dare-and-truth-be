package PNV.DareAndTruth.dto.request.reminder;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateReminderRequest {
    String title;
    String hashtag;
    LocalDate startDate;
    LocalDate endDate;
    String reminderContent;

    @Schema(type = "string", format = "time", example = "08:00:00", pattern = "HH:mm:ss")
    LocalTime reminderTime;

    @Schema(type = "string", format = "time", example = "09:00:00", pattern = "HH:mm:ss")
    LocalTime startTime;

    @Schema(type = "string", format = "time", example = "10:00:00", pattern = "HH:mm:ss")
    LocalTime endTime;
}
