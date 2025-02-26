package PNV.DareAndTruth.dto.request.badge;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBadgeRequest {
    String title;

    String image;

    String description;

    @Min(value = 1, message = "BADGE_CRITERIA_INVALID")
    Integer badgeCriteria;

    @Min(value = 0, message = "BADGE_POINT_INVALID")
    Integer points;

    LocalDate startDay;

    LocalDate endDay;

    Boolean isActive;
}
