package PNV.DareAndTruth.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LoginRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    String email;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
}
