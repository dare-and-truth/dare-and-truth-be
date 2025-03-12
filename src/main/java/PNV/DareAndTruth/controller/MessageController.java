package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.request.message.SendMessageRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessageService messageService;
    JwtService jwtService;

    @Operation(
            summary = "Send a message",
            description = "Send a message between two users. If no conversation exists, a new one is created."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Message sent successfully\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": 1017, \"status\": \"fail\", \"message\": \"Content cannot be empty\"}"
                            )
                    )
            )
    })
    @PostMapping("/send")
    public ResponseEntity<AppApiResponse<Void>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpServletRequest) {
        // Lấy token từ header
        UUID senderId = jwtService.extractUserIdFromHeader(httpServletRequest);

        messageService.sendMessage(senderId, request);

        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Message sent successfully")
                        .build());
    }

    @Operation(
            summary = "Mark messages as read",
            description = "Marks all unread messages in a conversation as read by the given user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Messages marked as read successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Messages marked as read successfully\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conversation not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": 1018, \"status\": \"fail\", \"message\": \"Conversation not found\"}"
                            )
                    )
            )
    })
    @PutMapping("/{conversationId}/mark-read")
    public ResponseEntity<AppApiResponse<Void>> markMessagesAsRead(
            @PathVariable String conversationId,
            HttpServletRequest httpServletRequest) {

        // Lấy userId từ token trong header
        UUID userId = jwtService.extractUserIdFromHeader(httpServletRequest);

        messageService.markMessagesAsRead(new ObjectId(conversationId), userId);

        return ResponseEntity.ok(
                AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Messages marked as read successfully")
                        .build()
        );
    }

}
