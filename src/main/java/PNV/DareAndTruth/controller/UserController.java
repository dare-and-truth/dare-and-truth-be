package PNV.DareAndTruth.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping
    public ResponseEntity<AppApiResponse<List<User>>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(AppApiResponse.<List<User>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Users retrieved successfully")
                .data(allUsers)
                .build());
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<User>> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(AppApiResponse.<User>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @Operation(summary = "Update user", description = "Update user details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AppApiResponse<Void>> updateUser(
            @PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(
                AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("User updated successfully")
                        .build());
    }

    @Operation(summary = "Delete user", description = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
