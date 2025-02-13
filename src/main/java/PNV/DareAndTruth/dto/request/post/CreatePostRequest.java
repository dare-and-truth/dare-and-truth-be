package PNV.DareAndTruth.dto.request.post;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    private String userId;

    @NotBlank(message = "HASHTAG_REQUIRED")
    private String hashtag;

    @NotBlank(message = "CONTENT_REQUIRED")
    private String content;

    @NotBlank(message = "MEDIA_URL_REQUIRED")
    private String mediaUrl;
}
