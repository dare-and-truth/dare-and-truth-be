package PNV.DareAndTruth.controller;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.notification.NotificationResponse;
import PNV.DareAndTruth.dto.response.notification.UnreadNotificationCountResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
    JwtService jwtService;

    @Operation(
            summary = "Get user notifications",
            description = "Retrieve paginated notifications for the current user")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notifications retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Retrieved notifications successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Failed to retrieve notifications",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1042, \"status\": \"fail\", \"message\": \"Unable to retrieve notifications\"}")
                                        }))
            })
    @GetMapping("/user/{receiverId}")
    public ResponseEntity<AppApiResponse<Page<NotificationResponse>>> getUserNotifications(
            @PathVariable String receiverId, @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotificationsForUser(receiverId, pageable);

        return ResponseEntity.ok(AppApiResponse.<Page<NotificationResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Retrieved notifications successfully")
                .data(notifications)
                .build());
    }

    @Operation(
            summary = "Get unread notifications count",
            description = "Retrieve count of unread notifications for the current user")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Unread notifications count retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Retrieved unread notifications count successfully\"}")
                                        }))
            })
    @GetMapping("/unread/count")
    public ResponseEntity<AppApiResponse<UnreadNotificationCountResponse>> getUnreadNotificationsCount(
            HttpServletRequest httpServletRequest) {
        UUID userId = jwtService.extractUserIdFromHeader(httpServletRequest);
        UnreadNotificationCountResponse unreadNotificationCountResponse =
                notificationService.countUnreadNotifications(userId);

        return ResponseEntity.ok(AppApiResponse.<UnreadNotificationCountResponse>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Retrieved unread notifications count successfully")
                .data(unreadNotificationCountResponse)
                .build());
    }

    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notification marked as read successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Marked notification as read\"}")
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Failed to mark notification as read",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1042, \"status\": \"fail\", \"message\": \"Notification not found\"}")
                                        }))
            })
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<AppApiResponse<Void>> markNotificationAsRead(@PathVariable String notificationId) {
        notificationService.markNotificationAsRead(notificationId);

        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Marked notification as read")
                .build());
    }

    @Operation(summary = "Update fcm token to push notification", description = "Update fcm token to push notification")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Update token successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Update fcm token successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Failed to update fcm token",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1042, \"status\": \"fail\", \"message\": \"User not found\"}")
                                        }))
            })
    @PostMapping("/update-fcm-token/{token}")
    public ResponseEntity<AppApiResponse<Void>> updateFcmToken(@PathVariable String token, HttpServletRequest request) {
        UUID userId = jwtService.extractUserIdFromHeader(request);
        notificationService.updateFcmToken(userId, token);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Update fcm token successfully")
                .build());
    }
}
