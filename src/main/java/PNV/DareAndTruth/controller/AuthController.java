package PNV.DareAndTruth.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.response.ApiResponse;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Object>> signUp(@RequestBody @Valid SignUpRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok(
                ApiResponse.builder().code(1000).status(ApiStatus.SUCCESS).build());
    }
}
