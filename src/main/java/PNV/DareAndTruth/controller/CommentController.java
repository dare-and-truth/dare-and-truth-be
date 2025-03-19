package PNV.DareAndTruth.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.request.comment.CreateCommentRequest;
import PNV.DareAndTruth.dto.request.comment.UpdateCommentRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.comment.CommentSummaryResponse;
import PNV.DareAndTruth.service.CommentService;
import PNV.DareAndTruth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;
    JwtService jwtService;

    @Operation(summary = "Comment feed", description = "Comment a challenge or post by their id")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Comment successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Comment successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1042, \"status\": \"fail\", \"message\": \"Challenge or post not found\"}")
                                        }))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> commentFeed(
            @Valid @RequestBody CreateCommentRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String email = jwtService.extractEmail(token);

        commentService.createComment(request, email);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Comment successfully")
                        .build());
    }

    @Operation(summary = "Get comments of a feed", description = "Retrieve a list comments of a feed by their id")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment retrieved successfully",
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
													"message": "Comments retrieved successfully",
													"data": [
														{
														"id": "6d76e0be-e529-48aa-b4a5-6ca3b43e7717",
														"content": "Good challenge for me :)",
														"mediaUrl": null,
														"createdAt": "2025-02-22T15:30:55.700304",
														"user": {
															"id": "2d76e0be-e529-48aa-b4a5-6ca3b43e7717",
															"username": "Admin"
														}
														}
													]
													}
													"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/feed/{feedId}")
    public ResponseEntity<AppApiResponse<List<CommentSummaryResponse>>> getCommentsByFeedId(
            @PathVariable String feedId, @RequestParam String feedUserId) {
        List<CommentSummaryResponse> comments = commentService.getCommentsByFeedId(feedId, feedUserId);
        return ResponseEntity.ok(AppApiResponse.<List<CommentSummaryResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Comments retrieved successfully")
                .data(comments)
                .build());
    }

    @Operation(
            summary = "Get replies of a comment",
            description = "Retrieve a list of replies to a specific comment by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Replies retrieved successfully",
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
											"message": "Comment reply retrieved successfully",
											"data": [
												{
												"id": "7d76e0be-e529-48aa-b4a5-6ca3b43e7717",
												"content": "I agree with you!",
												"mediaUrl": null,
												"createdAt": "2025-02-22T16:00:00.000Z",
												"user": {
													"id": "3d76e0be-e529-48aa-b4a5-6ca3b43e7717",
													"username": "User123"
												}
												}
											]
											}
											"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<AppApiResponse<List<CommentSummaryResponse>>> getReplies(@PathVariable String commentId) {
        List<CommentSummaryResponse> replies = commentService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(AppApiResponse.<List<CommentSummaryResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Comment reply retrieved successfully")
                .data(replies)
                .build());
    }

    @Operation(summary = "Update a comment", description = "Update an existing comment by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Comment updated successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "403",
                        description = "Unauthorized to update this comment",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @PutMapping("/{commentId}")
    public ResponseEntity<AppApiResponse<Void>> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String email = jwtService.extractEmail(token);

        commentService.updateComment(commentId, request.getContent(), email);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Comment updated successfully")
                .build());
    }

    @Operation(summary = "Delete a comment", description = "Delete a comment by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "403",
                        description = "Unauthorized to delete this comment",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<AppApiResponse<Void>> deleteComment(
            @PathVariable String commentId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String email = jwtService.extractEmail(token);

        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Comment deleted successfully")
                .build());
    }

    @Operation(summary = "Get comment by ID", description = "Retrieve a comment by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment retrieved successfully",
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
												"message": "Comment retrieved successfully",
												"data": {
													"id": "6d76e0be-e529-48aa-b4a5-6ca3b43e7717",
													"content": "Nice post!",
													"mediaUrl": "https://example.com/image.png",
													"createdAt": "2025-02-22T15:30:55.700304",
													"user": {
														"id": "2d76e0be-e529-48aa-b4a5-6ca3b43e7717",
														"username": "Admin",
														"avatarUrl": "https://example.com/avatar.png"
													},
													"parentCommentId": null,
													"replyCount": 5,
													"level": 1
												}
											}
											"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid comment ID format",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/{commentId}")
    public ResponseEntity<AppApiResponse<CommentSummaryResponse>> getCommentById(@PathVariable String commentId) {
        CommentSummaryResponse comment = commentService.getCommentById(commentId);

        return ResponseEntity.ok(AppApiResponse.<CommentSummaryResponse>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Comment retrieved successfully")
                .data(comment)
                .build());
    }
}
