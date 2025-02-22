package PNV.DareAndTruth.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UUID;

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
