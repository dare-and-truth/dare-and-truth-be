package PNV.DareAndTruth.dto.request.badge;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

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

    @NotBlank(message = "BADGE_CRITERIA_REQUIRED")
    int badgeCriteria;

    @NotBlank(message = "BADGE_POINT_REQUIRED")
    int points;

    @NotBlank(message = "BADGE_START_DAY_REQUIRED")
    LocalDate startDay;

    LocalDate endDay;
}
