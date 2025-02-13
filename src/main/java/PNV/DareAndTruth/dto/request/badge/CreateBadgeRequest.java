package PNV.DareAndTruth.dto.request.badge;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBadgeRequest {
    @NotBlank(message = "BADGE_TITLE_REQUIRED")
    String title;

    @NotBlank(message = "BADGE_IMAGE_REQUIRED")
    String image;

    @NotBlank(message = "BADGE_DECS_REQUIRED")
    String description;

    @NotNull(message = "BADGE_CRITERIA_REQUIRED")
    @Min(value = 1, message = "BADGE_CRITERIA_INVALID")
    int badgeCriteria;

    @NotNull(message = "BADGE_POINT_REQUIRED")
    @Min(value = 0, message = "BADGE_POINT_INVALID")
    int points;

    @NotNull(message = "BADGE_START_DAY_REQUIRED")
    LocalDate startDay;

    LocalDate endDay;

    @NotNull(message = "BADGE_IS_ACTIVE_REQUIRED")
    Boolean isActive;
}
