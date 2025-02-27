package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.projection.reminder.ReminderSummaryProjection;
import PNV.DareAndTruth.dto.request.reminder.CreateReminderRequest;
import PNV.DareAndTruth.dto.request.reminder.UpdateReminderRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.ReminderService;
import PNV.DareAndTruth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReminderController {
    JwtService jwtService;
    ReminderService reminderService;

    @Operation(summary = "Create new reminder", description = "Create a new reminder by providing valid reminder details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Create reminder successfully", content = @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Create reminder successfully\"}")}
            )),
            @ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1028, \"status\": \"fail\", \"message\": \"Title is required\"}")}
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")
            ))
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

    @Operation(summary = "Get all reminders by day and user", description = "Retrieve all reminders for a specific user on a given day.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminders retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(value = "[{\"id\": \"550e8400-e29b-41d4-a716-446655440000\", \"title\": \"Team Meeting\", \"hashtag\": \"#meeting\", " +
                                    "\"startDate\": \"2025-03-01\", \"endDate\": \"2025-03-01\", \"reminderContent\": \"Prepare slides\", " +
                                    "\"reminderTime\": \"08:00:00\", \"startTime\": \"09:00:00\", \"endTime\": \"10:00:00\", " +
                                    "\"userId\": \"123e4567-e89b-12d3-a456-426614174000\"}]")
                    }
            )),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1001, \"status\": \"fail\", \"message\": \"User not found\"}")}
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")
            ))
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder updated successfully", content =
            @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Reminder updated successfully\"}")}
            )),
            @ApiResponse(responseCode = "404", description = "Reminder not found", content =
            @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1003, \"status\": \"fail\", \"message\": \"Reminder not found\"}")}
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")
            ))
    })
    @PatchMapping("/{reminderId}")
    public ResponseEntity<AppApiResponse<Void>> updateReminder(
            @PathVariable String reminderId,
            @RequestBody @Valid UpdateReminderRequest request,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        reminderService.updateReminder(UUID.fromString(reminderId), request, userEmail);
        return ResponseEntity.ok(
                AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Reminder updated successfully")
                        .build());
    }

    @Operation(summary = "Delete a reminder", description = "Delete a reminder with its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder deleted successfully", content = @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Reminder deleted successfully\"}")}
            )),
            @ApiResponse(responseCode = "404", description = "Reminder not found", content = @Content(
                    mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"code\": 1003, \"status\": \"fail\", \"message\": \"Reminder not found\"}")}
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\": 1001, \"status\": \"fail\", \"message\": \"An unexpected error occurred\"}")
            ))
    })
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<AppApiResponse<Void>> deleteReminder(
            @PathVariable String reminderId,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        reminderService.deleteReminder(UUID.fromString(reminderId), userEmail);
        return ResponseEntity.ok(
                AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Reminder deleted successfully")
                        .build());
    }
}
