package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    UserService userService;

    @Operation(
            summary = "Sign up new account",
            description = "Create a new account by providing valid user details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sign up successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppApiResponse.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<AppApiResponse<Object>> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.status(201).body(
                AppApiResponse.builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Sign up successfully")
                        .build()
        );
    }
}
