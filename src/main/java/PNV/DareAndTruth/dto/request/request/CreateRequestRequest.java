package PNV.DareAndTruth.dto.request.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRequestRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    String userId;
}
