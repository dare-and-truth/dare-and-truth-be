package PNV.DareAndTruth.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import PNV.DareAndTruth.service.FeedService;
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
@RequestMapping("/feeds")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {
    FeedService feedService;
    JwtService jwtService;

    @Operation(
            summary = "Get feeds including posts and challenge",
            description = "Retrieve a list of challenges including posts and challenge with pagination")
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
																"id": "e3196817-5751-4359-9f3e-dcafef4f2b16",
																"hashtag": "LearningChallenge",
																"content": "Learn new technologies to grow up yourself !!!",
																"mediaUrl": "https://ldzbpqvspnjrhgfgigev.supabase.co/storage/v1/object/public/uploads/f20fa22c-9327-42bc-b21a-8f43aa9b3fd8.png",
																"startDate": "2025-02-22",
																"endDate": "2025-02-28",
																"createdAt": "2025-02-22T12:31:41.338293",
																"userId": "2d76e0be-e529-48aa-b4a5-6ca3b43e7717",
																"username": "Admin",
																"likeCount": 0,
																"commentCount": 1,
																"liked": false
																}
															]
														}
														"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping
    public ResponseEntity<AppApiResponse<List<GetFeedResponse>>> getFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);

        List<GetFeedResponse> feed = feedService.getFeed(page, size, userEmail);
        return ResponseEntity.status(200)
                .body(AppApiResponse.<List<GetFeedResponse>>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .data(feed)
                        .message("Feed retrieved successfully")
                        .build());
    }

	@GetMapping({"/{userId}","/"})
	public ResponseEntity<AppApiResponse<List<GetFeedResponse>>> getChallengeAndPostByUser(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "challenge") String type,
			@PathVariable(required = false) String userId,
			HttpServletRequest httpServletRequest) {
		String token = jwtService.extractTokenFromHeader(httpServletRequest);
		String userEmail = jwtService.extractEmail(token);

		List<GetFeedResponse> feed = feedService.getFeedByUser(userId, type,page, size, userEmail);
		return ResponseEntity.status(200)
				.body(AppApiResponse.<List<GetFeedResponse>>builder()
						.code(1000)
						.status(ApiStatus.SUCCESS)
						.data(feed)
						.message("Feed retrieved successfully")
						.build());
	}
}
