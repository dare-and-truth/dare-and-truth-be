package PNV.DareAndTruth.dto.response.request;

import PNV.DareAndTruth.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestResponse {
    UUID id;
    LocalDateTime followedAt;
    Boolean isAccepted;
    LocalDateTime acceptedAt;
    UserResponse user;
    UserResponse follower;
}
