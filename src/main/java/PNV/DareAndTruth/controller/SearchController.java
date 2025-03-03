package PNV.DareAndTruth.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import PNV.DareAndTruth.dto.response.user.UserWithRequestsResponse;
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

    @Operation(
            summary = "Search challenges",
            description = "Retrieve a list of challenges matching the keyword in feed format")
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
															"message": "Challenges retrieved successfully",
															"data": [
																{
																	"id": "8a761752-0d00-444b-bf01-aaa594349896",
																	"type": "challenge",
																	"hashtag": "UUUID",
																	"content": "No content",
																	"mediaUrl": "https://wtxaejtlgnodidmnasia.supabase.co/storage/v1/object/public/uploads/dd7cbd4f-b5c5-43ca-baad-6193c765dbcf.jpg",
																	"startDate": "2025-02-22",
																	"endDate": "2025-02-23",
																	"createdAt": "2025-02-22T20:22:00.583268",
																	"userId": "08b8f6b9-3741-4eb0-a593-f8b46ba84d52",
																	"username": "MaiHoThioc0",
																	"likeCount": 0,
																	"commentCount": 0,
																	"isLiked": false,
																	"isJoined": false
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
    public ResponseEntity<AppApiResponse<List<GetFeedResponse>>> searchChallenges(
            @RequestParam String keyword, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<GetFeedResponse> challenges = searchService.searchChallenges(keyword, userEmail);
        return ResponseEntity.ok(AppApiResponse.<List<GetFeedResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Challenges retrieved successfully")
                .data(challenges)
                .build());
    }

    @Operation(
            summary = "Search users",
            description = "Retrieve a list of users matching the keyword with friend requests")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Users retrieved successfully with friend requests",
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
																	"user": {
																		"id": "05832af0-cc11-403b-a318-d26f9ee81ecd",
																		"username": "nguyenmaioc0@gmail2.comnguyenmaioc0@gmail2.com"
																	},
																	"requests": [
																		{
																			"id": "96a129ec-eb67-44fe-95c1-f6cb88bf8652",
																			"followedAt": "2025-03-01T16:18:30.017525",
																			"acceptedAt": "2025-03-01T19:54:47.121874",
																			"user": {
																				"id": "08b8f6b9-3741-4eb0-a593-f8b46ba84d52",
																				"username": "MaiHoThioc0"
																			},
																			"isAccepted": false,
																			"follower": {
																				"id": "05832af0-cc11-403b-a318-d26f9ee81ecd",
																				"username": "nguyenmaioc0@gmail2.comnguyenmaioc0@gmail2.com"
																			}
																		}
																	]
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
    public ResponseEntity<AppApiResponse<List<UserWithRequestsResponse>>> searchUsers(
            @RequestParam String keyword, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<UserWithRequestsResponse> users = searchService.searchUsers(keyword, userEmail);
        return ResponseEntity.ok(AppApiResponse.<List<UserWithRequestsResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }
}
