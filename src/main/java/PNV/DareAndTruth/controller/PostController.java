package PNV.DareAndTruth.controller;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.post.PostSummaryProjection;
import PNV.DareAndTruth.dto.request.post.CreatePostRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.post.StartDateEndDateOfPostResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;
    JwtService jwtService;

    @Operation(summary = "Create new post", description = "Create a new post by providing valid challenge details.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Create post successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Create post successfully\"}")
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
                                                            "{\"code\": 1018, \"status\": \"fail\", \"message\": \"Content is required\"}")
                                        }))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> createPost(
            @Valid @RequestBody CreatePostRequest request, HttpServletRequest httpServletRequest) {

        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);

        postService.createPost(request, userEmail);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Create post successfully")
                        .build());
    }

    @Operation(summary = "Get all posts", description = "Retrieve a list of all posts is not deleted")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Posts retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Challenges retrieved successfully\", "
                                                                + "\"data\": [ "
                                                                + "{ "
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"hashtag\": \"#runChallenge\", "
                                                                + "\"content\": \"string\", "
                                                                + "\"isActive\": true "
                                                                + "}, "
                                                                + "{ "
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"hashtag\": \"#runChallenge2\", "
                                                                + "\"content\": \"Running challenge\", "
                                                                + "\"isActive\": true "
                                                                + "} "
                                                                + "] "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping
    public ResponseEntity<AppApiResponse<Set<PostSummaryProjection>>> getAllPost() {
        Set<PostSummaryProjection> posts = postService.getPosts();
        return ResponseEntity.ok(AppApiResponse.<Set<PostSummaryProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Posts retrieved successfully")
                .data(posts)
                .build());
    }

    @Operation(summary = "Get Post by ID", description = "Retrieve post details by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Post retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Post retrieved successfully\", "
                                                                + "\"data\": {"
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"hashtag\": \"#runChallenge\", "
                                                                + "\"content\": \"string\", "
                                                                + "\"isActive\": true "
                                                                + "} "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Post not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1006,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"Post does not find\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<PostSummaryProjection>> getPostById(@PathVariable String id) {
        PostSummaryProjection post = postService.getPostById(id);
        return ResponseEntity.ok(AppApiResponse.<PostSummaryProjection>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User retrieved successfully")
                .data(post)
                .build());
    }

    @Operation(
            summary = "Get Start and End Date by Hashtag and Created Date",
            description = "Retrieve the start date and end date of a post by hashtag and created date.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Start and end date retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{"
                                                                + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Dates retrieved successfully\","
                                                                + "\"data\": {"
                                                                + "\"startDate\": \"2024-03-01\","
                                                                + "\"endDate\": \"2024-03-07\""
                                                                + "}"
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "No post found for given hashtag and date",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{"
                                                                + "\"code\": 1006,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"No post found for given hashtag and date\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("date/{hashtag}")
    public ResponseEntity<AppApiResponse<StartDateEndDateOfPostResponse>> getStartDateAndEndDateByHashtagAndCreatedAt(
            @PathVariable String hashtag, @RequestParam String createdAt) {
        StartDateEndDateOfPostResponse date =
                postService.getStartDateAndEndDateByHashtagAndCreatedAt(hashtag, createdAt);
        return ResponseEntity.ok(AppApiResponse.<StartDateEndDateOfPostResponse>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User retrieved successfully")
                .data(date)
                .build());
    }
}
