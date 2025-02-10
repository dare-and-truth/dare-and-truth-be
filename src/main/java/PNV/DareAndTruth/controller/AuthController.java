package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.request.auth.LoginRequest;
import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.auth.LoginResponse;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.UserRepository;
import PNV.DareAndTruth.security.JwtTokenProvider;
import PNV.DareAndTruth.security.JwtUtil;
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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    UserService userService;
    JwtTokenProvider jwtTokenProvider;
    JwtUtil jwtUtil;
    UserRepository userRepository;

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
        userService.createUser(request);
        return ResponseEntity.status(201).body(
                AppApiResponse.builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Sign up successfully")
                        .build()
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AppApiResponse<Object>> login(@RequestBody LoginRequest request) {
        var authResult = userService.authenticateUser(request);

        String accessToken = (String) authResult.get("access_token");
        String refreshToken = (String) authResult.get("refresh_token");
        Object userInfo = authResult.get("user");

        return ResponseEntity.ok(
                AppApiResponse.builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Sign In successful. Welcome back!")
                        .data(new LoginResponse(accessToken, refreshToken, userInfo))
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
        }

        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken()) || !jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }

        String newAccessToken = jwtTokenProvider.createToken(user.getEmail(), user.getIsAdmin() ? "admin" : "user", false);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", newAccessToken);
        response.put("refresh_token", refreshToken);
        response.put("user", Map.of("id", user.getId(), "username", user.getUsername()));

        return ResponseEntity.ok(
                AppApiResponse.builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Refresh token successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (jwtTokenProvider.isTokenInvalid(token)) {
            return ResponseEntity.badRequest().body("Token has been disabled!");
        }

        String email = jwtTokenProvider.extractEmail(token);

        jwtTokenProvider.invalidateToken(token);

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });

        return ResponseEntity.ok(AppApiResponse.builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Logout successfully")
                .build());
    }
}
