package PNV.DareAndTruth.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    private String userId;

    @NotBlank(message = "CHALLENGE_ID_REQUIRED")
    private String challengeId;

    @NotBlank(message = "CONTENT_REQUIRED")
    private String content;

    @NotBlank(message = "MEDIA_URL_REQUIRED")
    private String mediaUrl;
}
