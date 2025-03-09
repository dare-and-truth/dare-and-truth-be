package PNV.DareAndTruth.controller;

import java.util.Set;

import PNV.DareAndTruth.dto.response.user.UserWithTypeOfRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.user.UserSummaryProjection;
import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    JwtService jwtService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
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
                                                                + "{ "
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"username\": \"nhat\", "
                                                                + "\"email\": \"nhat@gmail.com\", "
                                                                + "\"isActive\": true "
                                                                + "}, "
                                                                + "{ "
                                                                + "\"id\": \"480e9041-35fe-4992-96f9-83cc087c4c07\", "
                                                                + "\"username\": \"123123\", "
                                                                + "\"email\": \"nhat2@gmail.com\", "
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
    public ResponseEntity<AppApiResponse<Set<UserSummaryProjection>>> getAllUsers() {
        Set<UserSummaryProjection> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(AppApiResponse.<Set<UserSummaryProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Users retrieved successfully")
                .data(allUsers)
                .build());
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"User retrieved successfully\", "
                                                                + "\"data\": {"
                                                                + "\"id\": \"fd609f00-90ef-435f-965c-88884767fbbf\", "
                                                                + "\"username\": \"nhat\", "
                                                                + "\"email\": \"nhat@gmail.com\", "
                                                                + "\"createdAt\": \"2025-01-23T16:24:05.995+00:00\", "
                                                                + "\"updatedAt\": \"2025-01-23T16:24:05.995+00:00\", "
                                                                + "\"isActive\": true "
                                                                + "} "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1006,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"User does not find\""
                                                                + "}")))
            })
    @GetMapping("/{userId}")
    public ResponseEntity<AppApiResponse<Object>> getUserById(
            @PathVariable String userId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        Object user = userService.getUserDetailById(userId, userEmail);
        return ResponseEntity.ok(AppApiResponse.<Object>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @Operation(summary = "Update user", description = "Update user details by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User updated successfully",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1004,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"Username must be at least 3 characters long\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1006,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"User does not find\""
                                                                + "}")))
            })
    @PatchMapping("/{userId}")
    public ResponseEntity<AppApiResponse<Void>> updateUser(
            @PathVariable String userId,
            @RequestBody @Valid UpdateUserRequest request,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        userService.updateUser(userEmail, request, userId);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User updated successfully")
                .build());
    }

    @Operation(summary = "Delete user", description = "Delete a user by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                @ApiResponse(responseCode = "404", description = "User not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get friend request type by user",
            description = "Returns the type of friend request for a target user based on the relationship between the logged-in user and the target user. \n\n" +
                    "- **NeedAccept:** The logged-in user is the recipient and has pending friend requests.\n" +
                    "- **Friend:** The friend request is accepted (either the logged-in user is the recipient or the sender).\n" +
                    "- **WaitingForAccept:** The logged-in user is the sender and the request is still pending."+
                    "-Stranger: The logged-in user doesn't have any request to targetusser or target user doesn't have any request to the logged-in user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend request type retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n  \"code\": 1000,\n  \"status\": \"success\",\n  \"message\": \"Friend request type retrieved successfully\",\n  \"data\": { \"typeOfRequest\": \"NeedAccept\" }\n}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or no friend requests available",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("request/user/{targetUserId}")
    public ResponseEntity<AppApiResponse<UserWithTypeOfRequest>> getUserWithRequestByUserId(
            @PathVariable String targetUserId,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        UserWithTypeOfRequest typeOfRequest =  userService.getUserWithTypeOfRequest(userEmail, targetUserId);
        return ResponseEntity.ok(AppApiResponse.<UserWithTypeOfRequest>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Get type of request successfully")
                .data(typeOfRequest)
                .build());
    }
}
