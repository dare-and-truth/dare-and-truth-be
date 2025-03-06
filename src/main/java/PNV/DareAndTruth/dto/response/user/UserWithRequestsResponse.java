package PNV.DareAndTruth.dto.response.user;

import java.util.List;

import PNV.DareAndTruth.dto.projection.request.FriendDetailProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdAndUsernameAndAvatarProjection;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserWithRequestsResponse {
    UserWithIdAndUsernameAndAvatarProjection user;
    List<FriendDetailProjection> requests;
}
