package PNV.DareAndTruth.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentRequest {

    @Schema(description = "New content of the comment", example = "This is an updated comment")
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
