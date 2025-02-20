package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.request.like.LikeRequest;
import PNV.DareAndTruth.dto.request.like.UnlikeRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.LikeService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeController {
    private static final Logger log = LoggerFactory.getLogger(LikeController.class);
    LikeService likeService;
    JwtService jwtService;

    @Operation(summary = "Like feed", description = "Like challenge or post by their id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Like successfully",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Like successfully\"}")
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
                                                            "{\"code\": 1042, \"status\": \"fail\", \"message\": \"Challenge or post not found\"}")
                                    }))
            })
    @PostMapping
    public ResponseEntity<AppApiResponse<Void>> likeFeed(@Valid @RequestBody LikeRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String email = jwtService.extractEmail(token);

        likeService.likeFeed(request, email);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Like successfully")
                        .build());
    }

    @Operation(summary = "Unlike feed", description = "Unlike challenge or post by their id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Unlike successfully",
                            content =
                            @Content(
                                    mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input provided",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1042, \"status\": \"fail\", \"message\": \"Challenge or post not found\"}")
                                    }))
            })
    @DeleteMapping
    public ResponseEntity<AppApiResponse<Void>> unlikeFeed(@Valid @RequestBody UnlikeRequest request, HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromHeader(httpServletRequest);
        String email = jwtService.extractEmail(token);

        likeService.unlikeFeed(request, email);
        return ResponseEntity.status(200).body(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Unlike successfully")
                .build());
    }
}
