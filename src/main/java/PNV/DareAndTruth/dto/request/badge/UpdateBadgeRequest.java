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
    String title;

    String image;

    String description;

    @Min(1)
    Integer badgeCriteria;

    @Min(0)
    Integer points;

    LocalDate startDay;

    LocalDate endDay;

    Boolean isActive;
}
