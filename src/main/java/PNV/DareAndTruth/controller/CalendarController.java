package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.projection.calendar.CalendarSummaryProjection;
import PNV.DareAndTruth.dto.request.calendar.CreateCalendarRequest;
import PNV.DareAndTruth.dto.request.calendar.UpdateCalendarRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.CalendarService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CalendarController {
    JwtService jwtService;
    CalendarService calendarService;

    @Operation(
            summary = "Create new calendar",
            description = "Create a new calendar by providing valid calendar details."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Create calendar successfully",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Create calendar successfully\"}")
                                    }
                                    )
                    ),
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
                                    }
                                    )
                    ),
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
                                    """)
                            )
                    )
            })
    @PostMapping()
    public ResponseEntity<AppApiResponse<Void>> createCalendar(@RequestBody @Valid CreateCalendarRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        calendarService.createNewCalendar(request,userEmail);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("New calendar created successfully")
                .build());
    }

    @Operation(
            summary = "Get all calendars by day and user",
            description = "Retrieve all calendars (including challenges and events) for a specific user on a given day."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calendars retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = "[{\"id\": \"550e8400-e29b-41d4-a716-446655440000\", \"title\": \"Team Meeting\", \"startDate\": \"2025-03-01\", \"endDate\": \"2025-03-01\", \"startTime\": \"09:00:00\", \"endTime\": \"10:00:00\", \"repeatType\": 1, \"isChallenge\": false, \"userId\": \"123e4567-e89b-12d3-a456-426614174000\"}]"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = "{\"code\": 1001, \"status\": \"fail\", \"message\": \"User not found\"}")}
                            )
                    ),
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
                                    """)
                            )
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<AppApiResponse<List<CalendarSummaryProjection>>> getAllCalendarByDate(@RequestParam String date, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        List<CalendarSummaryProjection> calendars =  calendarService.getCalendarsByDayAndUserId(date,userEmail);
        return ResponseEntity.status(200)
                .body(AppApiResponse.<List<CalendarSummaryProjection>>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .data(calendars)
                        .message("Calendar is getting successfully")
                        .build());
    }

    @Operation(
            summary = "Update a calendar",
            description = "Update an existing calendar with provided details."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calendar updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Calendar updated successfully\"}")}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Calendar not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = "{\"code\": 1003, \"status\": \"fail\", \"message\": \"Calendar not found\"}")}
                            )
                    ),
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
                                    """)
                            )
                    )
            }
    )
    @PatchMapping("/{calendarId}")
    public ResponseEntity<AppApiResponse<Void>> updateCalendar(
            @PathVariable String calendarId,
            @RequestBody @Valid UpdateCalendarRequest request,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        calendarService.updateCalendar(UUID.fromString(calendarId), request, userEmail);
        return ResponseEntity.ok(
                AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Calendar updated successfully")
                        .build());
    }

    @Operation(
            summary = "Delete a calendar",
            description = "Delete a calendar with its ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calendar deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Calendar deleted successfully\"}")}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Calendar not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = "{\"code\": 1003, \"status\": \"fail\", \"message\": \"Calendar not found\"}")}
                            )
                    ),
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
                                    """)
                            )
                    )
            }
    )
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<AppApiResponse<Void>> deleteCalendar(
            @PathVariable UUID calendarId,
            HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String userEmail = jwtService.extractEmail(token);
        calendarService.deleteCalendar(calendarId, userEmail);
        return ResponseEntity.ok(
                AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Calendar deleted successfully")
                        .build());
    }
}
