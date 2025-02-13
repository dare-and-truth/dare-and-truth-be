package PNV.DareAndTruth.controller;

import java.util.List;
import java.util.UUID;

import PNV.DareAndTruth.dto.request.badge.UpdateBadgeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                                "{\"code\": 1000,\"status\": \"success\",\"message\": \"Badges retrieved successfully\",\"data\": [{\"id\":\"123e4567-e89b-12d3-a456-426614174000\", \"name\":\"Gold Badge\", \"description\":\"Awarded for excellence\", \"createdAt\":\"2025-01-01T12:00:00Z\"}]}"))),
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
                                                                "{\"code\": 1000,\"status\": \"success\",\"message\": \"Badge retrieved successfully\",\"data\": {\"id\":\"123e4567-e89b-12d3-a456-426614174000\", \"name\":\"Gold Badge\", \"description\":\"Awarded for excellence\", \"createdAt\":\"2025-01-01T12:00:00Z\"}}"))),
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
            @PathVariable String id,
            @RequestBody @Valid UpdateBadgeRequest request) {
        badgeService.updateBadge(UUID.fromString(id), request);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Badge updated successfully")
                .build());
    }
}
