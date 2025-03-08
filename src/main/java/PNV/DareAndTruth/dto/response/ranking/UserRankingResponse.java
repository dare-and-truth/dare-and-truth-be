package PNV.DareAndTruth.dto.response.ranking;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRankingResponse {
    UUID userId;
    String username;
    String avatarURL;
    int totalLikes;
    int rank;
}
