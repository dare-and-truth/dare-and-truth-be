package PNV.DareAndTruth.controller;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.projection.reminder.ReminderSummaryProjection;
import PNV.DareAndTruth.dto.request.reminder.CreateReminderRequest;
import PNV.DareAndTruth.dto.request.reminder.UpdateReminderRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.hashtag.HashtagForDoChallengeResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReminderController {
    JwtService jwtService;
    ReminderService reminderService;

    @Operation(
            summary = "Create new reminder",
            description = "Create a new reminder by providing valid reminder details.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Create reminder successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Create reminder successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1028, \"status\": \"fail\", \"message\": \"Title is required\"}")
                                        })),
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
    @PostMapping()
    public ResponseEntity<AppApiResponse<Void>> createReminder(
            @RequestBody @Valid CreateReminderRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        reminderService.createNewReminder(request, userEmail);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("New reminder created successfully")
                        .build());
    }

    @Operation(
            summary = "Get all reminders by day and user",
            description = "Retrieve all reminders for a specific user on a given day.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Reminders retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            """
																	{
																	"code": 1000,
																	"status": "success",
																	"message": "Reminders are getting successfully",
																	"data": [
																		{
																		"title": null,
																		"id": "e56a79ad-f0fe-456f-81e6-1732512f599b",
																		"userId": "2d76e0be-e529-48aa-b4a5-6ca3b43e7717",
																		"startDate": "2025-03-01",
																		"endDate": "2025-03-27",
																		"reminderContent": "Time to crush your # dodo challenge! 🏃‍♂️💨 Head to DoDo and let is make it happen! 🚀",
																		"hashtag": "dodo",
																		"startTime": null,
																		"reminderTime": "10:00:00",
																		"endTime": null
																		},
																		{
																		"title": null,
																		"id": "503046ce-7558-460d-9117-d2fefdd7b4b4",
																		"userId": "2d76e0be-e529-48aa-b4a5-6ca3b43e7717",
																		"startDate": "2025-03-01",
																		"endDate": "2025-03-20",
																		"reminderContent": "Time to crush your # check challenge! 🏃‍♂️💨 Head to DoDo and let is make it happen! 🚀",
																		"hashtag": "check",
																		"startTime": null,
																		"reminderTime": "10:00:00",
																		"endTime": null
																		}
																	]
																	}
																	""")
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1001, \"status\": \"fail\", \"message\": \"User not found\"}")
                                        })),
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
    @GetMapping()
    public ResponseEntity<AppApiResponse<List<ReminderSummaryProjection>>> getAllReminderByDate(
            @RequestParam String date, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<ReminderSummaryProjection> reminders = reminderService.getRemindersByDayAndUserId(date, userEmail);
        return ResponseEntity.status(200)
                .body(AppApiResponse.<List<ReminderSummaryProjection>>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .data(reminders)
                        .message("Reminders are getting successfully")
                        .build());
    }

    @Operation(summary = "Update a reminder", description = "Update an existing reminder with provided details.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Reminder updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Reminder updated successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "Reminder not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1003, \"status\": \"fail\", \"message\": \"Reminder not found\"}")
                                        })),
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
    @PatchMapping("/{reminderId}")
    public ResponseEntity<AppApiResponse<Void>> updateReminder(
            @PathVariable String reminderId,
            @RequestBody @Valid UpdateReminderRequest request,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        reminderService.updateReminder(UUID.fromString(reminderId), request, userEmail);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Reminder updated successfully")
                .build());
    }

    @Operation(summary = "Delete a reminder", description = "Delete a reminder with its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Reminder deleted successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Reminder deleted successfully\"}")
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "Reminder not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1003, \"status\": \"fail\", \"message\": \"Reminder not found\"}")
                                        })),
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
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<AppApiResponse<Void>> deleteReminder(
            @PathVariable String reminderId, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        reminderService.deleteReminder(UUID.fromString(reminderId), userEmail);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Reminder deleted successfully")
                .build());
    }

    @Operation(
            summary = "Get hashtags to do challenges",
            description = "Get hashtags to do challenges relies on user, start date, and end date")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Hashtags retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            """
														{
															"code": 1000,
															"status": "success",
															"message": "Hashtags retrieved successfully",
															"data": [
																{
																		"hashtag": "check",
																		"did": false
																		},
																		{
																		"hashtag": "dodo",
																		"did": false
																		}
															]
														}
														""")
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1001, \"status\": \"fail\", \"message\": \"User not found\"}")
                                        })),
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
    @GetMapping("/hashtags")
    public ResponseEntity<AppApiResponse<List<HashtagForDoChallengeResponse>>> getHashtags(
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<HashtagForDoChallengeResponse> hashtags = reminderService.getHashtagsForUserToday(userEmail);
        return ResponseEntity.status(200)
                .body(AppApiResponse.<List<HashtagForDoChallengeResponse>>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .data(hashtags)
                        .message("Hashtags retrieved successfully")
                        .build());
    }
}
