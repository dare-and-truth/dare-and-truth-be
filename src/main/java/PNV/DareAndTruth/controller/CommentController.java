package PNV.DareAndTruth.controller;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.dto.request.comment.CreateCommentRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
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
    public ResponseEntity<AppApiResponse<Set<CommentSummaryProjection>>> getCommentsByFeedId(
            @PathVariable String feedId) {
        Set<CommentSummaryProjection> comments = commentService.getCommentsByFeedId(feedId);
        return ResponseEntity.ok(AppApiResponse.<Set<CommentSummaryProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Comments retrieved successfully")
                .data(comments)
                .build());
    }
}
