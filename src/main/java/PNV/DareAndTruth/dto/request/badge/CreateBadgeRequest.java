package PNV.DareAndTruth.dto.request.badge;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBadgeRequest {
    @NotBlank(message = "BADGE_TITLE_REQUIRED")
    private String title;

    @NotBlank(message = "BADGE_IMAGE_REQUIRED")
    private String image;

    @NotBlank(message = "BADGE_DECS_REQUIRED")
    private String description;

    @NotBlank(message = "BADGE_REQUIRED_COUNT_REQUIRED")
    private int requiredCount;

    @NotBlank(message = "BADGE_POINT_REQUIRED")
    private int points;

    @NotBlank(message = "BADGE_START_DAY_REQUIRED")
    private LocalDate startDay;

    private LocalDate endDay;
}