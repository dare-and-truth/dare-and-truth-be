package PNV.DareAndTruth.dto.request.badge;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBadgeRequest {
    @NotBlank
    String title;

    @NotBlank
    String image;

    @NotBlank
    String description;

    @Min(1)
    int badgeCriteria;

    @Min(0)
    int points;

    @NotNull
    LocalDate startDay;

    LocalDate endDay;

    @NotNull
    Boolean isActive;
}
