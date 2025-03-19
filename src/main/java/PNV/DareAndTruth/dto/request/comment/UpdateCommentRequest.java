package PNV.DareAndTruth.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentRequest {

    @Schema(description = "New content of the comment", example = "This is an updated comment")
    private String content;
}
