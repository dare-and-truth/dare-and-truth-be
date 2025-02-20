package PNV.DareAndTruth.dto.request.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;


@Getter
@Setter
public class LikeRequest {
    @NotNull(message = "FEED_ID_REQUIRED")
    @UUID(message = "FEED_ID_INVALID")
    private String feedId;

    @NotNull(message = "IS_CHALLENGE_REQUIRED")
    @JsonProperty("isChallenge")
    private boolean isChallenge;
}
