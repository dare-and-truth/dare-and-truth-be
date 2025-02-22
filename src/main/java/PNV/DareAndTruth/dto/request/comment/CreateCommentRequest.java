package PNV.DareAndTruth.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCommentRequest {
    @NotNull(message = "FEED_ID_REQUIRED")
    @UUID(message = "FEED_ID_INVALID")
    String feedId;

    @NotBlank(message = "CONTENT_REQUIRED")
    String content;

    String mediaUrl;

    @NotNull(message = "IS_CHALLENGE_REQUIRED")
    @JsonProperty("isChallenge")
    private boolean isChallenge;
}
