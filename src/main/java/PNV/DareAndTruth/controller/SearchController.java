package PNV.DareAndTruth.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdAndUsernameProjection;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchController {
    SearchService searchService;
    JwtService jwtService;

    @Operation(summary = "Search challenges", description = "Retrieve a list of challenges matching the keyword")
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
																				"id": "123e4567-e89b-12d3-a456-426614174000",
																				"hashtag": "#fun",
																				"content": "This is a fun challenge!",
																				"mediaUrl": "https://example.com/media.jpg",
																				"startDate": "2025-01-01",
																				"endDate": "2025-01-31",
																				"isActive": true,
																				"createdAt": "2025-01-01T10:00:00",
																				"user": {
																					"id": "321e4567-e89b-12d3-a456-426614174001",
																					"username": "john_doe"
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
    @GetMapping("/challenge")
    public ResponseEntity<AppApiResponse<List<ChallengeSummaryProjection>>> searchChallenges(
            @RequestParam String keyword) {
        List<ChallengeSummaryProjection> challenge = searchService.searchChallenges(keyword);
        return ResponseEntity.ok(AppApiResponse.<List<ChallengeSummaryProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Challenge retrieved successfully")
                .data(challenge)
                .build());
    }

    @Operation(summary = "Search users", description = "Retrieve a list of users matching the keyword")
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
                                                        value =
                                                                """
																	{
																		"code": 1000,
																		"status": "success",
																		"message": "Users retrieved successfully",
																		"data": [
																			{
																				"id": "321e4567-e89b-12d3-a456-426614174001",
																				"username": "john_doe"
																			},
																			{
																				"id": "421e4567-e89b-12d3-a456-426614174002",
																				"username": "jane_doe"
																			}
																		]
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/user")
    public ResponseEntity<AppApiResponse<List<UserWithIdAndUsernameProjection>>> searchUsers(
            @RequestParam String keyword, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<UserWithIdAndUsernameProjection> user = searchService.searchUsers(keyword, userEmail);
        return ResponseEntity.ok(AppApiResponse.<List<UserWithIdAndUsernameProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }
}
