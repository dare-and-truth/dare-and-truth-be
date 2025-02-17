package PNV.DareAndTruth.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.request.badge.CreateBadgeRequest;
import PNV.DareAndTruth.dto.request.badge.UpdateBadgeRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.entity.Badge;
import PNV.DareAndTruth.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadgeController {
    BadgeService badgeService;

    @Operation(summary = "Create a new badge", description = "Create a new badge with provided details")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Badge created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Create badge successfully\", "
                                                                + "\"data\": {"
                                                                + "\"id\": \"a1b2c3d4-5678-90ef-ghij-klmnopqrstuv\", "
                                                                + "\"title\": \"Gold Badge\", "
                                                                + "\"description\": \"Awarded for excellence\", "
                                                                + "\"image\": \"https://example.com/badge.png\", "
                                                                + "\"badgeCriteria\": 10, "
                                                                + "\"points\": 100, "
                                                                + "\"startDay\": \"2025-01-01\", "
                                                                + "\"endDay\": \"2025-12-31\", "
                                                                + "\"isActive\": true, "
                                                                + "} "
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1023,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"Badge title already exists\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Invalid date range",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1024,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"Start day should be less than end day\""
                                                                + "}")))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> createBadge(@RequestBody @Valid CreateBadgeRequest request) {
        badgeService.createBadge(request);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Create badge successfully")
                        .build());
    }

    @Operation(summary = "Get all badges", description = "Retrieve a list of all available badges")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Badges retrieved successfully",
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
																"message": "Badges retrieved successfully",
																"data": [
																	{
																	"id": "847cb87c-307b-4c28-abb2-524d1f711c5a",
																	"createdBy": null,
																	"updatedBy": null,
																	"createdAt": "2025-02-13T09:03:49.180+00:00",
																	"updatedAt": "2025-02-13T09:03:49.180+00:00",
																	"title": "Gold Badge dscc",
																	"image": "https://example.com/badge.png",
																	"description": "Awarded for excellence",
																	"badgeCriteria": 10,
																	"points": 0,
																	"startDay": "2025-01-01",
																	"endDay": "2025-12-31",
																	"isActive": true,
																	"isDeleted": false
																	}
																]
																}
																"""))),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping
    public ResponseEntity<AppApiResponse<List<Badge>>> getAllBadges() {
        List<Badge> allBadges = badgeService.getAllBadges();
        return ResponseEntity.ok(AppApiResponse.<List<Badge>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Badges retrieved successfully")
                .data(allBadges)
                .build());
    }

    @Operation(summary = "Get badge by ID", description = "Retrieve a badge by its unique identifier")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Badge retrieved successfully",
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
																"message": "Badges retrieved successfully",
																"data": [
																	{
																	"id": "847cb87c-307b-4c28-abb2-524d1f711c5a",
																	"createdBy": null,
																	"updatedBy": null,
																	"createdAt": "2025-02-13T09:03:49.180+00:00",
																	"updatedAt": "2025-02-13T09:03:49.180+00:00",
																	"title": "Gold Badge dscc",
																	"image": "https://example.com/badge.png",
																	"description": "Awarded for excellence",
																	"badgeCriteria": 10,
																	"points": 0,
																	"startDay": "2025-01-01",
																	"endDay": "2025-12-31",
																	"isActive": true,
																	"isDeleted": false
																	}
																]
																}
																"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Badge not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1016,\"status\": \"fail\",\"message\": \"Badge not found\"}")))
            })
    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<Badge>> getBadgeById(@PathVariable String id) {
        Badge badge = badgeService.getBadgeById(UUID.fromString(id));
        return ResponseEntity.ok(AppApiResponse.<Badge>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Badge retrieved successfully")
                .data(badge)
                .build());
    }

    @Operation(summary = "Update an existing badge", description = "Update a badge with new details")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Badge updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1000,"
                                                                + "\"status\": \"success\","
                                                                + "\"message\": \"Badge updated successfully\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Badge not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1016,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"Badge not found\""
                                                                + "}"))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value = "{" + "\"code\": 1023,"
                                                                + "\"status\": \"fail\","
                                                                + "\"message\": \"Invalid request parameters\""
                                                                + "}")))
            })
    @PatchMapping("/{id}")
    public ResponseEntity<AppApiResponse<Void>> updateBadge(
            @PathVariable String id, @RequestBody @Valid UpdateBadgeRequest request) {
        badgeService.updateBadge(UUID.fromString(id), request);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Badge updated successfully")
                .build());
    }

    @Operation(summary = "Delete badge", description = "Mark a badge as deleted (soft delete)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Badge deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 1000, \"status\": \"success\", \"message\": \"Badge deleted successfully\"}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Badge not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 1016, \"status\": \"fail\", \"message\": \"Badge not found\"}"
                                    )
                            )
                    )
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<AppApiResponse<Void>> deleteBadge(@PathVariable String id) {
        badgeService.deleteBadge(UUID.fromString(id));
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Badge deleted successfully")
                .build());
    }
}
