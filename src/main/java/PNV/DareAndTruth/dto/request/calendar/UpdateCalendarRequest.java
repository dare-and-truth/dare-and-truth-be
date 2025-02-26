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
public class UpdateCalendarRequest {
    String title;
    LocalDate startDate;
    LocalDate endDate;

    @Schema(type = "string", format = "time", example = "09:00:00", pattern = "HH:mm:ss")
    LocalTime startTime;

    @Schema(type = "string", format = "time", example = "10:00:00", pattern = "HH:mm:ss")
    LocalTime endTime;

    @Schema(example = "1")
    Integer repeatType; // 1-Does not repeat, 2-Daily, 3-Weekly, 4-Monthly

    @Schema(example = "false")
    Boolean isChallenge = false;
}
