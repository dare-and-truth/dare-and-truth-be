package PNV.DareAndTruth.dto.request.reminder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateReminderRequest {
    String title;
    String hashtag;

    @NotNull(message = "START_DATE_REQUIRED")
    LocalDate startDate;

    @NotNull(message = "END_DATE_REQUIRED")
    LocalDate endDate;

    @NotBlank(message = "REMINDER_CONTENT_REQUIRED")
    String reminderContent;

    @Schema(type = "string", format = "time", example = "08:00:00", pattern = "HH:mm:ss")
    LocalTime reminderTime;

    @Schema(type = "string", format = "time", example = "09:00:00", pattern = "HH:mm:ss")
    LocalTime startTime;

    @Schema(type = "string", format = "time", example = "10:00:00", pattern = "HH:mm:ss")
    LocalTime endTime;
}