package PNV.DareAndTruth.controller;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.request.FriendDetailProjection;
import PNV.DareAndTruth.dto.request.request.CreateRequestRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController {
    RequestService requestService;
    JwtService jwtService;

    @Operation(summary = "Create a new friend request", description = "Creates a new friend request between two users.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Request created successfully",
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
																"message": "Create request successfully"
																}
																"""))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																{
																"code": 1026,
																"status": "fail",
																"message": "User ID is required"
																}
																"""))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Friend request already exists",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																{
																"code": 1049,
																"status": "fail",
																"message": "Friend request already exists"
																}
																"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
														{
														"code": 1001,
														"status": "fail",
														"message": "An unexpected error occurred"
														}
														""")))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> createRequest(
            @RequestBody @Valid CreateRequestRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.createRequest(request, userEmail);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Create request successfully")
                        .build());
    }

    @Operation(
            summary = "Get all friend requests",
            description = "Retrieves all incoming and outgoing friend requests for a user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully fetched requests",
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
																"message": "Fetched requests successfully",
																"data": [
																	{
																	id:"UUID123"
																	"followerId": "UUID456",
																	"followedAt": "2025-02-17T12:30:00",
																	"isAccepted": false
																	}
																]
																}
																"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																{
																"code": 1006,
																"status": "fail",
																"message": "User not found"
																}
																"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
												{
												"code": 1001,
												"status": "fail",
												"message": "An unexpected error occurred"
												}
												""")))
            })
    @GetMapping()
    public ResponseEntity<AppApiResponse<List<FriendDetailProjection>>> getAllAddFriendRequests(
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<FriendDetailProjection> requests = requestService.getAllAddFriendRequests(userEmail);
        return ResponseEntity.ok(AppApiResponse.<List<FriendDetailProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Fetched requests successfully")
                .data(requests)
                .build());
    }

    @Operation(
            summary = "Get list of accepted friends",
            description = "Retrieves the list of friends who have accepted friend requests.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully fetched friends list",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																{
																"code": 1000,
																"status": "success",
																"message": "Fetched friends list successfully",
																"data": [
																	{
																	"userId": "UUID123",
																	"friendId": "UUID456",
																	"friendshipStartedAt": "2025-02-17T12:30:00"
																	}
																]
																}
																"""),
                                        mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																	{
																	"code": 1006,
																	"status": "fail",
																	"message": "User not found"
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
														{
														"code": 1001,
														"status": "fail",
														"message": "An unexpected error occurred"
														}
														""")))
            })
    @GetMapping("/acceptance")
    public ResponseEntity<AppApiResponse<List<FriendDetailProjection>>> getAllFriendsList(
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<FriendDetailProjection> requests = requestService.getAllFriendsList(userEmail);
        return ResponseEntity.ok(AppApiResponse.<List<FriendDetailProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Fetched requests successfully")
                .data(requests)
                .build());
    }

    @Operation(summary = "Accept a request", description = "Accept a friend request by requestId.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Request accepted successfully",
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
																	"message": "Request accepted successfully"
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Request not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																	{
																	"code": 1051,
																	"status": "fail",
																	"message": "Request not found"
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Request has already been accepted",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																	{
																	"code": 1052,
																	"status": "fail",
																	"message": "Request has already been accepted"
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
														{
														"code": 1001,
														"status": "fail",
														"message": "An unexpected error occurred"
														}
														""")))
            })
    @PatchMapping("/accept")
    public ResponseEntity<AppApiResponse<Void>> acceptRequest(
            @RequestParam String requestId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.acceptRequest(UUID.fromString(requestId), userEmail);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Request accepted successfully")
                .build());
    }

    @Operation(summary = "Reject a request", description = "Reject a friend request")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Request rejected successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Request rejected successfully\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Request not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1051, \"status\": \"fail\", \"message\": \"Request not found\"}"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
														{
														"code": 1001,
														"status": "fail",
														"message": "An unexpected error occurred"
														}
														""")))
            })
    @DeleteMapping("/reject")
    public ResponseEntity<AppApiResponse<Void>> rejectRequest(
            @RequestParam String requestId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.rejectRequest(UUID.fromString(requestId), userEmail);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Request rejected successfully")
                .build());
    }

    @Operation(summary = "Unfriend a user", description = "Removes a user from the friend list by their friendId.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Friend deleted successfully",
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
																	"message": "Friend deleted successfully"
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Friend not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																	{
																	"code": 1006,
																	"status": "fail",
																	"message": "Friend not found"
																	}
																	"""))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
																	{
																	"code": 1001,
																	"status": "fail",
																	"message": "An unexpected error occurred"
																	}
																	""")))
            })
    @DeleteMapping("/un-friend")
    public ResponseEntity<AppApiResponse<Void>> unFriend(
            HttpServletRequest httpServletRequest, @RequestParam String friendId) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.unfFriend(userEmail, UUID.fromString(friendId));
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Unfriend successfully")
                .build());
    }
}
