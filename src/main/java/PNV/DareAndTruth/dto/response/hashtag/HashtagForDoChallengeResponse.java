package PNV.DareAndTruth.dto.response.hashtag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HashtagForDoChallengeResponse {
    String hashtag;
    boolean isDid;
}
