package PNV.DareAndTruth.dto.request.like;

import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

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
