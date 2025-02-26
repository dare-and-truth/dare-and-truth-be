package PNV.DareAndTruth.dto.request.calendar;

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
public class CreateCalendarRequest {

    @NotBlank(message = "BADGE_TITLE_REQUIRED")
    String title;

    @NotNull(message = "START_DATE_REQUIRED")
    LocalDate startDate;

    @NotNull(message = "END_DATE_REQUIRED")
    LocalDate endDate;

    @NotNull(message = "START_TIME_REQUIRED")
    @Schema(type = "string", format = "time", example = "09:00:00", pattern = "HH:mm:ss")
    LocalTime startTime;

    @NotNull(message = "END_TIME_REQUIRED")
    @Schema(type = "string", format = "time", example = "10:00:00", pattern = "HH:mm:ss")
    LocalTime endTime;

    @NotNull(message = "REPEAT_TYPE_REQUIRED")
    @Schema(example = "1")
    Integer repeatType; // 1-Does not repeat, 2-Daily, 3-Weekly, 4-Monthly

    @Schema(example = "false")
    Boolean isChallenge = false;
}
