package PNV.DareAndTruth.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SigninResponse {
    String accessToken;
    String refreshToken;
    Object user;
}
