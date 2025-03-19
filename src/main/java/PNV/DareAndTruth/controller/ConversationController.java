package PNV.DareAndTruth.controller;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.chat.ChatResponse;
import PNV.DareAndTruth.dto.response.chat.ConversationResponse;
import PNV.DareAndTruth.dto.response.chat.UnreadMessagesOfAllChatCountResponse;
import PNV.DareAndTruth.service.ConversationService;
import PNV.DareAndTruth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/conversations")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    ConversationService conversationService;
    JwtService jwtService;

    @Operation(
            summary = "Get user's conversations",
            description = "Returns a list of conversations for the authenticated user, ordered by last updated time.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the list of conversations",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
											[
											{
												"id": "bf0cbdff-b2df-4f79-9add-36808334788c",
												"participants": [
												{
													"id": "9cb773c0-7bf2-446f-89fa-32035cadf803",
													"username": "Nguyễn Lâm Nhật",
													"avatarUrl": null
												},
												{
													"id": "0dcdbd04-ae76-45d0-89ac-7c92c5b5e74e",
													"username": "Nguyễn Nhật Phi",
													"avatarUrl": null
												}
												],
												"lastMessage": {
												"content": "hello",
												"senderId": "9cb773c0-7bf2-446f-89fa-32035cadf803"
												},
												"unreadMessages": 1,
												"updatedAt": "2025-03-09T17:14:41.098Z"
											}
											]
											"""))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User is not authenticated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1001, \"status\": \"fail\", \"message\": \"Unauthorized\"}")))
            })
    @GetMapping
    public ResponseEntity<AppApiResponse<List<ConversationResponse>>> getConversations(
            HttpServletRequest httpServletRequest) {
        UUID userId = jwtService.extractUserIdFromHeader(httpServletRequest);
        List<ConversationResponse> conversations = conversationService.getConversations(userId);
        return ResponseEntity.status(200)
                .body(AppApiResponse.<List<ConversationResponse>>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .data(conversations)
                        .message("Conversations retrieved successfully")
                        .build());
    }

    @Operation(
            summary = "Get chat messages between two users",
            description =
                    "Returns messages of a conversation between the authenticated user and another user, ordered by newest first.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved chat messages",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
										{
										"code": 1000,
										"status": "success",
										"message": "Chat retrieved successfully",
										"data": {
											"conversationId": "bf0cbdff-b2df-4f79-9add-36808334788c",
											"otherUser": {
											"id": "0dcdbd04-ae76-45d0-89ac-7c92c5b5e74e",
											"username": "Nguyễn Nhật Phi",
											"avatarUrl": null
											},
											"messages": [
											{
												"id": "65ee2f97f3a2c61f143cbb48",
												"content": "Hello!",
												"senderId": "9cb773c0-7bf2-446f-89fa-32035cadf803",
												"createdAt": "2025-03-09T17:14:41.098Z"
											},
											{
												"id": "65ee2fa7f3a2c61f143cbb49",
												"content": "Hi there!",
												"senderId": "0dcdbd04-ae76-45d0-89ac-7c92c5b5e74e",
												"createdAt": "2025-03-09T17:15:00.120Z"
											}
											],
											"lastMessageId": "65ee2fa7f3a2c61f143cbb49"
										}
										}
										"""))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User is not authenticated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
										{
										"code": 1001,
										"status": "fail",
										"message": "Unauthorized"
										}
										"""))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request parameters",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
										{
										"code": 1002,
										"status": "fail",
										"message": "Invalid parameters"
										}
										""")))
            })
    @GetMapping("/chat")
    public ResponseEntity<AppApiResponse<ChatResponse>> getChat(
            HttpServletRequest request,
            @RequestParam String otherUserId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String conversationId,
            @RequestParam(required = false) String nextMessageId) {
        UUID userId = jwtService.extractUserIdFromHeader(request);

        // Convert String nextMessageId to ObjectId if it's not null
        ObjectId objectIdLastMessage = nextMessageId != null ? new ObjectId(nextMessageId) : null;

        ChatResponse chatResponse = conversationService.getChatBetweenUsers(
                userId, otherUserId, conversationId, limit, objectIdLastMessage);

        return ResponseEntity.status(200)
                .body(AppApiResponse.<ChatResponse>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .data(chatResponse)
                        .message("Chat retrieved successfully")
                        .build());
    }

    @Operation(
            summary = "Get total unread messages",
            description =
                    "Returns the total count of unread messages across all conversations for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the unread messages count",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
												{
													"code": 1000,
													"status": "success",
													"data": { "unreadMessagesCount": 15 },
													"message": "Unread messages count retrieved successfully"
												}
												"""))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User is not authenticated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
												{
													"code": 1001,
													"status": "fail",
													"message": "Unauthorized"
												}
												""")))
            })
    @GetMapping("/unread-messages/count")
    public ResponseEntity<AppApiResponse<UnreadMessagesOfAllChatCountResponse>> getUnreadMessagesCount(
            HttpServletRequest httpServletRequest) {
        UUID userId = jwtService.extractUserIdFromHeader(httpServletRequest);
        UnreadMessagesOfAllChatCountResponse unreadCount = conversationService.getTotalUnreadMessages(userId);

        return ResponseEntity.ok(AppApiResponse.<UnreadMessagesOfAllChatCountResponse>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .data(unreadCount)
                .message("Unread messages count retrieved successfully")
                .build());
    }

    @Operation(
            summary = "Mark conversation as read",
            description = "Marks all unread messages in a conversation as read by the given user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Conversation marked as read successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1000, \"status\": \"success\", \"message\": \"Conversation marked as read successfully\"}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Conversation not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1018, \"status\": \"fail\", \"message\": \"Conversation not found\"}")))
            })
    @PutMapping("/{conversationId}/mark-read")
    public ResponseEntity<AppApiResponse<Void>> markConversationAsRead(
            @PathVariable String conversationId, HttpServletRequest httpServletRequest) {

        // Lấy userId từ token trong header
        UUID userId = jwtService.extractUserIdFromHeader(httpServletRequest);

        conversationService.markConversationAsRead(new ObjectId(conversationId), userId);

        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Conversation marked as read successfully")
                .build());
    }
}
