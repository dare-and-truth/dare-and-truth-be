package PNV.DareAndTruth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.ranking.UserRankingResponse;
import PNV.DareAndTruth.dto.response.ranking.UserRankingWithScoreResponse;
import PNV.DareAndTruth.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RankingController {
    RankingService rankingService;

    @Operation(summary = "Get ranking of challenge", description = "Retrieve a list of users in ranking of challenge")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Users retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Users retrieved successfully\", "
                                                                + "\"data\": [ "
                                                                + "{"
                                                                + "\"userId\": \"08b8f6b9-3741-4eb0-a593-f8b46ba84d52\","
                                                                + "\"username\": \"mai oc0\","
                                                                + "\"avatarURL\": null,"
                                                                + "\"totalLikes\": 4,"
                                                                + "\"rank\": 1"
                                                                + "}"
                                                                + "] "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<AppApiResponse<List<UserRankingResponse>>> getRankingOfChallenge(
            @PathVariable String challengeId) {
        List<UserRankingResponse> rankings = rankingService.getRankingOfChallenge(challengeId);
        return ResponseEntity.ok(AppApiResponse.<List<UserRankingResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Ranking retrieved successfully")
                .data(rankings)
                .build());
    }

    @Operation(summary = "Get ranking of challenge", description = "Retrieve a list of users in ranking of challenge")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ranking retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Ranking retrieved successfully\", "
                                                                + "\"data\": [ "
                                                                + "{"
                                                                + "\"userId\": \"08b8f6b9-3741-4eb0-a593-f8b46ba84d52\","
                                                                + "\"username\": \"mai oc0\","
                                                                + "\"avatarURL\": null,"
                                                                + "\"totalScore\": 4,"
                                                                + "\"rank\": 1"
                                                                + "}"
                                                                + "] "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping()
    public ResponseEntity<AppApiResponse<List<UserRankingWithScoreResponse>>> getRankingOfServer() {
        List<UserRankingWithScoreResponse> rankings = rankingService.getRankingOfServer();
        return ResponseEntity.ok(AppApiResponse.<List<UserRankingWithScoreResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Server Ranking retrieved successfully")
                .data(rankings)
                .build());
    }
}
