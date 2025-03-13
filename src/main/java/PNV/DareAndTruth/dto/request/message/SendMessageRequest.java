package PNV.DareAndTruth.dto.request.message;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    ObjectId conversationId;

    @NotNull(message = "REQUIRED_RECEIVER_ID")
    private UUID receiverId;

    private String content;

    private String mediaUrl;
}
