package PNV.DareAndTruth.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
     String access_token;
     String refresh_token;
     Object user;
}
