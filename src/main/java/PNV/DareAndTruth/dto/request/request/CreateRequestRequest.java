package PNV.DareAndTruth.dto.request.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRequestRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    String userId;

    Boolean isAccepted;
    LocalDateTime followedAt;
}
