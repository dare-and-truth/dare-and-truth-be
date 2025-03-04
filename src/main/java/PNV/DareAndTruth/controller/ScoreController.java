package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreController {
    ScoreService scoreService;

    /**
     * Retrieves the total score (monster score) for a user based on their userId.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing the total score of the user.
     */
    @Operation(summary = "Get total score of a monster (user)", description = "Retrieve the total accumulated points for a user based on their userId")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Total score retrieved successfully",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value = "{" +
                                                    "\"code\": 1000," +
                                                    "\"status\": \"success\"," +
                                                    "\"message\": \"Total score retrieved successfully\", " +
                                                    "\"data\": {" +
                                                    "\"userId\": \"fd609f00-90ef-435f-965c-88884767fbbf\", " +
                                                    "\"totalScore\": 250" +
                                                    "}" +
                                                    "}"))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/monster/{userId}")
    public ResponseEntity<AppApiResponse<ScoreSummaryProjection>> getScoreOfMonster(
            @PathVariable String userId) {
        UUID userUuid = UUID.fromString(userId);
        ScoreSummaryProjection scoreSummary = scoreService.calculateTotalScoreForUser(userUuid);
        return ResponseEntity.ok(AppApiResponse.<ScoreSummaryProjection>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Total score retrieved successfully")
                .data(scoreSummary)
                .build());
    }
}
