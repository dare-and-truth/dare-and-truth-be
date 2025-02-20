package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.response.request.RequestResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import PNV.DareAndTruth.dto.request.request.CreateRequestRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import PNV.DareAndTruth.service.JwtService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController {
    RequestService requestService;
    JwtService jwtService;

    @Operation(summary = "Create a new request", description = "Create a new friend request between two users")
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
                                            value = "{" +
                                                    "\"code\": 1000," +
                                                    "\"status\": \"success\"," +
                                                    "\"message\": \"Create request successfully\"" +
                                                    "}"))),

                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input provided",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1026, \"status\": \"fail\", \"message\": \"User ID is required\"}"))),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity - Invalid friend request action",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1045, \"status\": \"fail\", \"message\": \"Cannot send a friend request to yourself\"}"))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1006, \"status\": \"fail\", \"message\": \"User not found\"}"))),

                    @ApiResponse(
                            responseCode = "409",
                            description = "Friend request already exists",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1041, \"status\": \"fail\", \"message\": \"Friend request already exists\"}")))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> createRequest(@RequestBody @Valid CreateRequestRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.createRequest(request,userEmail);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Create request successfully")
                        .build());
    }

    @Operation(summary = "Get all requests", description = "Retrieve all friend requests for a user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched requests",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value =
                                    "{" +
                                            "\"code\": 1000," +
                                            "\"status\": \"success\"," +
                                            "\"message\": \"Fetched requests successfully\"," +
                                            "\"data\": [{" +
                                            "\"userId\": \"UUID123\"," +
                                            "\"followerId\": \"UUID456\"," +
                                            "\"followedAt\": \"2025-02-17T12:30:00\"," +
                                            "\"isAccepted\": false" +
                                            "}]" +
                                            "}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value =
                                    "{\"code\": 1006, \"status\": \"fail\", \"message\": \"User not found\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value =
                                    "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")
                    )
            )
    })
    @GetMapping("/user")
    public ResponseEntity<AppApiResponse<List<RequestResponse>>> getAllRequests(HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<RequestResponse> requests = requestService.getAllRequests(userEmail);
        return ResponseEntity.ok(AppApiResponse.<List<RequestResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Fetched requests successfully")
                .data(requests)
                .build());
    }

    @Operation(summary = "Accept a request", description = "Accept a friend request")
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
                                            value = "{" +
                                                    "\"code\": 1000," +
                                                    "\"status\": \"success\"," +
                                                    "\"message\": \"Request accepted successfully\"" +
                                                    "}"))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request not found",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1043, \"status\": \"fail\", \"message\": \"Request not found\"}"))),

                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - Request has already been accepted",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1044, \"status\": \"fail\", \"message\": \"Request has already been accepted\"}"))),

                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")))
            })
    @PatchMapping("/accept")
    public ResponseEntity<AppApiResponse<Void>> acceptRequest(@RequestParam String requestId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.acceptRequest(UUID.fromString(requestId),userEmail);
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
                                            value = "{" +
                                                    "\"code\": 1000," +
                                                    "\"status\": \"success\"," +
                                                    "\"message\": \"Request rejected successfully\"" +
                                                    "}"))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request not found",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1043, \"status\": \"fail\", \"message\": \"Request not found\"}"))),

                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")))
            })
    @DeleteMapping("/reject")
    public ResponseEntity<AppApiResponse<Void>> rejectRequest(@RequestParam String requestId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.rejectRequest(UUID.fromString(requestId), userEmail);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Request rejected successfully")
                .build());
    }

    @Operation(summary = "Delete a friend", description = "Delete a friend from the list")
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
                                            value = "{" +
                                                    "\"code\": 1000," +
                                                    "\"status\": \"success\"," +
                                                    "\"message\": \"Friend deleted successfully\"" +
                                                    "}"))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Friend not found",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1006, \"status\": \"fail\", \"message\": \"Friend not found\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")))
            })
    @DeleteMapping("/delete")
    public ResponseEntity<AppApiResponse<Void>> deleteFriend(HttpServletRequest httpServletRequest, @RequestParam String friendId) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        requestService.deleteFriend(userEmail, UUID.fromString(friendId));
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Friend deleted successfully")
                .build());
    }
}