package PNV.DareAndTruth.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.UUID;

@Getter
@Setter
public class SendMessageRequest {
    ObjectId conversationId;

    @NotNull(message = "REQUIRED_RECEIVER_ID")
    private UUID receiverId;

    @NotBlank(message = "CONTENT_REQUIRED")
    private String content;
}
