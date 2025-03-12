package PNV.DareAndTruth.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserWithTypeOfRequest {
    String typeOfRequest;
    String requestId;
}
