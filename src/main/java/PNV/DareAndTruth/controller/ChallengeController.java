package PNV.DareAndTruth.controller;

import java.util.Set;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.dto.request.challenge.CreateChallengeRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChallengeController {
    ChallengeService challengeService;

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
    public ResponseEntity<AppApiResponse<Void>> createChallenge(@Valid @RequestBody CreateChallengeRequest request) {
        challengeService.createChallenge(request);
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
    public ResponseEntity<AppApiResponse<Set<ChallengeSummaryProjection>>> getAllPost() {
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
}
