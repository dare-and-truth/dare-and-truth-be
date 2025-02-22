package PNV.DareAndTruth.controller;

import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.dto.request.challenge.CreateChallengeRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.challenge.ChallengeWithUserAndLikeCountResponse;
import PNV.DareAndTruth.service.ChallengeService;
import PNV.DareAndTruth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChallengeController {
    ChallengeService challengeService;
    JwtService jwtService;

    @Operation(
            summary = "Create new challenge",
            description = "Create a new challenge by providing valid challenge details.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Create challenge successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Create challenge successfully\"}")
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
                                                            "{\"code\": 1017, \"status\": \"fail\", \"message\": \"Hashtag is required\"}")
                                        }))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> createChallenge(
            @Valid @RequestBody CreateChallengeRequest request, HttpServletRequest httpServletRequest) {

        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);

        challengeService.createChallenge(request, userEmail);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Create challenge successfully")
                        .build());
    }

    @Operation(summary = "Get all challenges", description = "Retrieve a list of all challenges is not deleted")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenges retrieved successfully",
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
                                                                + "\"isActive\": true, "
                                                                + "\"startDate\": \"2025-02-14\", "
                                                                + "\"endDate\": \"2025-02-15\" "
                                                                + "}, "
                                                                + "{ "
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"hashtag\": \"#runChallenge2\", "
                                                                + "\"content\": \"Running challenge\", "
                                                                + "\"isActive\": true, "
                                                                + "\"startDate\": \"2025-02-14\", "
                                                                + "\"endDate\": \"2025-02-15\" "
                                                                + "} "
                                                                + "] "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping
    public ResponseEntity<AppApiResponse<Set<ChallengeSummaryProjection>>> getAllChallenges() {
        Set<ChallengeSummaryProjection> challenges = challengeService.getChallenges();
        return ResponseEntity.ok(AppApiResponse.<Set<ChallengeSummaryProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Challenge retrieved successfully")
                .data(challenges)
                .build());
    }

    @Operation(summary = "Get challenge by ID", description = "Retrieve challenge details by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenge retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Challenge retrieved successfully\", "
                                                                + "\"data\": {"
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"hashtag\": \"#runChallenge\", "
                                                                + "\"content\": \"string\", "
                                                                + "\"startDate\": \"2025-02-14\", "
                                                                + "\"endDate\": \"2025-02-15\", "
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
                                                                + "\"message\": \"Challenge does not find\""
                                                                + "}")))
            })
    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<ChallengeSummaryProjection>> getChallengeById(@PathVariable String id) {
        ChallengeSummaryProjection challenge = challengeService.getChallengeById(id);
        return ResponseEntity.ok(AppApiResponse.<ChallengeSummaryProjection>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User retrieved successfully")
                .data(challenge)
                .build());
    }

    @Operation(
            summary = "Get all challenges for feed",
            description = "Retrieve a list of all challenges for feed to render on the homepage")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenges retrieved successfully",
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
													"message": "Challenge retrieved successfully",
													"data": [
														{
														"id": "b8762fa3-6e4c-4bec-a190-f061b373da12",
														"hashtag": "eye",
														"content": "Welcome to eye challenge",
														"mediaUrl": "https://ldzbpqvspnjrhgfgigev.supabase.co/storage/v1/object/public/uploads/271c0b3d-fbc9-475e-ba5b-21806611fd03.jpg",
														"startDate": "2025-02-20",
														"endDate": "2025-02-23",
														"userId": "2d76e0be-e529-48aa-b4a5-6ca3b43e7717",
														"username": "string",
														"likeCount": 1
														}
													]
													}
													"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/with-like-count")
    public ResponseEntity<AppApiResponse<List<ChallengeWithUserAndLikeCountResponse>>> getAllChallengesWithLikeCount(
            HttpServletRequest httpServletRequest) {

        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);

        List<ChallengeWithUserAndLikeCountResponse> challenges = challengeService.getChallengesWithLikeCount(userEmail);

        return ResponseEntity.ok(AppApiResponse.<List<ChallengeWithUserAndLikeCountResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Challenge retrieved successfully")
                .data(challenges)
                .build());
    }
}
